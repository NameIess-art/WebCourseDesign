<template>
  <div class="admin-layout">
    <section class="admin-hero">
      <div>
        <p class="eyebrow">平台控制台</p>
        <h1>运营总后台</h1>
        <p class="muted">平台侧负责全域活动、权限风控、配置中心、商家治理和数据分析。</p>
      </div>
      <button class="ghost-button" :disabled="loading" @click="loadData">刷新数据</button>
    </section>

    <div v-if="notice" class="feedback-banner" :class="notice.type">{{ notice.text }}</div>

    <section id="activities" class="module-section">
      <div class="section-head"><div><p class="eyebrow">Operation</p><h2>活动运营</h2></div></div>
      <div class="dual-grid">
        <form class="admin-form form-panel" @submit.prevent="saveActivity">
          <h3>全域活动配置</h3>
          <input v-model="activityForm.title" placeholder="活动名称" required />
          <select v-model="activityForm.type">
            <option value="FULL_REDUCTION">满减</option>
            <option value="SECKILL">秒杀会场</option>
            <option value="PRE_SALE">预售排期</option>
            <option value="GROUP_BUY">拼团会场</option>
            <option value="DISTRIBUTION">分销</option>
            <option value="BUNDLE">组合套餐</option>
          </select>
          <textarea v-model="activityForm.ruleText" placeholder="会场搭建、排期、规则说明" required></textarea>
          <button class="accent-button" :disabled="loading">新增活动</button>
        </form>
        <form class="admin-form form-panel" @submit.prevent="savePromotionRule">
          <h3>活动规则审核</h3>
          <input v-model="promotionForm.title" placeholder="规则名称" required />
          <select v-model="promotionForm.promotionType">
            <option value="COUPON">优惠券</option>
            <option value="FULL_REDUCTION">满减</option>
            <option value="POINTS">积分权益</option>
          </select>
          <div class="form-row">
            <input v-model.number="promotionForm.thresholdAmount" type="number" min="0" step="0.01" placeholder="门槛" />
            <input v-model.number="promotionForm.discountAmount" type="number" min="0" step="0.01" placeholder="优惠" />
          </div>
          <textarea v-model="promotionForm.ruleText" placeholder="规则文本" required></textarea>
          <button class="accent-button" :disabled="loading">保存规则</button>
        </form>
      </div>
      <div class="module-grid">
        <article v-for="activity in marketing.content" :key="activity.id" class="row-card">
          <div class="row-main">
            <strong>{{ activity.title }} · {{ formatStatus(activity.type) }}</strong>
            <span class="muted">{{ activity.ruleText }} · {{ formatStatus(activity.status) }}</span>
          </div>
          <div class="row-actions">
            <button class="ghost-button" @click="auditActivityItem(activity, true)">通过</button>
            <button class="ghost-button" @click="auditActivityItem(activity, false)">驳回</button>
          </div>
        </article>
      </div>
      <Pagination v-if="marketing.totalPages > 1" :page="marketing.page" :totalPages="marketing.totalPages" @change="p => getMarketing(p, 10).then(res => marketing = res.data)" />
    </section>

    <section id="risk" class="module-section">
      <div class="section-head"><div><p class="eyebrow">RBAC & Risk</p><h2>权限与风控</h2></div></div>
      <div class="module-grid">
        <form class="admin-form form-panel" @submit.prevent="saveRole">
          <h3>角色权限</h3>
          <input v-model="roleForm.title" placeholder="角色编码，如 AUDITOR" required />
          <input v-model="roleForm.type" placeholder="角色名称" required />
          <textarea v-model="roleForm.content" placeholder="菜单权限、按钮权限说明" required></textarea>
          <button class="accent-button" :disabled="loading">新增角色</button>
        </form>
        <form class="admin-form form-panel" @submit.prevent="savePermission">
          <h3>权限点</h3>
          <input v-model="permissionForm.title" placeholder="权限编码，如 platform:config:write" required />
          <input v-model="permissionForm.type" placeholder="权限类型，如 BUTTON" required />
          <textarea v-model="permissionForm.content" placeholder="权限说明" required></textarea>
          <button class="accent-button" :disabled="loading">新增权限</button>
        </form>
        <form class="admin-form form-panel" @submit.prevent="saveMerchant">
          <h3>商家管理</h3>
          <input v-model="merchantForm.title" placeholder="商家名称" required />
          <input v-model="merchantForm.type" placeholder="状态，如 ACTIVE" required />
          <textarea v-model="merchantForm.content" placeholder="联系方式或资质备注" required></textarea>
          <button class="accent-button" :disabled="loading">新增商家</button>
        </form>
        <form class="admin-form form-panel" @submit.prevent="saveContentAudit">
          <h3>内容审核</h3>
          <input v-model="contentForm.title" placeholder="内容目标" required />
          <select v-model="contentForm.type">
            <option value="REVIEW">评价</option>
            <option value="QUESTION">问答</option>
            <option value="PRODUCT_DETAIL">商品图文</option>
          </select>
          <textarea v-model="contentForm.content" placeholder="待审核内容" required></textarea>
          <button class="accent-button" :disabled="loading">加入审核池</button>
        </form>
      </div>
      <div class="dual-grid">
        <ListPanel title="风控事项" :items="risks.content">
          <template #default="{ item }">
            <div class="row-main"><strong>{{ item.target }}</strong><span class="muted">{{ formatStatus(item.riskType) }} · {{ item.description }} · {{ formatStatus(item.status) }}</span></div>
            <button class="ghost-button" @click="resolveRiskItem(item.id)">处理</button>
          </template>
        </ListPanel>
        <Pagination v-if="risks.totalPages > 1" :page="risks.page" :totalPages="risks.totalPages" @change="p => getRiskItems(p, 10).then(res => risks = res.data)" />
        <ListPanel title="商家处罚与启停" :items="merchants.content">
          <template #default="{ item }">
            <div class="row-main"><strong>{{ item.title }}</strong><span class="muted">{{ formatStatus(item.type) }} · {{ formatStatus(item.status) }}</span></div>
            <div class="row-actions">
              <button class="ghost-button" @click="changeMerchantStatus(item, '正常')">启用</button>
              <button class="ghost-button" @click="changeMerchantStatus(item, 'DISABLED')">停用</button>
              <button class="ghost-button" @click="penalty(item)">处罚</button>
            </div>
          </template>
        </ListPanel>
        <Pagination v-if="merchants.totalPages > 1" :page="merchants.page" :totalPages="merchants.totalPages" @change="p => getMerchants(p, 10).then(res => merchants = res.data)" />
      </div>
      <div class="dual-grid">
        <ListPanel title="角色与权限" :items="[...(roles.content || []), ...(permissions.content || [])]">
          <template #default="{ item }">
            <div class="row-main"><strong>{{ item.title }}</strong><span class="muted">{{ formatStatus(item.type) }} · {{ item.content }}</span></div>
          </template>
        </ListPanel>
        <ListPanel title="审核池" :items="contentAudits.content">
          <template #default="{ item }">
            <div class="row-main"><strong>{{ item.title }}</strong><span class="muted">{{ formatStatus(item.type) }} · {{ formatStatus(item.status) }}</span></div>
            <div class="row-actions">
              <button class="ghost-button" @click="auditContentItem(item, true)">通过</button>
              <button class="ghost-button" @click="auditContentItem(item, false)">驳回</button>
            </div>
          </template>
        </ListPanel>
        <Pagination v-if="contentAudits.totalPages > 1" :page="contentAudits.page" :totalPages="contentAudits.totalPages" @change="p => getContentAudits(p, 10).then(res => contentAudits = res.data)" />
      </div>
    </section>

    <section id="configs" class="module-section">
      <div class="section-head"><div><p class="eyebrow">Config Center</p><h2>配置中心</h2></div></div>
      <div class="module-grid">
        <form class="admin-form form-panel" @submit.prevent="saveConfig">
          <h3>系统参数 / 渠道配置</h3>
          <input v-model="configForm.key" placeholder="配置 Key" required />
          <input v-model="configForm.value" placeholder="配置值" required />
          <textarea v-model="configForm.description" placeholder="说明" required></textarea>
          <button class="accent-button" :disabled="loading">保存配置</button>
        </form>
        <form class="admin-form form-panel" @submit.prevent="saveDictionary">
          <h3>字典</h3>
          <input v-model="dictionaryForm.title" placeholder="字典 Key" required />
          <input v-model="dictionaryForm.type" placeholder="字典类型" required />
          <textarea v-model="dictionaryForm.content" placeholder="字典值" required></textarea>
          <button class="accent-button" :disabled="loading">新增字典</button>
        </form>
        <form class="admin-form form-panel" @submit.prevent="saveAnnouncement">
          <h3>弹窗 / 公告</h3>
          <input v-model="announcementForm.title" placeholder="公告标题" required />
          <select v-model="announcementForm.type">
            <option value="POPUP">弹窗</option>
            <option value="NOTICE">公告</option>
            <option value="CHANNEL">渠道通知</option>
          </select>
          <textarea v-model="announcementForm.content" placeholder="公告内容" required></textarea>
          <button class="accent-button" :disabled="loading">发布公告</button>
        </form>
      </div>
      <div class="module-grid">
        <SimpleRow v-for="item in configs.content" :key="item.configKey || item.key" :title="item.configKey || item.key" :type="item.configValue || item.value" :content="item.description">
          <button class="ghost-button" @click="removeConfig(item.configKey || item.key)">删除</button>
        </SimpleRow>
        <SimpleRow v-for="item in dictionaries.content" :key="`dict-${item.id}`" :title="item.title" :type="item.type" :content="item.content">
          <button class="ghost-button" @click="removeDictionary(item.id)">删除</button>
        </SimpleRow>
        <SimpleRow v-for="item in announcements.content" :key="`ann-${item.id}`" :title="item.title" :type="item.type" :content="item.content">
          <button class="ghost-button" @click="removeAnnouncement(item.id)">删除</button>
        </SimpleRow>
      </div>
    </section>

    <section id="analytics" class="module-section">
      <div class="section-head"><div><p class="eyebrow">Analytics</p><h2>数据分析</h2></div></div>
      <div class="stat-grid">
        <article class="stat-card"><span>平台模块</span><strong>{{ board.platformModules?.length ?? 0 }}</strong></article>
        <article class="stat-card"><span>商家模块</span><strong>{{ board.merchantModules?.length ?? 0 }}</strong></article>
        <article class="stat-card"><span>流量</span><strong>{{ reports.visitorCount ?? 0 }}</strong></article>
        <article class="stat-card"><span>营销转化</span><strong>{{ reports.conversionCount ?? 0 }}</strong></article>
      </div>
      <div class="module-grid">
        <article class="notice-box"><strong>用户画像</strong><span>{{ reports.userPortrait || '暂无画像数据' }}</span></article>
        <article class="notice-box"><strong>营销效果</strong><span>{{ reports.marketingEffect || '暂无营销效果数据' }}</span></article>
        <SimpleRow v-for="item in analytics" :key="item.id" :title="item.title" :type="item.type" :content="item.content" />
        <SimpleRow v-for="rule in promotionRules.content" :key="`rule-${rule.id}`" :title="rule.title" :type="rule.type" :content="rule.content" />
      </div>
    </section>
  </div>
</template>

<script setup>
import { formatStatus } from '../utils/format'
import { defineComponent, h, onMounted, reactive, ref } from 'vue'
import Pagination from '../components/Pagination.vue'
import {
  auditActivity,
  auditContent,
  createAnnouncement,
  createContentAudit,
  createDictionary,
  createMarketing,
  createMerchant,
  createPermission,
  createPromotionRule,
  createRole,
  deleteAnnouncement,
  deleteConfig,
  deleteDictionary,
  getAnalytics,
  getAnnouncements,
  getConfigs,
  getContentAudits,
  getDictionaries,
  getMarketing,
  getMerchants,
  getOperationBoard,
  getPermissions,
  getPromotionRules,
  getReports,
  getRiskItems,
  getRoles,
  penalizeMerchant,
  resolveRisk,
  updateConfig,
  updateMerchantStatus
} from '../api/admin'

const SimpleRow = defineComponent({
  props: { title: String, type: String, content: String },
  setup(props, { slots }) {
    return () => h('article', { class: 'row-card' }, [
      h('div', { class: 'row-main' }, [h('strong', props.title), h('span', { class: 'muted' }, `${props.type || ''} · ${props.content || ''}`)]),
      slots.default?.()
    ])
  }
})

const ListPanel = defineComponent({
  props: { title: String, items: Array },
  setup(props, { slots }) {
    return () => h('div', { class: 'stack-list' }, [
      h('h3', props.title),
      ...(props.items || []).map(item => h('article', { class: 'row-card', key: item.id || `${item.title}-${item.type}` }, slots.default?.({ item })))
    ])
  }
})

const loading = ref(false)
const notice = ref(null)
const board = ref({})
const reports = ref({})
const marketing = ref({ content: [], page: 0, totalPages: 0 })
const configs = ref({ content: [], page: 0, totalPages: 0 })
const dictionaries = ref({ content: [], page: 0, totalPages: 0 })
const announcements = ref({ content: [], page: 0, totalPages: 0 })
const risks = ref({ content: [], page: 0, totalPages: 0 })
const merchants = ref({ content: [], page: 0, totalPages: 0 })
const roles = ref({ content: [], page: 0, totalPages: 0 })
const permissions = ref({ content: [], page: 0, totalPages: 0 })
const contentAudits = ref({ content: [], page: 0, totalPages: 0 })
const promotionRules = ref({ content: [], page: 0, totalPages: 0 })
const analytics = ref([])

const activityForm = reactive({ title: '', type: 'FULL_REDUCTION', ruleText: '' })
const promotionForm = reactive({ title: '', promotionType: 'FULL_REDUCTION', thresholdAmount: 0, discountAmount: 0, ruleText: '' })
const roleForm = reactive({ title: 'OPERATOR', type: '运营专员', content: '' })
const permissionForm = reactive({ title: 'platform:view', type: 'MENU', content: '' })
const merchantForm = reactive({ title: '', type: '正常', content: '' })
const contentForm = reactive({ title: '', type: 'REVIEW', content: '' })
const configForm = reactive({ key: 'mall.channel.default', value: 'WEB', description: '默认渠道配置' })
const dictionaryForm = reactive({ title: '', type: 'CHANNEL', content: '' })
const announcementForm = reactive({ title: '', type: 'NOTICE', content: '' })

onMounted(loadData)

async function runAction(successText, action) {
  loading.value = true
  notice.value = null
  try {
    await action()
    notice.value = { type: 'success', text: successText }
    await loadData()
  } catch (error) {
    notice.value = { type: 'error', text: String(error) }
  } finally {
    loading.value = false
  }
}

async function loadData() {
  const [boardRes, marketingRes, configRes, dictRes, annRes, riskRes, merchantRes, roleRes, permissionRes, auditRes, ruleRes, analyticsRes, reportRes] = await Promise.all([
    getOperationBoard(), getMarketing(), getConfigs(), getDictionaries(), getAnnouncements(), getRiskItems(), getMerchants(),
    getRoles(), getPermissions(), getContentAudits(), getPromotionRules(), getAnalytics(), getReports()
  ])
  board.value = boardRes.data || {}
  marketing.value = marketingRes.data || { content: [], page: 0, totalPages: 0 }
  configs.value = configRes.data || { content: [], page: 0, totalPages: 0 }
  dictionaries.value = dictRes.data || { content: [], page: 0, totalPages: 0 }
  announcements.value = annRes.data || { content: [], page: 0, totalPages: 0 }
  risks.value = riskRes.data || { content: [], page: 0, totalPages: 0 }
  merchants.value = merchantRes.data || { content: [], page: 0, totalPages: 0 }
  roles.value = roleRes.data || { content: [], page: 0, totalPages: 0 }
  permissions.value = permissionRes.data || { content: [], page: 0, totalPages: 0 }
  contentAudits.value = auditRes.data || { content: [], page: 0, totalPages: 0 }
  promotionRules.value = ruleRes.data || { content: [], page: 0, totalPages: 0 }
  analytics.value = analyticsRes.data || []
  reports.value = reportRes.data || {}
}

async function saveActivity() {
  await runAction('活动已创建，等待审核', async () => {
    await createMarketing(activityForm)
    activityForm.title = ''
    activityForm.ruleText = ''
  })
}

async function auditActivityItem(item, approved) {
  await runAction(approved ? '活动审核通过' : '活动已驳回', () => auditActivity(item.id, approved))
}

async function savePromotionRule() {
  await runAction('活动规则已保存', async () => {
    await createPromotionRule(promotionForm)
    promotionForm.title = ''
    promotionForm.ruleText = ''
  })
}

async function saveRole() {
  await runAction('角色已创建', async () => {
    await createRole(roleForm)
    roleForm.content = ''
  })
}

async function savePermission() {
  await runAction('权限已创建', async () => {
    await createPermission(permissionForm)
    permissionForm.content = ''
  })
}

async function saveMerchant() {
  await runAction('商家已创建', async () => {
    const res = await createMerchant(merchantForm)
    window.alert(`商家创建成功！\n\n${res.data.content}`)
    merchantForm.title = ''
    merchantForm.content = ''
  })
}

async function changeMerchantStatus(item, status) {
  await runAction('商家状态已更新', () => updateMerchantStatus(item.id, status))
}

async function penalty(item) {
  await runAction('违规处罚已记录', () => penalizeMerchant(item.id, {
    title: `${item.title} 违规处罚`,
    type: 'PENALTY',
    content: '平台巡检发现违规，限制活动报名 7 天'
  }))
}

async function resolveRiskItem(id) {
  await runAction('风控事项已处理', () => resolveRisk(id))
}

async function saveContentAudit() {
  await runAction('内容已加入审核池', async () => {
    await createContentAudit(contentForm)
    contentForm.title = ''
    contentForm.content = ''
  })
}

async function auditContentItem(item, approved) {
  await runAction(approved ? '内容审核通过' : '内容已驳回', () => auditContent(item.id, approved))
}

async function saveConfig() {
  await runAction('配置已保存', () => updateConfig(configForm.key, { value: configForm.value, description: configForm.description }))
}

async function removeConfig(key) {
  await runAction('配置已删除', () => deleteConfig(key))
}

async function saveDictionary() {
  await runAction('字典已新增', async () => {
    await createDictionary(dictionaryForm)
    dictionaryForm.title = ''
    dictionaryForm.content = ''
  })
}

async function removeDictionary(id) {
  await runAction('字典已删除', () => deleteDictionary(id))
}

async function saveAnnouncement() {
  await runAction('公告已发布', async () => {
    await createAnnouncement(announcementForm)
    announcementForm.title = ''
    announcementForm.content = ''
  })
}

async function removeAnnouncement(id) {
  await runAction('公告已删除', () => deleteAnnouncement(id))
}
</script>
