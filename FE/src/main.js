import { createApp } from 'vue'
import router from "./router";
import App from './App.vue'
import "./assets/style.css";
import store from '@/store/index';

createApp(App).use(router, store).mount('#app')
