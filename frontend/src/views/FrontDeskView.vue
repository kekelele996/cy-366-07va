<script setup lang="ts">
import { onMounted, ref } from "vue";
import { showNotify } from "vant";
import { checkinReservation, fetchReservations } from "../api/frontdesk";
import type { CheckinResult, Reservation } from "../types";
import {
  chargeTypeLabel,
  formatDateTime,
  formatMoney,
  formatTime,
  seatStatusLabel,
} from "../utils/format";

const reservations = ref<Reservation[]>([]);
const loading = ref(false);
const selected = ref<Reservation | null>(null);
const verifyVisible = ref(false);
const resultVisible = ref(false);
const result = ref<CheckinResult | null>(null);
const submitting = ref(false);

async function loadReservations() {
  loading.value = true;
  try {
    reservations.value = await fetchReservations("RESERVED");
  } catch (error) {
    showNotify({ type: "danger", message: (error as Error).message });
  } finally {
    loading.value = false;
  }
}

function choose(reservation: Reservation) {
  selected.value = reservation;
  result.value = null;
  verifyVisible.value = true;
}

async function confirmCheckin() {
  if (!selected.value || submitting.value) {
    return;
  }
  submitting.value = true;
  try {
    result.value = await checkinReservation(selected.value.id);
    verifyVisible.value = false;
    resultVisible.value = true;
    if (result.value.success) {
      showNotify({ type: "success", message: "开机成功，机位已切为使用中" });
    } else {
      showNotify({ type: "warning", message: "余额不足，预约与机位已保留" });
    }
    await loadReservations();
  } catch (error) {
    showNotify({ type: "danger", message: (error as Error).message });
  } finally {
    submitting.value = false;
  }
}

function askConfirm() {
  // 核验弹窗点击确认即执行扣费；余额不足时后端不会改动预约与机位。
  void confirmCheckin();
}

onMounted(loadReservations);
</script>

<template>
  <section class="page">
    <div class="page-head">
      <div>
        <h2>前台 · 到店开机</h2>
        <p>选择预约单后核验会员与机位，系统按「时长包优先、余额兜底」扣首小时并把机位切为使用中。</p>
      </div>
      <van-button type="primary" size="small" icon="replay" :loading="loading" @click="loadReservations">
        刷新预约
      </van-button>
    </div>

    <van-notice-bar
      left-icon="info-o"
      text="今晚会员到店请先在此选择预约单；余额不足时预约和机位状态会保留，按页面提示的差额充值后重试。"
    />

    <div v-if="loading" class="state-box">
      <van-loading size="24px">加载预约单中…</van-loading>
    </div>
    <van-empty v-else-if="reservations.length === 0" description="暂无待开机的预约单" />

    <div v-else class="reservation-list">
      <article v-for="item in reservations" :key="item.id" class="reservation-card">
        <div class="card-main">
          <div class="card-title">
            <van-tag plain type="primary">{{ item.reservationNo }}</van-tag>
            <strong>{{ item.seatNo }} · {{ item.seatArea }}</strong>
            <van-tag :type="item.seatStatus === 'RESERVED' ? 'warning' : 'danger'">
              机位{{ seatStatusLabel(item.seatStatus) }}
            </van-tag>
          </div>
          <dl class="verify-grid">
            <div>
              <dt>会员</dt>
              <dd>{{ item.memberName }}（{{ item.memberNo }}）</dd>
            </div>
            <div>
              <dt>预约时段</dt>
              <dd>今日 {{ formatTime(item.startTime) }} – {{ formatTime(item.endTime) }}</dd>
            </div>
            <div>
              <dt>机位类型 / 时租</dt>
              <dd>{{ item.seatType }} / ¥{{ formatMoney(item.hourlyRate) }}/小时</dd>
            </div>
            <div>
              <dt>时长包余额</dt>
              <dd :class="{ 'is-low': item.packageRemainingMinutes < 60 }">
                {{ item.packageRemainingMinutes }} 分钟
              </dd>
            </div>
            <div>
              <dt>账户余额</dt>
              <dd :class="{ 'is-low': item.memberBalance < item.hourlyRate }">
                ¥{{ formatMoney(item.memberBalance) }}
              </dd>
            </div>
            <div>
              <dt>首小时预估</dt>
              <dd>时长包优先，最多扣 ¥{{ formatMoney(item.hourlyRate) }}</dd>
            </div>
          </dl>
        </div>
        <div class="card-action">
          <van-button type="primary" block @click="choose(item)">选择并开机</van-button>
        </div>
      </article>
    </div>

    <van-dialog
      v-model:show="verifyVisible"
      title="到店开机核验"
      show-cancel-button
      confirm-button-text="确认无误，去扣费"
      cancel-button-text="取消"
      @confirm="askConfirm"
    >
      <div v-if="selected" class="dialog-body">
        <van-notice-bar left-icon="passed" theme="success" :scrollable="false">
          会员 {{ selected.memberName }} 与机位 {{ selected.seatNo }}（{{ seatStatusLabel(selected.seatStatus) }}）核验通过
        </van-notice-bar>
        <ul class="verify-list">
          <li><span>预约单号</span><strong>{{ selected.reservationNo }}</strong></li>
          <li><span>预约时段</span><strong>今日 {{ formatTime(selected.startTime) }} – {{ formatTime(selected.endTime) }}</strong></li>
          <li><span>可用时长包</span><strong>{{ selected.packageRemainingMinutes }} 分钟</strong></li>
          <li><span>账户余额</span><strong>¥{{ formatMoney(selected.memberBalance) }}</strong></li>
          <li><span>扣费规则</span><strong>先扣时长包，不足部分按 ¥{{ formatMoney(selected.hourlyRate) }}/小时扣余额</strong></li>
        </ul>
      </div>
    </van-dialog>

    <van-dialog
      v-model:show="resultVisible"
      :title="result?.success ? '开机成功' : '余额不足，未开机'"
      confirm-button-text="知道了"
      :show-confirm-button="true"
    >
      <div v-if="result" class="dialog-body">
        <van-notice-bar
          :left-icon="result.success ? 'checked' : 'warning-o'"
          :theme="result.success ? 'success' : 'danger'"
          :scrollable="false"
        >
          {{ result.message }}
        </van-notice-bar>

        <div v-if="!result.success" class="shortfall-box">
          <p>需补差额：<strong class="danger-text">¥{{ formatMoney(result.shortfall) }}</strong></p>
          <p class="hint">预约单 {{ result.reservationNo }} 与机位 {{ result.seatNo }} 均保持原状态，充值后可直接重试。</p>
        </div>

        <ul class="verify-list">
          <li><span>会员</span><strong>{{ result.memberName }}</strong></li>
          <li><span>机位</span><strong>{{ result.seatNo }} {{ result.seatStatus ? `（${seatStatusLabel(result.seatStatus)}）` : "（状态未变）" }}</strong></li>
          <li><span>首小时费用</span><strong>¥{{ formatMoney(result.firstHourFee) }}</strong></li>
          <li><span>时长包抵扣</span><strong>{{ result.packageMinutesUsed }} 分钟</strong></li>
          <li v-if="result.success"><span>余额扣款</span><strong>¥{{ formatMoney(result.balanceUsed) }}</strong></li>
          <li v-else><span>当前余额</span><strong class="danger-text">¥{{ formatMoney(result.remainingBalance) }}</strong></li>
          <li v-if="result.success"><span>剩余时长包</span><strong>{{ result.remainingPackageMinutes }} 分钟</strong></li>
          <li v-if="result.success"><span>计划下机</span><strong>{{ formatDateTime(result.planEndTime) }}</strong></li>
        </ul>

        <div v-if="result.charges.length" class="charge-box">
          <p class="charge-title">本次扣费明细</p>
          <div v-for="(charge, index) in result.charges" :key="index" class="charge-line">
            <van-tag plain :type="charge.chargeType === 'PACKAGE' ? 'primary' : 'warning'">
              {{ chargeTypeLabel(charge.chargeType) }}
            </van-tag>
            <span>{{ charge.minutes }} 分钟</span>
            <strong>¥{{ formatMoney(charge.amount) }}</strong>
          </div>
        </div>
      </div>
    </van-dialog>
  </section>
</template>
