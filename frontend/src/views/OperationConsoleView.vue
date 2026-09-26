<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { showNotify } from "vant";
import { fetchActiveSessions, fetchRecentSessions, fetchSeats } from "../api/console";
import type { Seat, UsageSession } from "../types";
import { chargeTypeLabel, formatDateTime, formatMoney, seatStatusLabel } from "../utils/format";

const seats = ref<Seat[]>([]);
const activeSessions = ref<UsageSession[]>([]);
const recentSessions = ref<UsageSession[]>([]);
const activeTab = ref(0);
const loading = ref(false);

const activeSeatIds = computed(() => new Set(activeSessions.value.map((session) => session.seatId)));

async function loadAll() {
  loading.value = true;
  try {
    const [seatList, activeList, recentList] = await Promise.all([
      fetchSeats(),
      fetchActiveSessions(),
      fetchRecentSessions(),
    ]);
    seats.value = seatList;
    activeSessions.value = activeList;
    recentSessions.value = recentList;
  } catch (error) {
    showNotify({ type: "danger", message: (error as Error).message });
  } finally {
    loading.value = false;
  }
}

function seatClass(status: string): string {
  return `seat-tile seat-${status.toLowerCase()}`;
}

onMounted(loadAll);
</script>

<template>
  <section class="page">
    <div class="page-head">
      <div>
        <h2>运营台 · 机位与上机</h2>
        <p>查看机位实时状态、上机中的机位，以及每笔开机扣费明细。</p>
      </div>
      <van-button type="primary" size="small" icon="replay" :loading="loading" @click="loadAll">
        刷新
      </van-button>
    </div>

    <van-tabs v-model:active="activeTab" sticky>
      <van-tab title="机位看板">
        <div class="seat-board">
          <div v-for="seat in seats" :key="seat.id" :class="seatClass(seat.status)">
            <div class="seat-no">{{ seat.seatNo }}</div>
            <div class="seat-area">{{ seat.area }}</div>
            <div class="seat-meta">
              <span>{{ seat.seatType }}</span>
              <van-tag :type="seat.status === 'IN_USE' ? 'danger' : seat.status === 'RESERVED' ? 'warning' : seat.status === 'FAULT' ? 'default' : 'success'">
                {{ seatStatusLabel(seat.status) }}
              </van-tag>
            </div>
            <div class="seat-rate">¥{{ formatMoney(seat.hourlyRate) }}/小时</div>
          </div>
        </div>
        <ul class="legend">
          <li><i class="dot seat-idle"></i>空闲</li>
          <li><i class="dot seat-in_use"></i>使用中</li>
          <li><i class="dot seat-reserved"></i>已预约</li>
          <li><i class="dot seat-fault"></i>故障</li>
        </ul>
      </van-tab>

      <van-tab :title="`上机中（${activeSessions.length}）`">
        <van-empty v-if="activeSessions.length === 0" description="当前没有上机中的机位" />
        <article v-for="session in activeSessions" :key="session.id" class="session-card">
          <header class="session-head">
            <div>
              <strong>{{ session.seatNo }}</strong>
              <span class="session-area">{{ session.seatArea }} · {{ session.seatType }}</span>
            </div>
            <van-tag type="danger">使用中</van-tag>
          </header>
          <dl class="verify-grid">
            <div>
              <dt>会员</dt>
              <dd>{{ session.memberName }}（{{ session.memberNo }}）</dd>
            </div>
            <div>
              <dt>开机时间</dt>
              <dd>{{ formatDateTime(session.startTime) }}</dd>
            </div>
            <div>
              <dt>计划下机</dt>
              <dd>{{ formatDateTime(session.planEndTime) }}</dd>
            </div>
            <div v-if="session.reservationNo">
              <dt>预约单号</dt>
              <dd>{{ session.reservationNo }}</dd>
            </div>
          </dl>
          <div class="charge-box">
            <p class="charge-title">本次扣费（已预付 {{ session.chargedMinutes }} 分钟）</p>
            <div v-for="charge in session.charges" :key="charge.id" class="charge-line">
              <van-tag plain :type="charge.chargeType === 'PACKAGE' ? 'primary' : 'warning'">
                {{ chargeTypeLabel(charge.chargeType) }}
              </van-tag>
              <span>{{ charge.minutes }} 分钟</span>
              <strong>¥{{ formatMoney(charge.amount) }}</strong>
            </div>
            <p v-if="session.charges.length === 0" class="hint">暂无扣费记录</p>
          </div>
        </article>
      </van-tab>

      <van-tab title="最近扣费">
        <van-empty v-if="recentSessions.length === 0" description="暂无上机记录" />
        <article v-for="session in recentSessions" :key="session.id" class="session-card compact">
          <header class="session-head">
            <div>
              <strong>{{ session.seatNo }}</strong>
              <span class="session-area">{{ session.memberName }} · {{ session.reservationNo ?? "散客开机" }}</span>
            </div>
            <van-tag :type="session.status === 'IN_USE' ? 'danger' : 'success'">
              {{ seatStatusLabel(session.status === 'IN_USE' ? 'IN_USE' : 'IDLE') }}
            </van-tag>
          </header>
          <p class="session-time">{{ formatDateTime(session.startTime) }} 开机 · {{ session.chargeSummary }}</p>
          <div class="charge-line" v-for="charge in session.charges" :key="charge.id">
            <van-tag plain :type="charge.chargeType === 'PACKAGE' ? 'primary' : 'warning'">
              {{ chargeTypeLabel(charge.chargeType) }}
            </van-tag>
            <span>{{ charge.minutes }} 分钟</span>
            <strong>¥{{ formatMoney(charge.amount) }}</strong>
          </div>
        </article>
      </van-tab>
    </van-tabs>

    <p v-if="activeSeatIds.size" class="sync-hint">上机中机位 {{ activeSeatIds.size }} 个，与机位看板实时联动。</p>
  </section>
</template>
