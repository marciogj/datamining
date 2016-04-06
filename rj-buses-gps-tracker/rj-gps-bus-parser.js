"use strict";

var fs = require('fs');
	   
//converts a 'dadosabertos' date format to a javascipt date object
var toDate = function(strDate) {
  var parts = strDate.split(' ');
  var dateParts = parts[0].split('-');
  var month = dateParts[0];
  var day = dateParts[1];
  var year = dateParts[2];

  return new Date(year + '-' + month + '-' + day + ' ' + parts[1]);
};

var twoDigits = function(num) {
  return num < 9 ? '0' + num : num.toString();
};

var strLocalDateTime = function(aDate) {
    var date = aDate.getFullYear() + '.' + twoDigits(aDate.getMonth()) + '.' + twoDigits(aDate.getDate());
    var time = twoDigits(aDate.getHours()) + ':' + twoDigits(aDate.getMinutes()) + ':' + twoDigits(aDate.getSeconds());
    return date + ' ' + time;
};


//{"COLUMNS":["DATAHORA","ORDEM","LINHA","LATITUDE","LONGITUDE","VELOCIDADE"],
//"DATA":[["04-02-2016 00:00:37","A63540","",-22.867701,-43.2584,0.0], ...]}
var updatePositionByBus = function(filePath, latestDatePosition) {
  fs.readFile(filePath, 'utf8', function (err, data) {
    if (err) {
      return console.log(err);
    }
    
    try {
      var gpsContent = JSON.parse(data);
      var gpsData = gpsContent.DATA;
    
      gpsData.forEach(function(entry) {
        var positionDateTime = toDate(entry[0]);
        var isNewPosition = positionDateTime.getTime() < latestDatePosition.getTime();

        if (isNewPosition) {
          var busEntry = {
            datetime: positionDateTime,
            busId: entry[1], 
            busLine: entry[2],
            latitude: entry[3],
            longitude: entry[4],
            speed: entry[5]
          };

          updateBusPosition(busEntry);
        }
      });
    } catch (e) {
      
      return console.error(e);
    }

  });
};

var updateBusPosition = function(busEntry) {
  var baseDir = 'data';

  if (!fs.existsSync(baseDir)){
    fs.mkdirSync(baseDir);
  }

  var localDateTime = strLocalDateTime(busEntry.datetime);
  var lineDir = busEntry.busLine.length == 0 ? baseDir + '/SEM_LINHA' : baseDir + '/' + busEntry.busLine.toString();
  var fileName = localDateTime.split(' ')[0] + '_' + busEntry.busId + '.csv';
  
  if (!fs.existsSync(lineDir)){
    fs.mkdirSync(lineDir);
  }

  var newLine = localDateTime + ',' + busEntry.latitude + ', ' + busEntry.longitude + ',' + busEntry.speed + '\r\n';
  fs.appendFileSync(lineDir + '/' + fileName, newLine);

};
 
//-----------------------------------------------------
module.exports = {
  updatePositionByBus: updatePositionByBus,
  
};

