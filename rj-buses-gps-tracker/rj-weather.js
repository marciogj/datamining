"use strict";

var openweather = require('./openweather'),
 	fs = require('fs');

var rjCoordinates = [
	/*{ lat: '22.73439', lon: '43.67600'},
	{ lat: '22.73439', lon: '43.57829'},
	{ lat: '22.73439', lon: '43.48058'},
	{ lat: '22.73439', lon: '43.38287'},
	{ lat: '22.73439', lon: '43.28516'},
	{ lat: '22.73439', lon: '43.18745'},
	{ lat: '22.82444', lon: '43.67600'},
	{ lat: '22.82444', lon: '43.57829'},
	{ lat: '22.82444', lon: '43.48058'},
	{ lat: '22.82444', lon: '43.38287'},
	{ lat: '22.82444', lon: '43.28516'},
	{ lat: '22.82444', lon: '43.18745'},
	{ lat: '22.91449', lon: '43.67600'},
	{ lat: '22.91449', lon: '43.57829'},
	{ lat: '22.91449', lon: '43.48058'},
	{ lat: '22.91449', lon: '43.38287'},
	{ lat: '22.91449', lon: '43.28516'},
	{ lat: '22.91449', lon: '43.18745'},
	{ lat: '23.00454', lon: '43.67600'},
	{ lat: '23.00454', lon: '43.57829'},
	{ lat: '23.00454', lon: '43.48058'},
	{ lat: '23.00454', lon: '43.38287'},
	{ lat: '23.00454', lon: '43.28516'},*/
	{ lat: '23.00454', lon: '43.18745'} ];

var self = undefined;

function RJWeather(key) {
	self = this;
	self.openWeatherKey = key;
}


/*
 * {"coord":
 *  {"lon":1.05,"lat":35.07},
 *  "weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],
 *  "base":"cmc stations",
 *  "main":{"temp":287.15,"pressure":1016,"humidity":82,"temp_min":287.15,"temp_max":287.15},
 *  "wind":{"speed":4.1,"deg":340},
 *  "clouds":{"all":40},
 *  "dt":1460923200,
 *  "sys":{"type":3,"id":6209,"message":0.0103,"country":"DZ","sunrise":1460870385,"sunset":1460917856},
 *  "id":2496232,"name":"Frenda","cod":200}
 */
RJWeather.prototype.coordinatesWeather2FS = function(filepath) {
	if (self.openWeatherKey === undefined) {
		return console.error('Open Weather Key is not defined!');
	}
	
	rjCoordinates.forEach(function(entry) {
		
		openweather.currentWeather(entry.lat, entry.lon, self.openWeatherKey).then( function(weather) {
			delete weather.sys;
			delete weather.base;
			delete weather.id;

			fs.appendFile(filepath, JSON.stringify(weather) + '\n');
  		});
	}); 
};

module.exports = RJWeather;
