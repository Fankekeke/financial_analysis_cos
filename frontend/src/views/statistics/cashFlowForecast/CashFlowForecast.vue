
<template>
  <div class="cash-flow-forecast-container" style="width: 60%;margin: 0 auto">
    <!-- 当前现金概览 -->
    <a-card :bordered="false" style="margin-bottom: 16px; padding: 16px;">
      <div style="display: flex; align-items: center;">
        <a-icon type="dollar" theme="twoTone" twoToneColor="#52c41a" style="font-size: 24px; margin-right: 12px;" />
        <div>
          <h3 style="margin: 0;">当前可用现金</h3>
          <p style="margin: 8px 0 0 0; font-size: 24px; font-weight: bold; color: #52c41a;">
            ¥{{ formatAmount(currentCash) }}
          </p>
        </div>
      </div>
    </a-card>

    <!-- 现金流指标 -->
    <a-row :gutter="12" style="margin-bottom: 16px;">
      <a-col :span="6">
        <a-card :bordered="false" style="background: #f9f9f9; padding: 12px;">
          <div style="text-align: center;">
            <div style="color: #999; font-size: 14px; margin-bottom: 4px;">总收入</div>
            <div style="font-weight: bold;">¥{{ formatAmount(cashFlowMetrics.totalIncome) }}</div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" style="background: #f9f9f9; padding: 12px;">
          <div style="text-align: center;">
            <div style="color: #999; font-size: 14px; margin-bottom: 4px;">总支出</div>
            <div style="font-weight: bold;">¥{{ formatAmount(cashFlowMetrics.totalExpense) }}</div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" style="background: #f9f9f9; padding: 12px;">
          <div style="text-align: center;">
            <div style="color: #999; font-size: 14px; margin-bottom: 4px;">净现金流</div>
            <div style="font-weight: bold;">¥{{ formatAmount(cashFlowMetrics.netCashFlow) }}</div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" style="background: #f9f9f9; padding: 12px;">
          <div style="text-align: center;">
            <div style="color: #999; font-size: 14px; margin-bottom: 4px;">利润率</div>
            <div style="font-weight: bold;">{{ (cashFlowMetrics.cashFlowMargin * 100).toFixed(1) }}%</div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 风险预警 -->
    <a-card title="风险预警" style="margin-bottom: 16px; padding: 12px;" :bordered="false">
      <a-alert
        v-if="riskWarning.length === 0"
        message="当前无财务风险，现金流状况稳定"
        type="success"
        show-icon        style="font-size: 14px;"
      />
      <a-list
        v-else
        item-layout="horizontal"
        :data-source="riskWarning"
        :split="false"
        size="small"
      >
        <a-list-item slot="renderItem" slot-scope="item, index">
          <a-list-item-meta>
            <div slot="title" style="display: flex; align-items: center;">
              <a-tag :color="getRiskLevelColor(item.level)" size="small">{{ item.level }}</a-tag>
              <span style="margin-left: 8px; font-weight: 500;">{{ item.type }}</span>
            </div>
            <div slot="description">
              {{ item.message }}
              <div style="font-size: 12px; color: #999; margin-top: 4px;">
                相关数据：{{ item.metrics }}
              </div>
            </div>
          </a-list-item-meta>
        </a-list-item>
      </a-list>
    </a-card>

    <!-- 每日预测图表 -->
    <a-card title="未来30天现金流动态预测" style="margin-bottom: 16px; padding: 12px;" :bordered="false">
      <div style="height: 300px; position: relative;">
        <div style="position: absolute; top: 0; left: 0; right: 0; bottom: 0;">
          <apexchart
            type="line"
            height="100%"
            :options="chartOptions"
            :series="chartSeries"
          />
        </div>
      </div>
    </a-card>

    <!-- 预测日期范围 -->
    <a-card style="margin-bottom: 16px; padding: 12px; text-align: center; background: #f0f5ff;">
      <span>预测周期：{{ forecastStartDate }} 至 {{ forecastEndDate }}（共 {{ dailyForecast.length }} 天）</span>
    </a-card>

    <!-- 每日详细数据 -->
    <a-card title="每日预测详情" style="padding: 12px;" :bordered="false">
      <a-table
        :columns="forecastColumns"
        :dataSource="dailyForecast"
        :pagination="{ pageSize: 10 }"
        rowKey="dayIndex"
        size="small"
        :row-class-name="(record, index) => record.isRisk ? 'risk-row' : ''"
      >
        <template slot="date" slot-scope="text, record">
          {{ moment(text).format('MM-DD') }}
          <div style="font-size: 12px; color: #999;">第{{ record.dayIndex }}天</div>
        </template>
        <template slot="expectedIncome" slot-scope="text, record">
          <span style="color: #52c41a;">+¥{{ formatAmount(text) }}</span>
        </template>
        <template slot="expectedExpense" slot-scope="text, record">
          <span style="color: #d4380d;">-¥{{ formatAmount(text) }}</span>
        </template>
        <template slot="netFlow" slot-scope="text, record">
          <span :style="{ color: text >= 0 ? '#52c41a' : '#d4380d' }">
            {{ text >= 0 ? '+' : '' }}¥{{ formatAmount(text) }}
          </span>
        </template>
        <template slot="predictedBalance" slot-scope="text, record">
          <span :style="{ fontWeight: 'bold', color: record.isRisk ? '#d4380d' : '#1890ff' }">
            ¥{{ formatAmount(text) }}
          </span>
        </template>
        <template slot="isRisk" slot-scope="text, record">
          <a-tag v-if="text" color="red">风险</a-tag>
          <a-tag v-else color="green">安全</a-tag>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script>import {mapState} from 'vuex'
import moment from 'moment'

export default {
  name: 'CashFlowForecast',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      loading: false,
      currentCash: 0,
      forecastStartDate: '',
      forecastEndDate: '',
      cashFlowMetrics: {
        totalIncome: 0,
        totalExpense: 0,
        netCashFlow: 0,
        cashFlowMargin: 0,
        positiveCashFlowDays: 0,
        volatility: 0,
        stabilityLevel: ''
      },
      riskWarning: [],
      dailyForecast: [],
      chartOptions: {
        chart: {
          zoom: {
            enabled: false
          },
          toolbar: {
            show: false
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          curve: 'smooth',
          width: 3
        },
        xaxis: {
          type: 'category',
          categories: [],
          labels: {
            rotate: -45
          }
        },
        yaxis: {
          title: {
            text: '余额 (¥)'
          },
          labels: {
            formatter: function(val) {
              return val.toFixed(0)
            }
          }
        },
        tooltip: {
          x: {
            format: 'dd/MM/yy'
          },
          y: {
            formatter: function(val) {
              return '¥' + val.toFixed(2)
            }
          }
        },
        colors: ['#1890ff']
      },
      chartSeries: [{
        name: '预测余额',
        data: []
      }],
      forecastColumns: [
        {
          title: '日期',
          dataIndex: 'date',
          key: 'date',
          scopedSlots: { customRender: 'date' },
          sorter: (a, b) => new Date(a.date) - new Date(b.date)
        },
        {
          title: '预期收入',
          dataIndex: 'expectedIncome',
          key: 'expectedIncome',
          scopedSlots: { customRender: 'expectedIncome' },
          align: 'right'
        },
        {
          title: '预期支出',
          dataIndex: 'expectedExpense',
          key: 'expectedExpense',
          scopedSlots: { customRender: 'expectedExpense' },
          align: 'right'
        },
        {
          title: '净现金流',
          dataIndex: 'netFlow',
          key: 'netFlow',
          scopedSlots: { customRender: 'netFlow' },
          align: 'right'
        },
        {
          title: '目标储蓄',
          dataIndex: 'goalSavings',
          key: 'goalSavings',
          scopedSlots: { customRender: 'goalSavings' },
          align: 'right'
        },
        {
          title: '预算支出',
          dataIndex: 'budgetExpense',
          key: 'budgetExpense',
          scopedSlots: { customRender: 'budgetExpense' },
          align: 'right'
        },
        {
          title: '预测余额',
          dataIndex: 'predictedBalance',
          key: 'predictedBalance',
          scopedSlots: { customRender: 'predictedBalance' },
          align: 'right',
          sorter: (a, b) => a.predictedBalance - b.predictedBalance
        },
        {
          title: '状态',
          dataIndex: 'isRisk',
          key: 'isRisk',
          scopedSlots: { customRender: 'isRisk' },
          align: 'center'
        }
      ]
    }
  },
  mounted () {
    this.queryCashFlowForecast()
  },
  methods: {
    moment,
    queryCashFlowForecast () {
      this.loading = true
      this.$get('/business/web/queryCashFlowForecast', {
        userId: this.currentUser.userId
      }).then((r) => {
        this.loading = false
        if (r.data.code === 0) {
          const data = r.data
          this.currentCash = data.currentCash || 0
          this.forecastStartDate = data.forecastStartDate
          this.forecastEndDate = data.forecastEndDate
          this.cashFlowMetrics = data.cashFlowMetrics || {}
          this.riskWarning = data.riskWarning || []
          this.dailyForecast = data.dailyForecast || []

          // 准备图表数据
          this.chartOptions.xaxis.categories = this.dailyForecast.map(item => moment(item.date).format('MM-DD'))
          this.chartSeries[0].data = this.dailyForecast.map(item => item.predictedBalance)
        }
      }).catch(() => {
        this.$message.error('获取数据失败')
        this.loading = false
      })
    },
    formatAmount(amount) {
      return parseFloat(amount || 0).toFixed(2)
    },
    getRiskLevelColor(level) {
      switch (level) {
        case 'critical':
          return 'red'
        case 'high':
          return 'orange'
        case 'medium':
          return 'gold'
        default:
          return 'blue'
      }
    }
  }
}
</script>

<style scoped>.cash-flow-forecast-container {
  padding: 16px;
  background-color: #f5f7fa;
}

.risk-row {
  background-color: #fff1f0 !important;
}

.ant-table-small .ant-table-content {
  overflow-x: auto;
}
</style>
