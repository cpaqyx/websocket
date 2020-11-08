var wsObj = null;
var wsUri = null;
var userId = -1;

var lockReconnect = false;//避免重复连接
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
	}
}

function initWsEventHandle() {
	try {
		wsObj.onopen = function (evt) {
			heartCheck.start();
			onWsOpen(evt);
		};

		wsObj.onmessage = function (evt) {
			heartCheck.start();
			onWsMessage(evt);
		};

		wsObj.onclose = function (evt) {
			writeToScreen("执行关闭事件，开始重连");
			//reconnect();
			onWsClose(evt);
		};
		wsObj.onerror = function (evt) {
			writeToScreen("执行error事件，开始重连");
			//reconnect();
			onWsError(evt);
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

var heartCheck = {
	//15s之内如果没有收到后台的消息，则认为是连接断开了，需要再次连接
	timeout: 15000,
	timeoutObj: null,
	serverTimeoutObj: null,
	//重启
	reset: function(){
		clearTimeout(this.timeoutObj);
		clearTimeout(this.serverTimeoutObj);
		this.start();
	},
	//开启定时器
	start: function(){
		var self = this;
		this.timeoutObj && clearTimeout(this.timeoutObj);
		this.serverTimeoutObj && clearTimeout(this.serverTimeoutObj);
		this.timeoutObj = setTimeout(function(){
			writeToScreen("发送ping到后台");
			try
			{
				wsObj.send("ping");
			}
			catch(ee)
			{
				writeToScreen("发送ping异常");
			}
			//内嵌计时器
			self.serverTimeoutObj = setTimeout(function(){
				//如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
				writeToScreen("没有收到后台的数据，关闭连接");
				//wsObj.close();
				reconnect();
			}, self.timeout);

		}, this.timeout)
	},
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