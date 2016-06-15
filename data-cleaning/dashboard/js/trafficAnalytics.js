var app = angular.module('trafficAnalytics', [
'ui.router'
]); 

var DBP_API = "http://localhost:9090/services";

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
      url: "/trajectory-map/{evaluationId}",
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
	
	
	function initialize(evaluationId) {
		$http.get(DBP_API + '/summary/trajectory-evaluation/' + evaluationId + '/coordinates').success(function(data) {			
			//http://stackoverflow.com/questions/29803045/how-to-clear-an-angular-array			
			drawTrajectory(data.coordinates);
        });		
    };


    function drawTrajectory(coordinates) {
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

		var map = new google.maps.Map(document.getElementById('map-canvas'), {
			zoom: 12,
			center: { lat: latCenter, lng : lonCenter, alt: 0 }
		});

      	lineCoordinatesPath.setMap(map);
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
          	var evaluation = {
          		trajectoryId: data.trajectoryId,
	          	evaluationId: data.evaluationId,
				trajectoryTime: data.trajectoryTime,
				startDateStr: weekDayMonth(new Date(data.startDateTime)),
				startDateTime: new Date(data.startDateTime),
				endDateTime: new Date(data.endDateTime),
				timeInterval: hoursMins(new Date(data.startDateTime)) + ' - ' + hoursMins(new Date(data.endDateTime)),
				hourClassification: data.hourClassification,
				totalDistance: data.totalDistance,
				avgSpeed: data.avgSpeed,
				maxSpeed: data.maxSpeed,
				maxDec: data.maxDec,
				maxAcc: data.maxAcc,
				wheatherCondition: data.wheatherCondition,
				trafficCondition: data.trafficCondition,
				riskAlerts: data.riskAlerts,//data.riskAlerts,
				speedChanges: data.speedChanges,
				agressiveIndex: data.agressiveIndex,
				overtakeCount: data.overtakeCount,
				accEvaluation: data.accEvaluation,
				mainStreet: data.mainStreet
			};

			console.log(data.streets);


			self.evaluation = evaluation;
          
          }).error(function(data, status, header, config) {thgtt
          	console.log('Error: ' + status);
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


