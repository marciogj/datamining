var restify = require('restify'), 
	promisify = require("promisify-node");
	
	
var connections  = 1;
var clients = {};

    
function saveCoordinates(gpsCoordinates, callback) {	
	var result = { status: "OK"};	
	log('Coordinates: ' + JSON.stringify(gpsCoordinates));
	callback(null, result);
}

function log(message) {
	console.log(new Date().toUTCString(), message);
}

function notify(msgString) {
	  for(var i in clients){
        // Send a message to the client with the message
        clients[i].sendUTF(msgString);
    }
	
}

//--------------

//var fs = require('fs');
var fs = require('graceful-fs');
var fileCount = 1;
var utf8 = 'utf8';
var lineBreak = '\n';

var write = function(filename, data) {
		fs.appendFileSync(filename, data, utf8, function(err) {
			if(err) {
				return console.log(err);
			}	
		});
};

var appendHeader = function(filename) {
	write(filename, 'timestamp,');
	write(filename, 'longitude,');
	write(filename, 'latitude,');
	write(filename, 'altitude,');
	write(filename, 'accuracy,');
	write(filename, 'bearing,');
	write(filename, 'speed');
	write(filename, lineBreak);
};

var appendLine = function(filename, coordinate) {
	write(filename, coordinate.timestamp + ',');
	write(filename, coordinate.longitude + ',');
	write(filename, coordinate.latitude + ',');
	write(filename, coordinate.altitude + ',');
	write(filename, coordinate.accuracy + ',');
	write(filename, coordinate.bearing + ',');
	write(filename, coordinate.speed);
	write(filename, lineBreak);
};

var json2csv = function (jsonMessage) {
	var deviceId = jsonMessage.deviceId,
	    userId = jsonMessage.userId,
	    coordinates = jsonMessage.coordinates;
		
	var startTime = jsonMessage.coordinates[0].timestamp;	
	var filename = userId + '_' + startTime + '_' + fileCount + '.csv';
	
	if ( coordinates.length <= 0) {
		console.log('No coordinates received. Cannot write file...');
		return;
	}
	
	
	console.log('writing to '+ filename + ': ' + deviceId+'@'+userId);
	var fileIdentification = deviceId + '@' + userId + lineBreak;
	fs.writeFileSync(filename, fileIdentification, utf8, function(err) {
		if(err) {
			return console.log(err);
		}
	});
		
	appendHeader(filename);
	coordinates.forEach( function(coordinate) {
		appendLine(filename, coordinate);
	});
	
};
//-------



var restServer = restify.createServer();
restServer.use(restify.acceptParser(restServer.acceptable));
restServer.use(restify.jsonp());
restServer.use(restify.bodyParser({ mapParams: false }));

restServer.post('/track/:id', function create(req, res, next) {
   var syncSave = promisify(saveCoordinates), 
       response = { status: "error"}, 
	   gpsCoordinates = req.body.coordinates;
   
   var message = JSON.stringify(req.body);
   json2csv(req.body);
   log('Request received!');
   
   syncSave(gpsCoordinates).then(
       function(saveResponse) { 	       
		    //response.isAuthenticated = isAuthenticated;
		    log('Coordinate list saved');
			response.status = "success";
			res.send(200, response);
			res.end();
			
			//todo
			notify(message);
			
			
			return next(response);
       }, function () {
	       log('Something wrong just happen on request: ');
		   log(req.body);
		   res.send(500, JSON.stringify(response));
	       res.end();
		   return next(response);
		   
	   }
	);
});

restServer.get("/ping", function (req, res, next) {
  res.writeHead(200, {'Content-Type': 'application/json; charset=utf-8'});
  res.end(JSON.stringify('Ping received!'));
  log('Client ping requested');
  return next();
});


restServer.listen(9999, function() {
  log('==============================================');
  log('REST Service v1 ' + restServer.name + ' listening at ' + restServer.url);
  log('==============================================\n\n');
  
});

//=============================== WEBSOCKET SESSION ===============================
//https://www.npmjs.com/package/websocket
//http://codular.com/node-web-sockets

var WebSocketServer = require('websocket').server;
var http = require('http');
 
var webSocketServer = http.createServer(function(request, response) {
    log('## WebSocket Received request for ' + request.url);
    response.writeHead(404);
	response.end();
});

webSocketServer.listen(9090, function() {
  log('##############################################');
  log('WebSocket Service listening at port 9090');
  log('##############################################\n\n');
});
 
wsServer = new WebSocketServer({
    httpServer: webSocketServer,
    // You should not use autoAcceptConnections for production 
    // applications, as it defeats all standard cross-origin protection 
    // facilities built into the protocol and the browser.  You should 
    // *always* verify the connection's origin and decide whether or not 
    // to accept it. 
    autoAcceptConnections: false
});
 
function originIsAllowed(origin) {
  // put logic here to detect whether the specified origin is allowed. 
  return true;
}


 
wsServer.on('request', function(request) {
    if (!originIsAllowed(request.origin)) {
      // Make sure we only accept requests from an allowed origin 
      request.reject();
      console.log((new Date()) + ' Connection from origin ' + request.origin + ' rejected.');
      return;
    }
    
    var connection = request.accept('echo-protocol', request.origin);
	
	var clientId = connections;
	clients[clientId] = connection;
	connections++;
	
	
	//registrar um callback na função de recebimento do webservice
	
    console.log((new Date()) + ' Connection accepted.');
    connection.on('message', function(message) {
        if (message.type === 'utf8') {
            console.log('Received Message: ' + message.utf8Data);
            connection.sendUTF(message.utf8Data);
        }
        else if (message.type === 'binary') {
            console.log('Received Binary Message of ' + message.binaryData.length + ' bytes');
            connection.sendBytes(message.binaryData);
        }
    });
	
    connection.on('close', function(reasonCode, description) {
        console.log((new Date()) + ' Peer ' + connection.remoteAddress + ' disconnected.');
		 delete clients[clientId];
    });
});

//=========================================
var connect = require('connect');
var serveStatic = require('serve-static');


connect().use(serveStatic(__dirname)).listen(8080, function() {
  log('_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-');
  log('HTTP Static Server listening on port 8080');  
  log('_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-\n\n');
  
});