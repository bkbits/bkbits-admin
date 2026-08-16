import { createApp } from "vue";
import { createPinia } from "pinia";
import AntdvNext from "antdv-next";
import zhCN from "antdv-next/locale/zh_CN";
import "antdv-next/dist/reset.css";
import App from "./App.vue";
import { router } from "./router";
import "./styles/index.scss";

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(AntdvNext, { locale: zhCN });

app.mount("#app");
