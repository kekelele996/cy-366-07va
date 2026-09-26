<script setup lang="ts">
import { onMounted, ref } from "vue";
import { showConfirmDialog, showFailToast, showSuccessToast } from "vant";
import { checkIn, fetchArrivals } from "../api/client";
import type { CheckInResult, ReservationArrival } from "../types";
import { formatMoney, formatTimeRange } from "../utils/format";

const arrivals = ref<ReservationArrival[]>([]);
const loading = ref(false);
const submittingId = ref<number | null>(null);
const errorMessage = ref("");
const lastResult = ref<CheckInResult | null>(null);

async function loadArrivals() {
  loading.value = true;
  errorMessage.value = "";
  try {
    arrivals.value = await fetchArrivals();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : "预约单加载失败";
  } finally {
    loading.value = false;
  }
}

async function handleCheckIn(reservation: ReservationArrival) {
  lastResult.value = null;
  try {
    await showConfirmDialog({
      title: "确认到店开机",
      message: `会员 ${reservation.memberName}（${reservation.memberNo}）将在机位 ${reservation.stationNo} 开机，首小时 ${formatMoney(reservation.hourlyRate)}，先扣时长包、不足扣余额。`,
      confirmButtonText: "确认开机",
    });
  } catch {
    return;
  }

  submittingId.value = reservation.reservationId;
  try {
    const result = await checkIn(reservation.reservationId);
    lastResult.value = result;
    if (result.success) {
      showSuccessToast("已开机，机位切换为使用中");
    } else {
      showFailToast(`余额不足，差额 ${formatMoney(result.shortage)}`);
    }
    await loadArrivals();
  } catch (error) {
    showFailToast(error instanceof Error ? error.message : "开机失败");
  } finally {
    submittingId.value = null;
  }
}

onMounted(loadArrivals);
</script>

<template>
  <section class="work-panel front-desk">
    <div class="panel-head">
      <div>
        <h2>到店开机</h2>
        <p>会员到店后选择预约单，系统核验会员与机位，自动扣首小时并切换机位状态。</p>
      </div>
      <van-button size="small" plain type="primary" :loading="loading" @click="loadArrivals">
        刷新预约单
      </van-button>
    </div>

    <van-notice-bar
      v-if="errorMessage"
      type="warning"
      :text="errorMessage"
      class="inline-notice"
    />

    <div v-if="!loading && arrivals.length === 0 && !errorMessage" class="empty-hint">
      当前没有待开机的预约单。
    </div>

    <div class="reservation-grid">
      <article v-for="item in arrivals" :key="item.reservationId" class="reservation-card">
        <header class="card-head">
          <div>
            <span class="card-no">预约 {{ item.reservationNo }}</span>
            <h3>{{ item.memberName }}<small>（{{ item.memberNo }}）</small></h3>
          </div>
          <van-tag type="warning" plain>预约中</van-tag>
        </header>

        <dl class="card-meta">
          <div><dt>机位</dt><dd>{{ item.zoneName }} · {{ item.stationNo }}</dd></div>
          <div><dt>预约时段</dt><dd>{{ formatTimeRange(item.startTime, item.endTime) }}</dd></div>
          <div><dt>时长包</dt><dd>{{ item.packageMinutes ?? 0 }} 分钟</dd></div>
          <div><dt>余额</dt><dd>{{ formatMoney(item.balance) }}</dd></div>
          <div><dt>首小时</dt><dd>{{ formatMoney(item.hourlyRate) }}</dd></div>
        </dl>

        <van-button
          block
          type="primary"
          :loading="submittingId === item.reservationId"
          @click="handleCheckIn(item)"
        >
          选择此预约单开机
        </van-button>
      </article>
    </div>

    <div v-if="lastResult" class="result-box" :class="lastResult.success ? 'is-success' : 'is-fail'">
      <template v-if="lastResult.success">
            <h3>✅ {{ lastResult.message }}</h3>
            <ul class="charge-list">
              <li>
                <span>时长包抵扣</span>
                <strong>{{ lastResult.packageMinutesUsed }} 分钟</strong>
              </li>
              <li>
                <span>余额兜底</span>
                <strong>{{ formatMoney(lastResult.balanceAmountUsed) }}</strong>
              </li>
              <li>
                <span>上机单号</span>
                <strong>{{ lastResult.sessionNo }}</strong>
              </li>
            </ul>
      </template>
      <template v-else>
        <h3>⚠️ 开机未完成</h3>
            <p>{{ lastResult.message }}</p>
            <p class="shortage-line">差额：<strong>{{ formatMoney(lastResult.shortage) }}</strong>，预约单与机位状态均已保留，充值后可重新开机。</p>
      </template>
    </div>
  </section>
</template>
