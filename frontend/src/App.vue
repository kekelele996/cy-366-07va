<script setup lang="ts">
import { onMounted, ref } from "vue";
import { fetchOverview } from "./api/client";
import { APP_CODE, APP_NAME } from "./constants/app";
import { REQUEST_MESSAGES } from "./constants/messages";
import { createFallbackOverview } from "./state/dashboard";
import type { OverviewResponse } from "./types";
import FeatureStrip from "./components/FeatureStrip.vue";
import MetricGrid from "./components/MetricGrid.vue";
import OperationsTable from "./components/OperationsTable.vue";
import FrontDeskView from "./views/FrontDeskView.vue";
import OperationsView from "./views/OperationsView.vue";

const overview = ref<OverviewResponse>(createFallbackOverview());
const notice = ref(REQUEST_MESSAGES.overviewFallback);
const activeTab = ref(0);

function goHealth() {
  window.location.href = REQUEST_MESSAGES.healthPath;
}

onMounted(async () => {
  try {
    overview.value = await fetchOverview();
    notice.value = "后端服务已联通，当前展示实时接口数据。";
  } catch {
    notice.value = REQUEST_MESSAGES.overviewFallback;
  }
});
</script>

<template>
  <main class="app-shell">
    <header class="topbar">
      <div>
        <span class="brand-code">{{ APP_CODE }}</span>
        <h1 class="brand-title">{{ APP_NAME }}</h1>
      </div>
      <van-button type="primary" @click="goHealth">API Health</van-button>
    </header>
    <section class="workspace">
      <van-tabs v-model:active="activeTab" sticky line-width="48" color="#1b7f82">
        <van-tab title="到店开机">
          <FrontDeskView v-if="activeTab === 0" />
        </van-tab>
        <van-tab title="运营台">
          <OperationsView v-if="activeTab === 1" />
        </van-tab>
        <van-tab title="运营总览">
          <div class="lead-grid overview-grid">
            <article class="hero-panel">
              <span class="pill">{{ notice }}</span>
              <h2>{{ overview.appName }}</h2>
              <p>{{ overview.description }}</p>
            </article>
            <MetricGrid :items="overview.kpis" />
          </div>
          <FeatureStrip :items="overview.features" />
          <section class="work-panel">
            <h2>运营任务流</h2>
            <OperationsTable :records="overview.records" />
          </section>
        </van-tab>
      </van-tabs>
    </section>
  </main>
</template>
