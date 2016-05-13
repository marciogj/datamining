var app = angular.module('trafficAnalytics', [
'ui.router'
]); 

var DBP_API = "http://localhost:9090/services";

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
      url: "/driver-profile/{id}",
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

    .state('trajectory-evaluation', {
      url: "/trajectory-evaluation/{id}",
      templateUrl: "views/trajectory-evaluation.html",
      controller: 'trajectoryEvaluationCtrl',
      controllerAs: 'vm'
    });

    $urlRouterProvider.otherwise("/drivers");

});

app.controller('driversCtrl',  ['$scope','$stateParams',  function($scope , $stateParams ){
	var self = this;
		

	//moto-x
	var listAllDrivers = function() {
		return [
			{id: 'moto-x', name: 'Marcio Jasinski', trajectories: 745, profileIndex: 25},
			{id: '2', name: 'Anderson Torres', trajectories: 3, profileIndex: 51},
			{id: '3', name: 'Taxi GTD-8912', trajectories: 1, profileIndex: 75}
		];
	};

	self.drivers = listAllDrivers();
}]);

app.controller('driverProfileCtrl',  ['$scope','$stateParams', '$http',  function($scope , $stateParams, $http){
	var self = this;

	function requestAllTrajectoryById(id) {

		$http.get(DBP_API + '/summary/trajectories/' + id).success(function(data) {			
			//http://stackoverflow.com/questions/29803045/how-to-clear-an-angular-array
			self.trajectories = [];
			self.trajectories = data;
        });
	}

	requestAllTrajectoryById($stateParams.id);

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
				totalDistance: data.totalDistance,
				avgSpeed: data.avgSpeed,
				wheatherCondition: data.wheatherCondition,
				trafficCondition: data.trafficCondition,
				riskAlerts: data.riskAlerts,
				speedChanges: "-",
				agressiveIndex: "-",
				overtakeCount: data.overtakeCount,
				accEvaluation: data.accEvaluation
			};

			self.evaluation = evaluation;
          
          }).error(function(data, status, header, config) {
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


