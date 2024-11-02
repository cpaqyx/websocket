import { createApp } from 'vue'
import App from './App.vue'
import websocket from './websocket'
// createApp.prototype.$websocket = websocket;

const app = createApp(App);

app.config.globalProperties.$websocket = websocket;

app.mount('#app')
