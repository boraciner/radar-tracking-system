var ws;

function connect() {
	ws = new WebSocket('ws://localhost:8080/user');
	ws.onmessage = function(data) {
		sendTrackMessage(data.data);
	}
}

function disconnect() {
	if (ws != null) {
		ws.close();
	}
	console.log("Websocket is in disconnected state");
}


function sendTrackMessage(message) {
	//var track = JSON.parse(message)
	$("#trackdetails").append(" " + 
'	<div class=\"card\">\
    <div class=\"card-body\">\
    <h5 class=\"card-title\">Card title</h5>\
    <p class=\"card-text\">Some quick example text to build on the card title and make up the bulk of the cards content.</p>\
  </div>\
</div>\
'
	
	+ "<br>");
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
});

connect();
