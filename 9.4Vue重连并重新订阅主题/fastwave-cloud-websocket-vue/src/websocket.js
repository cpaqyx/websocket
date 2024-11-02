import { createApp } from 'vue'
import Vuex from 'vuex'
import App from './App.vue'
import SockJS from "sockjs-client";
import Stomp from "stompjs";



createApp(App).use(Vuex)

export default new Vuex.Store({
    state: {
        url:'',
        checkInterval:null,//断线重连时 检测连接是否建立成功
        websocket:null,
        stompClient:null,
        listenerList:[],//监听器列表，断线重连时 用于重新注册监听
    },
    getters: {
        stompClient(state) {
            return function () {
                return state.stompClient;
            }
        }
    },
    mutations: {
        WEBSOCKET_INIT(state, params) {
            state.listenerList = params.listenerList
            if(state.stompClient==null||!state.stompClient.connected) {
                state.url = params.url
                if(state.stompClient!=null&&state.websocket.readyState=== SockJS.OPEN){
                    state.stompClient.disconnect(()=>{
                        this.commit('WEBSOCKET_CONNECT')
                    })
                }else if(state.stompClient!=null&&state.websocket.readyState=== SockJS.CONNECTING){
                    console.log("连接正在建立")
                    return;
                }else{
                    this.commit('WEBSOCKET_CONNECT')
                }
                if(!state.checkInterval){
                    const interval=setInterval(()=>{
                        console.log("检测连接："+state.websocket.readyState)
                        if(state.stompClient!=null&&state.stompClient.connected){
                            clearInterval(state.checkInterval)
                            state.checkInterval=null
                            console.log('重连成功')
                        }else if(state.stompClient!=null&&state.websocket.readyState!= SockJS.CONNECTING){
                            //经常会遇到websocket的状态为open 但是stompClient的状态却是未连接状态，故此处需要把连接断掉 然后重连
                            state.stompClient.disconnect(()=>{
                                this.commit('WEBSOCKET_CONNECT')
                            })
                        }
                    },2000)
                    state.checkInterval=interval
                }
            }else{
                console.log("连接已建立成功，不再执行")
            }
        },
        WEBSOCKET_CONNECT(state){
            const _this = this
            const websock = new SockJS(state.url);
            state.websocket=websock
            // 获取STOMP子协议的客户端对象
            const stompClient = Stomp.over(websock);
            stompClient.debug = null //关闭控制台打印
            stompClient.heartbeat.outgoing = 20000;
            stompClient.heartbeat.incoming = 0;//客户端不从服务端接收心跳包
            // 向服务器发起websocket连接
            stompClient.connect(
                {name: 'none'},  //此处注意更换自己的用户名，最好以参数形式带入
                () => {
                    console.log('链接成功！')
                    state.listenerList.forEach(item=>{
                        state.stompClient.subscribe(item.topic,item.callback)
                    })
                },
                () => {//第一次连接失败和连接后断开连接都会调用这个函数 此处调用重连
                    setTimeout(() => {
                        const params = {}
                        params.url = state.url
                        params.listenerList = state.listenerList
                        _this.commit('WEBSOCKET_INIT', params)
                    }, 1000)
                }
            );
            state.stompClient = stompClient
        },
        WEBSOCKET_SEND(state, p) {
            state.stompClient.send(p.topic,{},p.data);
        },
        WEBSOCKET_UNSUBSRCIBE(state,p){
            state.stompClient.unsubscribe(p)
            for(let i=0;i<state.listenerList.length;i++){
                if(state.listenerList[i].topic==p){
                    state.listenerList.splice(i,1)
                    console.log("解除订阅："+p+" size:"+state.listenerList.length)
                    break;
                }
            }

        }
    },
    actions: {
        WEBSOCKET_INIT({commit}, params) {
            commit('WEBSOCKET_INIT', params)
        },
        WEBSOCKET_SEND({commit}, p) {
            commit('WEBSOCKET_SEND', p)
        },
        WEBSOCKET_UNSUBSRCIBE({commit}, p){
            commit('WEBSOCKET_UNSUBSRCIBE', p)
        }
    }
})