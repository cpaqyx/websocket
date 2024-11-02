var wsObj = null;
var wsUri = null;
var userId = -1;
var lockReconnect = false;
var wsCreateHandler = null;
function createWebSocket() {
	var host = window.location.host; // 带有端口号
	userId = GetQueryString("userId");
	// wsUri = "ws://" + host + "/websocket?userId=" + userId;
	wsUri = "ws://" + host + "/websocket/" + userId;

	try {
		wsObj = new WebSocket(wsUri);
		initWsEventHandle();
	} catch (e) {
		writeToScreen("执行关闭事件，开始重连");
		reconnect();
	}
}

function initWsEventHandle() {
	try {
		wsObj.onopen = function (evt) {
			onWsOpen(evt);
		};

		wsObj.onmessage = function (evt) {
			onWsMessage(evt);
		};

		wsObj.onclose = function (evt) {
			writeToScreen("执行关闭事件，开始重连");
			onWsClose(evt);
			reconnect();
		};
		wsObj.onerror = function (evt) {
			writeToScreen("执行error事件，开始重连");
			onWsError(evt);
			reconnect();
		};
	} catch (e) {
		writeToScreen("绑定事件没有成功");
		reconnect();
	}
}

function onWsOpen(evt) {
	writeToScreen("CONNECTED");
}

function onWsClose(evt) {
	writeToScreen("DISCONNECTED");
}

function onWsError(evt) {
	writeToScreen(evt.data);
}

function writeToScreen(message) {
	if(DEBUG_FLAG)
	{
		$("#debuggerInfo").val($("#debuggerInfo").val() + "\n" + message);
	}
}

function GetQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	var r = window.location.search.substr(1).match(reg); //获取url中"?"符后的字符串并正则匹配
	var context = "";
	if (r != null)
		context = r[2];
	reg = null;
	r = null;
	return context == null || context == "" || context == "undefined" ? "" : context;
}

function reconnect() {
	if(lockReconnect) {
		return;
	};
	writeToScreen("1秒后重连");
	lockReconnect = true;
	//没连接上会一直重连，设置延迟避免请求过多
	wsCreateHandler && clearTimeout(wsCreateHandler);
	wsCreateHandler = setTimeout(function () {
		writeToScreen("重连..." + wsUri);
		createWebSocket();
		lockReconnect = false;
		writeToScreen("重连完成");
	}, 1000);
}