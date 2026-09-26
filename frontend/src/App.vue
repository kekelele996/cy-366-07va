<script setup lang="ts">
import { computed, ref } from "vue";
import { APP_CODE, APP_NAME } from "./constants/app";
import FrontDeskView from "./views/FrontDeskView.vue";
import OperationConsoleView from "./views/OperationConsoleView.vue";
import OverviewView from "./views/OverviewView.vue";

const active = ref(0);

const tabs = [
  { label: "运营总览", icon: "chart-trending-o" },
  { label: "前台开机", icon: "desktop-o" },
  { label: "运营台", icon: "cluster-o" },
];

const currentTitle = computed(() => tabs[active.value].label);
</script>

<template>
  <main class="app-shell">
    <header class="topbar">
      <div>
        <span class="brand-code">{{ APP_CODE }}</span>
        <h1 class="brand-title">{{ APP_NAME }} · {{ currentTitle }}</h1>
      </div>
      <van-tag v-if="active === 1" type="warning" size="large">会员到店后在此开机</van-tag>
    </header>
    <section class="workspace">
      <OverviewView v-if="active === 0" />
      <FrontDeskView v-else-if="active === 1" />
      <OperationConsoleView v-else />
    </section>
    <van-tabbar v-model="active" fixed safe-area-inset-bottom>
      <van-tabbar-item v-for="tab in tabs" :key="tab.label" :icon="tab.icon">
        {{ tab.label }}
      </van-tabbar-item>
    </van-tabbar>
  </main>
</template>
