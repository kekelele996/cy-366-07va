<script setup lang="ts">
import { onMounted, ref } from "vue";
import { fetchActiveStations, fetchCharges } from "../api/client";
import type { ActiveStation, ChargeLine } from "../types";
import {
  CHARGE_TYPE_LABELS,
  formatDateTime,
  formatMoney,
} from "../utils/format";

const activeStations = ref<ActiveStation[]>([]);
const charges = ref<ChargeLine[]>([]);
const loading = ref(false);
const errorMessage = ref("");

async function load() {
  loading.value = true;
  errorMessage.value = "";
  try {
    const [stations, chargePage] = await Promise.all([
      fetchActiveStations(),
      fetchCharges(50),
    ]);
    activeStations.value = stations;
    charges.value = chargePage.charges;
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : "运营数据加载失败";
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<template>
  <section class="work-panel operations">
    <div class="panel-head">
      <div>
        <h2>运营台 · 上机监控</h2>
        <p>实时查看上机中的机位，以及本次开机的扣费明细（时长包优先、余额兜底）。</p>
      </div>
      <van-button size="small" plain type="primary" :loading="loading" @click="load">
        刷新
      </van-button>
    </div>

    <van-notice-bar
      v-if="errorMessage"
      type="warning"
      :text="errorMessage"
      class="inline-notice"
    />

    <h3 class="sub-title">上机中的机位（{{ activeStations.length }}）</h3>
    <div v-if="activeStations.length === 0 && !loading" class="empty-hint">
      暂无使用中的机位。
    </div>
    <div class="plain-table" v-else>
      <div class="plain-row plain-head ops-row">
        <span>机位</span>
        <span>会员</span>
        <span>上机单号</span>
        <span>开机时间</span>
        <span>首小时扣费</span>
      </div>
      <div
        v-for="station in activeStations"
        :key="station.sessionId"
        class="plain-row ops-row"
      >
        <span>
          <van-tag type="success" plain>使用中</van-tag>
          {{ station.zoneName }} · {{ station.stationNo }}
        </span>
        <span>{{ station.memberName }}（{{ station.memberNo }}）</span>
        <span>{{ station.sessionNo }}</span>
        <span>{{ formatDateTime(station.startedAt) }}</span>
        <span class="charge-breakdown">
          时长包 {{ station.packageMinutesUsed }} 分
          <template v-if="station.balanceAmountUsed > 0">
            ＋ 余额 {{ formatMoney(station.balanceAmountUsed) }}
          </template>
        </span>
      </div>
    </div>

    <h3 class="sub-title">最近扣费记录</h3>
    <div v-if="charges.length === 0 && !loading" class="empty-hint">
      暂无扣费记录。
    </div>
    <div class="plain-table" v-else>
      <div class="plain-row plain-head charge-row">
        <span>时间</span>
        <span>会员</span>
        <span>机位</span>
        <span>扣费方式</span>
        <span>分钟</span>
        <span>金额</span>
        <span>说明</span>
      </div>
      <div
        v-for="charge in charges"
        :key="charge.id"
        class="plain-row charge-row"
      >
        <span>{{ formatDateTime(charge.createdAt) }}</span>
        <span>{{ charge.memberName }}</span>
        <span>{{ charge.stationNo }}</span>
        <span>
          <van-tag :type="charge.chargeType === 'package' ? 'primary' : 'warning'" plain>
            {{ CHARGE_TYPE_LABELS[charge.chargeType] ?? charge.chargeType }}
          </van-tag>
        </span>
        <span>{{ charge.minutes }}</span>
        <span>{{ formatMoney(charge.amount) }}</span>
        <span class="charge-detail">{{ charge.detail }}</span>
      </div>
    </div>
  </section>
</template>
