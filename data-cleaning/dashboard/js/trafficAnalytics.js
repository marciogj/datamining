var app = angular.module('trafficAnalytics', [
'ui.router'
]); 

var DBP_API = "http://localhost:9999/services";

var weekDayMonth = function(date) {
	//var weekDays = [ 'Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'];
	//var months = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];
	var weekDays = [ 'Sunday', 'Monday', 'Tuesday', 'wednesday', 'Thursday', 'Friday', 'Saturday'];
	var months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

	var weekDay = weekDays[date.getDay()];
	var month = months[date.getMonth()];

	return weekDay + ' ' + date.getDate() + ' de ' + month;
};

var hoursMins = function(date) {
	var time = date.toLocaleTimeString().split(":");
	return time[0] + ':' + time[1];
};

//http://stackoverflow.com/questions/16288190/angularjs-location-service-apparently-not-parsing-url
/*app.config(['$locationProvider', '$stateProvider', function($locationProvider, $stateProvider) {
	$locationProvider.html5Mode({
      enabled: true,
      requireBase: false
    });*/

app.config(function($locationProvider, $stateProvider, $urlRouterProvider) {

	console.log('###### App Config ######');


  	$stateProvider.state('vehicle', {
      	url: "/vehicle",
      	//template: "<h1>Welcome to state 1</h1>",
      	templateUrl: "views/vehicle.html",
      	//controller: 'YourCtrl'
    })
    
    .state('drivers', {
      url: "/drivers",
      templateUrl: "views/drivers.html",
      controller: 'driversCtrl',
      controllerAs: 'vm'
    })

    .state('driver-profile', {
      url: "/driver-profile?{id}&{userId}",
      templateUrl: "views/driver-profile.html",
      controller: 'driverProfileCtrl',
      controllerAs: 'vm'
    })

    .state('trajectory-telemetry', {
      url: "/trajectory-telemetry/{id}",
      templateUrl: "views/trajectory-telemetry.html",
      controller: 'trajectoryTelemetryCtrl',
      controllerAs: 'vm'
    })

    .state('trajectory-map', {
      url: "/trajectory-map/{evaluationId}?{mode}",
      templateUrl: "views/trajectory-map.html",
      controller: 'trajectoryMapCtrl',
      controllerAs: 'vm'
    })

    .state('trajectory-coordinates', {
      url: "/trajectory-coordinates/{evaluationId}",
      templateUrl: "views/trajectory-coordinates.html",
      controller: 'trajectoryCoordinatesCtrl',
      controllerAs: 'vm'
    })

    .state('trajectory-evaluation', {
      url: "/trajectory-evaluation/{id}",
      templateUrl: "views/trajectory-evaluation.html",
      controller: 'trajectoryEvaluationCtrl',
      controllerAs: 'vm'
    });

    $urlRouterProvider.otherwise("/drivers");

});

app.controller('trajectoryCoordinatesCtrl',  ['$scope','$stateParams', '$http',  function($scope , $stateParams, $http){
	var self = this;
	
	function initialize(evaluationId) {
		$http.get(DBP_API + '/summary/trajectory-evaluation/' + evaluationId + '/coordinates').success(function(data) {			
			self.trajectory = data;
			console.log(data);
        });		
    };
	
	initialize($stateParams.evaluationId);

}]);

app.controller('trajectoryMapCtrl',  ['$scope','$stateParams', '$http',  function($scope , $stateParams, $http){
	var self = this;
	self.speedColors = {};
	self.speedColors['underLimit'] = '#000000';
	self.speedColors['from10to20Limit'] = '#ff6600';
	self.speedColors['from20to50Limit'] = '#ff0000';
	self.speedColors['over50Limit'] = '#6600cc';
	self.map = undefined;
	
	
	function initialize(evaluationId) {

		var mapStyle = $stateParams.mapStyle;
		if(mapStyle === 'styled') {
			//self.map = createStyledMap(latCenter, lonCenter);
			self.map = createStyledMap();
		} else {
			//self.map = createBasicMap(latCenter, lonCenter);	
			self.map = createBasicMap();	
		}
		
		//var mapEditable = $stateParams.editable;
		//if(mapEditable === 'true') {
		//	enableEdition(self.map);
		//}
		
		requestAndDrawCoordinates(evaluationId, self.map);
		requestAndDrawImportantPlaces(evaluationId, self.map);
       
    };

    function requestAndDrawCoordinates(evaluationId, map) {
    	$http.get(DBP_API + '/summary/trajectory-evaluation/' + evaluationId + '/coordinates').success(function(data) {			
			var trajectoryMode = $stateParams.mode;
			if ( trajectoryMode == "line" || trajectoryMode == null) {
				drawPolylineTrajectory(data.coordinates, map);
			} else if (trajectoryMode == "dots") {
				drawDotsTrajectory(data.coordinates, map);
			}

			setMapCenter(data.coordinates, map);
        });	
    };

    function setMapCenter(coordinates, map) {
    	var middle = Math.floor(coordinates.length / 2);
		var latCenter = coordinates[middle].latitude;
		var lonCenter = coordinates[middle].longitude;
		map.setCenter({ lat: latCenter, lng : lonCenter, alt: 0 });
    }

    function requestAndDrawImportantPlaces(evaluationId, map) {
    	$http.get(DBP_API + '/summary/trajectory-evaluation/' + evaluationId + '/important-places-traveled').success(function(data) {
    		drawImportantPlaces(data, map);
        }).error(function(data, status, header, config) {
        	console.log('Error! Status: ' + status);
        });

    };

	function drawImportantPlaces(placeList, map) {
		placeList.forEach(function(place){
			console.log(place);
			var coordinate = { latitude: place.lat, longitude: place.lon };
			
			createMarker(coordinate, map);
			if (place.isTravelled) {
				createImportantPlaceCircle(place, map, '#f4f0c8', 100);		
			} else {
				createImportantPlaceCircle(place, map, '#bababa', 10);	
			}
			
		});
	};


    function drawPolylineTrajectory(coordinates, map) {
    	var googleCoordinates = [];

		coordinates.forEach( function(coordinate) {
          googleCoordinates.push(new google.maps.LatLng(parseFloat(coordinate.latitude), parseFloat(coordinate.longitude)));
        });

        var middle = Math.floor(coordinates.length / 2);
		var latCenter = coordinates[middle].latitude;
		var lonCenter = coordinates[middle].longitude;

		var lineCoordinatesPath = new google.maps.Polyline({
			path: googleCoordinates,
			geodesic: true,
			strokeColor: '#2E10FF',
			strokeOpacity: 1.0,
			strokeWeight: 2
		});

      	lineCoordinatesPath.setMap(map);
	};

	function drawDotsTrajectory(coordinates, map) {
		var middle = Math.floor(coordinates.length / 2);
		var latCenter = coordinates[middle].latitude;
		var lonCenter = coordinates[middle].longitude;

		coordinates.forEach( function(coordinate) {
          drawCoord(coordinate, map);
        });
	};

	function drawCoord(coordinate, map) {
		var speedLimit = coordinate.speedLimit
        var overLimitfactor = ( (coordinate.speed * 100)/speedLimit ) - 100;
        var color = getColorBySpeedFactor(overLimitfactor);

        if (coordinate.meanType === 'W') { 
        	createMarker(coordinate, map, 'images/walking-icon-orange-small.png');
		} else if (coordinate.isNoise) {
        	createMarker(coordinate, map, 'images/noise-red.png');
        } else {
        	createCoordinateCircle(coordinate, map, color);
        }

	};

	function coord2html(coordinate) {
		var html = '';
		for (var key in coordinate) {
			var value = coordinate[key];
			if (key === 'speed' || key === 'speedLimit') {	
				html += '<strong>' + key + "</strong>: " + formatDouble(value) + ' km/h<br>';
			} else if (key == 'acceleration') {	
				html += '<strong>' + key + "</strong>: " + formatDouble(value) + ' m/s²<br>';
			} else {
				html += '<strong>' + key + "</strong>: " + value + '<br>';
			}
				
		}
		return html;
	};

	function formatDouble(value) {
		return parseFloat(Math.round(value * 100) / 100).toFixed(2);
	}


	function getColorBySpeedFactor(overLimitfactor) {
		if (overLimitfactor <= 10) {
			return self.speedColors['underLimit'];
		} else if (overLimitfactor > 10 && overLimitfactor <= 20) {
			return self.speedColors['from10to20Limit'];
		} else if(overLimitfactor > 20 && overLimitfactor <= 50) {
			return self.speedColors['from20to50Limit'];
		} else if (overLimitfactor > 50) {
			return self.speedColors['over50Limit'];
		} 
		return self.speedColors['underLimit'];
	}

	function obj2html(obj) {
		var html = '';
		for (var key in obj) {
			var value = obj[key];
			html += '<strong>' + key + "</strong>: " + value + '<br>';
		}
		return html;
	}

	function createCoordinateCircle(coordinate, map, hexColor) {
		var coordHtmlDetails = coord2html(coordinate);
		createInfoCircle(coordinate, map, 0.90, 0.95, 1, hexColor, 3, coordHtmlDetails);
	}

	function createImportantPlaceCircle(place, map, hexColor, radius) {
		var coordinate = { latitude: place.lat, longitude: place.lon };
		var placeHtml = obj2html(place);
		createInfoCircle(coordinate, map, 0.6, 0.08, 1, hexColor, radius, placeHtml);
	}

	function createInfoCircle(coordinate, map, fillOpacity, strokeOpacity, strokeWeight, hexColor, radius, infoMessage) {
		var googleCoord = new google.maps.LatLng(parseFloat(coordinate.latitude), parseFloat(coordinate.longitude));
        
		var circle = new google.maps.Circle({
		      strokeColor: hexColor,
		      strokeOpacity: strokeOpacity,
		      strokeWeight: strokeWeight,
		      fillColor: hexColor,
		      fillOpacity: fillOpacity,
		      map: map,
		      center: googleCoord,
		      radius: radius
		});

		google.maps.event.addListener(circle, 'click', function(ev){
			var infoWindow = new google.maps.InfoWindow;
		    infoWindow.setPosition(circle.getCenter());
			infoWindow.setContent(infoMessage);
		    infoWindow.open(map);
		});

		return circle;
	};

	function createMarker(coordinate, map, icon) {
		var googleCoord = new google.maps.LatLng(parseFloat(coordinate.latitude), parseFloat(coordinate.longitude));
        
		var marker = new google.maps.Marker({
		    position: googleCoord,
		    icon: icon,
		    map: map
		  });

		return marker;
	};

	function createStyledMap(latCenter, lonCenter) {
		var styles = [
		  {
		  	stylers: [
        		{ hue: "#00ffe6" },
        		{ saturation: -20 }
      		]
    	  }, 
    	  {
      		featureType: "road",
      		elementType: "geometry",
      		stylers: [
        		{ lightness: 100 },
        		{ visibility: "simplified" }
      		]
    	},{
      		featureType: "road",
      		elementType: "labels",
      		stylers: [
        		{ visibility: "off" }
      		]
    	}
  		];

  		var styledMap = new google.maps.StyledMapType(styles, {name: "Styled Map"});
		var map = new google.maps.Map(document.getElementById('map-canvas'), {
			zoom: 12,
			//center: { lat: latCenter, lng : lonCenter, alt: 0 },
			mapTypeControlOptions: {
		      mapTypeIds: [google.maps.MapTypeId.ROADMAP, 'map_style']
		    }
		});
		map.mapTypes.set('map_style', styledMap);
  		map.setMapTypeId('map_style');

  		return map;
	};

	function createBasicMap(latCenter, lonCenter) {
		var map = new google.maps.Map(document.getElementById('map-canvas'), {
			zoom: 12
			//,
			//center: { lat: latCenter, lng : lonCenter, alt: 0 }
		});
		
		map.addListener('rightclick', function() {
			map.set('disableDoubleClickZoom', true);
		    updateMarkerTitle();
		    console.log('New marker label: ' + MarkerTitle.text); 
		});

		return map;
	};

	var MarkerTitle = { text: 'A'};
	function updateMarkerTitle() {
		MarkerTitle.text = nextChar(MarkerTitle.text);
	};

	//http://stackoverflow.com/questions/12504042/what-is-a-method-that-can-be-used-to-increment-letters
	function nextChar(c) {
    	return String.fromCharCode(c.charCodeAt(0) + 1);
	}

	function enableEdition(map) {
		var drawingManager = new google.maps.drawing.DrawingManager({
		    drawingMode: google.maps.drawing.OverlayType.MARKER,
		    drawingControl: true,
		    drawingControlOptions: {
		      position: google.maps.ControlPosition.TOP_CENTER,
		      drawingModes: [
		        google.maps.drawing.OverlayType.MARKER,
		        google.maps.drawing.OverlayType.CIRCLE,
		        google.maps.drawing.OverlayType.POLYGON,
		        google.maps.drawing.OverlayType.POLYLINE,
		        google.maps.drawing.OverlayType.RECTANGLE
		      ]
		    },
		    //markerOptions: {icon: 'images/beachflag.png'},

		    markerOptions: { 
		    	label: MarkerTitle,
		    	clickable: true,
		    	editable: true
		    },

		});

  		drawingManager.setMap(map);
	};


    initialize($stateParams.evaluationId);

}]);


app.controller('driversCtrl',  ['$scope','$stateParams',  function($scope , $stateParams ){
	var self = this;
		

	//moto-x
	var listAllDrivers = function() {
		return [
			{id: 'moto-x', userId: 'marcio.jasinski', name: 'Marcio Jasinski', trajectories: 745, profileIndex: 25},
			{id: '2', userId: 'anderson.torres', name: 'Anderson Torres', trajectories: 3, profileIndex: 51},
			{id: '3', userId: 'taxi.gtd8912', name: 'Taxi GTD-8912', trajectories: 1, profileIndex: 75}
		];
	};

	self.drivers = listAllDrivers();
}]);

app.controller('driverProfileCtrl',  ['$scope','$stateParams', '$http',  function($scope , $stateParams, $http){
	var self = this;
	self.riskAlerts = 3;

	function requestAllTrajectoryById(id) {

		$http.get(DBP_API + '/summary/trajectories/' + id).success(function(data) {			
			//http://stackoverflow.com/questions/29803045/how-to-clear-an-angular-array
			self.trajectories = [];
			self.trajectories = data;
        });
	};


	function requestDriverProfile(driverId) {

		$http.get(DBP_API + '/driver-profile/' + driverId).success(function(data) {			
			self.driverProfile = data;
			console.log(data);
        });
	}


	requestAllTrajectoryById($stateParams.id);
	requestDriverProfile($stateParams.userId);

}]);


app.controller('trajectoryEvaluationCtrl',  ['$scope','$stateParams', '$http', function($scope , $stateParams, $http){
	var self = this;
	console.log('trajectoryEvaluationCtrl initialized ' +  $stateParams.id);


	var loadEvaluation = function(evaluationId) {
		console.log("Loading Evaluation: " + evaluationId);
		$http.get(DBP_API + '/summary/trajectory-evaluation/' + evaluationId)		
          .success(function(data) {
          	
          	var evaluation = data;
			console.log(data.streets);
			evaluation.startDateStr =  weekDayMonth(new Date(data.startDateTime));
			evaluation.startDateTime = new Date(data.startDateTime);

			evaluation.endDateTime = new Date(data.endDateTime);
			evaluation.timeInterval = hoursMins(new Date(data.startDateTime)) + ' - ' + hoursMins(new Date(data.endDateTime));
			evaluation.hourClassification = data.hourClassification;
			if (evaluation.mainStreet.length == 0) {
				evaluation.mainStreet = "-";
			}

			console.log(evaluation);
			self.evaluation = evaluation;
          
          }).error(function(data, status, header, config) {
          	console.log('Error! Status: ' + status + ' Header: ');
          });
	};

	loadEvaluation($stateParams.id);

}]);

app.controller('trajectoryTelemetryCtrl',  ['$scope','$stateParams', '$http',  function($scope , $stateParams, $http){
	var self = this;
	console.log('trajectoryTelemetryCtrl initialized ' +  $stateParams.id);

	var loadTelemetry = function(trajectoryId) {
		console.log("loading telemetry ... " + trajectoryId);
		$http.get(DBP_API + '/summary/trajectories/telemetry/speed/' + trajectoryId).		
          success(function(data) {
          	console.log("Updating chart");
			var labelValues = Array.apply(null, {length: data.speedList.length}).map(Number.call, Number);
          	updateChart(labelValues, data.speedList);
          });
	};


	var updateChart = function(labelValues, dataValues) {
		 var data = {
			labels: labelValues,
            datasets: [
	            {
	                label: "Telemetria",
	                fillColor: "rgba(220,220,220,0.5)",
	                strokeColor: "rgba(220,220,220,0.8)",
	                highlightFill: "rgba(220,220,220,0.75)",
	                highlightStroke: "rgba(220,220,220,1)",
	                data: dataValues
	            },
			]};

		var ctx = document.getElementById("telemetry").getContext("2d");

		var myBarChart = new Chart(ctx, {
		    type: 'bar',
		    data: data,
		    options: {
        		scales: {
            		xAxes: [{
                		display: false
            		}]
        		}
    		}
		});
		
	
	};

	loadTelemetry($stateParams.id);

}]);



/*
app.controller('stateCtrl',  ['$scope','$stateParams',  function($scope , $stateParams ){

	var print = function() {
		console.log('StateParams id:' + $stateParams.id);
	};


	print();

}]);

*/


app.controller('vehicleDashboardCtrl', function($scope, $timeout, $http) {
    $scope.trajectoryTime= "6:28";
    $scope.totalDistance= "317 km";
	$scope.avgSpeed= "53 km/h";
	$scope.wheatherCondition= "Chuva Forte";
	$scope.trafficCondition= "Trânsito Intenso";
	$scope.riskAlerts = "3";
	$scope.speedChanges = "455";
	$scope.agressiveIndex = "62";
	$scope.overtakeCount = "15";
	
	$scope.trajectories = [];
	
	
	var timeInterval = 5000;
	

	//$timeout(requestTrafficAnalytics, timeInterval);
	
	function requestTrafficAnalytics() {
		requestLatestTrajectoryEvaluation();
		requestAllTrajectoryEvaluation();
		
		$timeout(requestTrafficAnalytics, timeInterval);
	}
	
	function requestLatestTrajectoryEvaluation() {
		$http.get('http://localhost:9090/services/summary/trajectory/moto-x').
        success(function(data) {
            console.log(data);
			
			$scope.evaluationId= data.evaluationId;
			$scope.trajectoryTime= data.trajectoryTime;
			$scope.totalDistance= data.totalDistance;
			$scope.avgSpeed= data.avgSpeed;
			$scope.wheatherCondition= data.wheatherCondition;
			$scope.trafficCondition= data.trafficCondition;
			$scope.riskAlerts = data.riskAlerts;
			$scope.speedChanges = "-";
			$scope.agressiveIndex = "-";
			$scope.overtakeCount = data.overtakeCount;

        });
		
		
	}
	
	function requestAllTrajectoryEvaluation() {
		
		
		$http.get('http://localhost:9090/services/summary/trajectories/moto-x').
        success(function(data) {
			//http://stackoverflow.com/questions/29803045/how-to-clear-an-angular-array
			$scope.trajectories = [];
			//console.log('Total: ' + data.length);
			$scope.trajectories = data;
        });
		
		
	}
	
});


