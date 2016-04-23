"use strict";

var http = require('http'), 
    Q = require('q');

var currentWeather = function(lat,lon, key) {
  var requestHeader = {
    'Content-Type': 'application/json'
  },
  
  options = {
    host: 'api.openweathermap.org',
    port: 80,
    path: '/data/2.5/weather?lat=' + lat + '&lon=' + lon + '&appid=' + key,
    method: 'GET',
    headers: requestHeader
  };

  var deferred = Q.defer();
  var strData = '';

  if (key != undefined && key != null) {
    
    try { 
      var request = http.request(options, function(res) {
            res.setEncoding('utf8');
            res.on('data', function (chunk) {
                strData += chunk;
            });

            res.on("end", function() {
            	try {
            		deferred.resolve(JSON.parse(strData));
            	} catch (e) {
        			 console.error(e);
        			 deferred.reject(e);
      		    }
            }); 

            res.on("error", function(error) {
                console.log('Error requesting OpenWeather at ' + new Date());
                deferred.reject(err);
            });  
      });
    } catch (err) {
      deferred.reject(e);
      return console.error(err);
    }

    request.end();
    return deferred.promise;

  }
};

module.exports = {
  currentWeather: currentWeather
};