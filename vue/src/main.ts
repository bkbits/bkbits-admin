import { createApp } from "vue";
import { createPinia } from "pinia";
import AntdvNext from "antdv-next";

import "antdv-next/dist/reset.css";
import App from "@/App.vue";
import { router } from "@/router";
import "@/styles/index.scss";

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(AntdvNext);

app.mount("#app");
