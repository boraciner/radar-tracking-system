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
	$("#trackdetails").append(" " + message + "<br>");
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
});

connect();
