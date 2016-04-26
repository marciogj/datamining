"use strict";

var http = require('http'),
    fs = require('fs'),
    schedule = require('node-schedule'),
    parser = require('./rj-gps-bus-parser'),
    openweather = require('./openweather'),
    RJWeather = require('./rj-weather');
    
	   
var strDateTime = function() {
    return new Date().toISOString().split('.')[0].replace(/-/g, '.').replace(/T/, '_').replace(/:/g, '');
};

var log = function(msg) {
    var date = new Date(); 
    console.log(date.toLocaleString() +' - ' + msg);
};
 
var allDayPositions2FS = function(filename) {
    log('Retrieving all day position from RJ Buses');
    var requestHeader = {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'
    },
    options = {
      host: 'dadosabertos.rio.rj.gov.br',
      port: 80,
      path: '/apiTransporte/apresentacao/rest/index.cfm/obterTodasPosicoes',
      method: 'GET',
      headers: requestHeader
    };
 
    var request = http.request(options, function(res) {
          log('DadosAbertos Response status ' + res.statusCode);
          res.setEncoding('utf8');

          //var filename = strDateTime() + '.json';
          var stream = fs.createWriteStream(filename);        
          stream.once('open', function(fd) {
            res.on('data', function (chunk) {
              stream.write(chunk);  
            });
          });    
          res.on("end", function() {
              stream.end();
              log('Stream written to file ' + filename);
              parser.updatePositionByBus(filename, new Date());

          });

          res.on("error", function(error) {
              log('Error requesting DadosAbertos at ' + error);
          });    
        }
    );
	
	request.on('error', function(err) {
		log('Error during HTTP Request: ' + err);
	});
	
	request.end();
};
 
var scheduleBusesGPSData = function(hour, min, sec) {
    var openWeatherKey = loadOpenWeatherKey('server.properties');    
    var dailyPositionsJob = schedule.scheduleJob(sec + ' ' + min + ' ' + hour + ' * * *', function(){
      try {
        console.log('Storing all buses positions and weather from ' + new Date());
        var filename = strDateTime();
        allDayPositions2FS(filename + '_buses.json');

        var rjCoordsWeather = new RJWeather(openWeatherKey);
        rjCoordsWeather.coordinatesWeather2FS(filename + '_weather.json');
      } catch(err) {
        console.error(err);
      }
    });
 
};

var loadOpenWeatherKey = function(filePath) {
  var key = undefined;
  var content = fs.readFileSync(filePath, 'utf8');

  var lines = content.split('\n');
  lines.forEach(function(line) {
    if (line.indexOf('open-weather-key=') > -1) {
        key = line.split('open-weather-key=')[1];
    }
  });
  return key;
};


var showWeather = function(lat, lon) {
  if (lat === undefined || lon === undefined) {
    log('Error! Cannot check weather at invalid location');
    process.exit();
  } 
  var openWeatherKey = loadOpenWeatherKey('server.properties');
  console.log('Checking weather at lat=' + lat + ', lon=' + lon);
  openweather.currentWeather(lat, lon, openWeatherKey).then( function(weather) {
    console.log(weather);  
  });
};


var deamon = function() {
  console.log('############################');
  console.log('RJ Data Store');
  console.log('############################');
  console.log();
  var openWeatherKey = loadOpenWeatherKey('server.properties');
  log('Scheduling GPS extraction every day at hours(' + scheduledHour + ') min(' + scheduledMin + ') sec(' + scheduledSec+')');
  scheduleBusesGPSData(scheduledHour, scheduledMin, scheduledSec);
};

var convertToCSV = function(filepath) {
  if (filepath === undefined) {
    console.log('Error! Cannot convert non specified filepath');
    process.exit()
  }
  
  console.log('Converting file ' + filepath);
  parser.jsonToCSV(filepath);
};


//-------------------------- MAIN EXECUTION ---------------------------
//Reading input parameters
//http://stackoverflow.com/questions/4351521/how-do-i-pass-command-line-arguments-to-node-js
var scheduledHour = '*', scheduledMin = '*', scheduledSec = '';
var index = 0;

process.argv.forEach(function (val, index, array) {
  if (val === '-now') {
    allDayPositions2FS();
  }

  if(val === '-tocsv') {
    convertToCSV(array[index+1]);
  }

  if (val === '-weather') {
    showWeather(array[index+1], array[index+2]);
  }

  if (val === '-deamon') {
    deamon();
  }
  index++;
});
