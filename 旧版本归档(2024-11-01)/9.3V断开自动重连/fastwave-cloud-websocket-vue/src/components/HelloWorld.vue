<template>
  <div class="">
    <h1>stomp消息发送</h1>
    <div>发送者：<input type="text" v-model="fromUserId" disabled /></div>
    <div>接收者：<input type="text" v-model="toUserId" /></div>
    <div>发送内容：<input type="text" v-model="message" /></div>
    <div>
      <button @click="login">登录</button>
      <button @click="sendToOne">点对点发送消息</button>
      <button @click="subscrible">订阅</button>
      <button @click="sendToAll">群发消息</button>
      <textarea id="debuggerInfo" style="width:100%;height:200px;" v-model="charts"></textarea>
    </div>
  </div>
</template>

<script>
  import SockJS from 'sockjs-client'
  import Stomp from 'webstomp-client'

  export default {
    data() {
      return {
        charts: "",
        stompClient: null,
        message: "",
        fromUserId: "",
        toUserId: ""
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
        this.$websocket.dispatch('WEBSOCKET_INIT',"http://localhost:8071/websocket")
      },
      subscrible(){
        const stompClient=this.$websocket.getters.stompClient()
        stompClient.subscribe('/topic/all', (val) => {
          this.charts += "\n 群发消息：" + val.body
        })

        stompClient.subscribe("/queue/" + this.fromUserId + "/topic", (val) => {
          this.charts += "\n " + val.body
          console.log(val.body)
        })
      },
      sendToOne() {
        let msgJson  = {"fromUserId": this.fromUserId, "toUserId": this.toUserId, "msg": this.message};
        let msgStr = JSON.stringify(msgJson)
        const stompClient=this.$websocket.getters.stompClient()
        stompClient.send("/app/sendToUser",{}, msgStr);
      },
      sendToAll() {
        const stompClient=this.$websocket.getters.stompClient()
        stompClient.send("/app/sendToAll", {}, this.message);
      },
      initWebSocket() {
        this.connection()
      },
      connection() {
        const socket = new SockJS('http://localhost:8071/websocket')
        this.stompClient = Stomp.over(socket)
        //建立连接，订阅主题
        this.stompClient.connect({}, (frame) => {
          console.log(frame)
          this.stompClient.subscribe('/topic/all', (val) => {
            this.charts += "\n 群发消息：" + val.body
          }),
          this.stompClient.subscribe("/queue/" + this.fromUserId + "/topic", (val) => {
            this.charts += "\n " + val.body
            console.log(val.body)
          })
        })
      }
    }
  }
</script>