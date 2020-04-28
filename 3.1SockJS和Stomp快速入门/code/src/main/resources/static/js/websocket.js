var stompClient = null;
var wsCreateHandler = null;

function connect() {
	var host = window.location.host; // 带有端口号
	var socket = new SockJS("http://" + host + "/websocket");
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		writeToScreen("connected: " + frame);
		stompClient.subscribe('/topic', function (response) {
			writeToScreen(response.body);
		});

		}, function (error) {
			wsCreateHandler && clearTimeout(wsCreateHandler);
			wsCreateHandler = setTimeout(function () {
				console.log("重连...");
				connect();
				console.log("重连完成");
			}, 1000);
		}
	)
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	writeToScreen("disconnected");
}

function writeToScreen(message) {
	if(DEBUG_FLAG)
	{
		$("#debuggerInfo").val($("#debuggerInfo").val() + "\n" + message);
	}
}