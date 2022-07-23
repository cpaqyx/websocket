<template>
  <div class="">
    <h1>stomp消息发送</h1>
    <div>发送者：<input type="text" v-model="fromUserId" disabled /></div>
    <div>接收者：<input type="text" v-model="toUserId" /></div>
    <div>发送内容：<input type="text" v-model="message" /></div>
    <div>
      <button @click="login">登录</button>
      <button @click="sendToOne">点对点发送消息</button>
      <button @click="sendToAll">群发消息</button>
      <textarea id="debuggerInfo" style="width:100%;height:200px;" v-model="charts"></textarea>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        charts: "",
        stompClient: null,
        message: "",
        fromUserId: "",
        toUserId: "",
        listenerList:[]
      }
    },
    mounted() {
      function GetQueryStrin(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg); //获取url中"?"符后的字符串并正则匹配
        var context = "";
        if (r != null)
          context = r[2];
        reg = null;
        r = null;
        return context == null || context == "" || context == "undefined" ? "" : context;
      }
      this.fromUserId = GetQueryStrin("userId");
    },
    methods: {
      // 模拟登录
      login() {
        const subscribes = [];

        // 订阅群发消息主题
        const subscribeAll = {};
        subscribeAll.topic = "/topic/all"
        subscribeAll.callback = ((val) => {
          this.charts += "\n 群发消息：" + val.body
        })
        subscribes.push(subscribeAll)

        // 点对点发消息主题
        const subscribeUser = {};
        subscribeUser.topic = "/queue/" + this.fromUserId + "/topic"
        subscribeUser.callback = ((val) => {
          this.charts += "\n 点对点消息：" + val.body
        })
        subscribes.push(subscribeUser)

        // 将参数封闭起来一起带过去
        const params = {}
        params.url = "http://localhost:8071/websocket"
        params.listenerList = subscribes

        // 初始化好链接，并订阅主题
        this.$websocket.dispatch('WEBSOCKET_INIT', params)
      },
      sendToAll() {
        const stompClient=this.$websocket.getters.stompClient()
        stompClient.send("/app/sendToAll", {}, this.message);
      },
      sendToOne() {
        let msgJson  = {"fromUserId": this.fromUserId, "toUserId": this.toUserId, "msg": this.message};
        let msgStr = JSON.stringify(msgJson)
        const stompClient=this.$websocket.getters.stompClient()
        stompClient.send("/app/sendToUser",{}, msgStr);
      }
    }
  }
</script>