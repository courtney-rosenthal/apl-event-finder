import './assets/main.css';
import "primevue/resources/themes/lara-light-indigo/theme.css";
import 'primeicons/primeicons.css'

import { createApp } from 'vue'
import App from './App.vue';
import PrimeVue from 'primevue/config';

const app = createApp(App)
app.use(PrimeVue);
app.mount('#app')
