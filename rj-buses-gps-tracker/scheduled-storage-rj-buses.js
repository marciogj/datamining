"use strict";

var //https = require('https'),
    http = require('http'),
    fs = require('fs'),
    //mysql = require('mysql'),
    schedule = require('node-schedule');
    //deferred = require('deferred');
	   
var strDateTime = function() {
    return new Date().toISOString().split('.')[0].replace(/-/g, '.').replace(/T/, '_').replace(/:/g, '');
};

var log = function(msg) {
    var date = new Date(); 
    console.log( date.toISOString() +' - ' + msg);
};
 
var allDayPositions2FS = function() {
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
          log('HTTP Server response status ' + res.statusCode);
          res.setEncoding('utf8');
          
          var filename = strDateTime() + '.json';
          var stream = fs.createWriteStream(filename);        
          stream.once('open', function(fd) {
            res.on('data', function (chunk) {
              stream.write(chunk);  
            });
          });    
          res.on("end", function() {
              stream.end();
              log('Stream writen to file ' + filename);
          });  
        }
    );
	
	request.on('error', function(err) {
		log('Error during HTTP Request: ' + error);
	});
	
	request.end();
};
 
var scheduleBusesGPSData = function(hour, min, sec) {
    
    var dailyPositionsJob = schedule.scheduleJob(sec + ' ' + min + ' ' + hour + ' * * *', function(){
      console.log('Storing all buses positions from ' + new Date());
      allDayPositions2FS();
    });
 
};


//-------------------------- MAIN EXECUTION ---------------------------
//Reading input parameters
//http://stackoverflow.com/questions/4351521/how-do-i-pass-command-line-arguments-to-node-js
var scheduledHour = 23, scheduledMin = 59, scheduledSec = 55;
var isScheduledMode = true;

process.argv.forEach(function (val, index, array) {
  if (val === '-now') {
    isScheduledMode = false;
  }
});


console.log('############################');
console.log('RJ Data Store ');
console.log('############################');

console.log();


if (isScheduledMode) {
  log('Scheduling GPS extraction every day at ' + scheduledHour + ':' + scheduledMin + ':' + scheduledSec);
  scheduleBusesGPSData(scheduledHour, scheduledMin, scheduledSec);
} else {
  allDayPositions2FS();
}