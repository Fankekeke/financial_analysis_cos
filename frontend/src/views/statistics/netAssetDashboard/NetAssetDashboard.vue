
<template>
  <div class="net-asset-dashboard-container" style="width: 50%;margin: 0 auto">
    <!-- 页面标题 -->
    <a-card :bordered="false" style="margin-bottom: 16px; padding: 16px;">
      <div style="display: flex; align-items: center;">
        <a-icon type="dashboard" theme="twoTone" twoToneColor="#1890ff" style="font-size: 24px; margin-right: 12px;" />
        <h3 style="margin: 0;">资产总览</h3>
      </div>
    </a-card>

    <!-- 资产概览卡片 -->
    <a-row :gutter="16" style="margin-bottom: 16px;">
      <!-- 总资产 -->
      <a-col :span="8">
        <a-card :bordered="false" class="statistic-card" style="background: linear-gradient(135deg, #f5f5f5 0%, #f3f3f3 100%);">
          <div style="color: black; text-align: center;">
            <div style="display: flex; justify-content: center; margin-bottom: 8px;">
              <a-avatar size="small" icon="dollar" style="background: rgba(73,72,72,0.2);" />
            </div>
            <div style="font-size: 12px; margin-bottom: 4px; opacity: 0.9;">总资产</div>
            <div style="font-size: 20px; font-weight: bold; letter-spacing: 0.5px;">
              ¥{{ formatAmount(totalAssets) }}
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 总负债 -->
      <a-col :span="8">
        <a-card :bordered="false" class="statistic-card" style="background: linear-gradient(135deg, #f5f5f5 0%, #f3f3f3 100%);">
          <div style="color: black; text-align: center;">
            <div style="display: flex; justify-content: center; margin-bottom: 8px;">
              <a-avatar size="small" icon="arrow-down" style="background: rgba(73,72,72,0.2);" />
            </div>
            <div style="font-size: 12px; margin-bottom: 4px; opacity: 0.9;">总负债</div>
            <div style="font-size: 20px; font-weight: bold; letter-spacing: 0.5px;">
              ¥{{ formatAmount(totalLiabilities) }}
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 净资产 -->
      <a-col :span="8">
        <a-card :bordered="false" class="statistic-card" style="background: linear-gradient(135deg, #f5f5f5 0%, #f3f3f3 100%);">
          <div style="color: black; text-align: center;">
            <div style="display: flex; justify-content: center; margin-bottom: 8px;">
              <a-avatar size="small" icon="rise" style="background: rgba(73,72,72,0.2);" />
            </div>
            <div style="font-size: 12px; margin-bottom: 4px; opacity: 0.9;">净资产</div>
            <div style="font-size: 20px; font-weight: bold; letter-spacing: 0.5px;">
              ¥{{ formatAmount(netAssets) }}
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 资产分类统计 -->
    <a-card title="资产分类统计" style="margin-bottom: 16px; padding: 12px;" :bordered="false">
      <a-row :gutter="16">
        <a-col :span="8">
          <div class="category-item">
            <a-icon type="bank" theme="twoTone" twoToneColor="#1890ff" style="font-size: 24px; margin-bottom: 8px;" />
            <h4>账户资产</h4>
            <p><strong>{{ accountsCount }}</strong> 个账户</p>
          </div>
        </a-col>
        <a-col :span="8">
          <div class="category-item">
            <a-icon type="database" theme="twoTone" twoToneColor="#52c41a" style="font-size: 24px; margin-bottom: 8px;" />
            <h4>固定资产</h4>
            <p><strong>{{ fixedAssetsCount }}</strong> 项资产</p>
          </div>
        </a-col>
        <a-col :span="8">
          <div class="category-item">
            <a-icon type="file-text" theme="twoTone" twoToneColor="#d4380d" style="font-size: 24px; margin-bottom: 8px;" />
            <h4>债务记录</h4>
            <p><strong>{{ debtsCount }}</strong> 笔债务</p>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 资产构成图表 -->
    <a-card title="资产构成分析" style="height: 300px;" :bordered="false">
      <apexchart
        type="donut"
        height="220"
        :options="pieChartOptions"
        :series="pieChartSeries"
      />
    </a-card>
  </div>
</template>

<script>import {mapState} from 'vuex'
import moment from 'moment'

export default {
  name: 'NetAssetDashboard',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      loading: false,
      totalAssets: 0,
      totalLiabilities: 0,
      netAssets: 0,
      accountsCount: 0,
      debtsCount: 0,
      fixedAssetsCount: 0,

      // 饼图配置
      pieChartOptions: {
        chart: {
          type: 'donut',
          toolbar: {
            show: false
          }
        },
        labels: ['流动资产', '固定资产', '负债'],
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              width: 200
            },
            legend: {
              position: 'bottom'
            }
          }
        }],
        colors: ['#52c41a', '#1890ff', '#d4380d'],
        legend: {
          position: 'right'
        },
        dataLabels: {
          enabled: true,
          formatter: function(val, opts) {
            const seriesIndex = opts.seriesIndex
            const label = opts.w.globals.labels[seriesIndex]
            const value = opts.w.config.series[seriesIndex]
            return `${label}\n¥${value.toFixed(2)}`
          }
        },
        plotOptions: {
          pie: {
            donut: {
              labels: {
                show: true,
                name: {
                  show: true,
                  fontSize: '14px',
                  fontFamily: 'Helvetica, Arial, sans-serif',
                  fontWeight: 600,
                  color: undefined,
                  offsetY: -10
                },
                value: {
                  show: true,
                  fontSize: '16px',
                  fontFamily: 'Helvetica, Arial, sans-serif',
                  fontWeight: 600,
                  color: undefined,
                  offsetY: 16,
                  formatter: function (val) {
                    return `¥${val}`
                  }
                },
                total: {
                  show: true,
                  showAlways: true,
                  label: '总资产',
                  fontSize: '14px',
                  fontFamily: 'Helvetica, Arial, sans-serif',
                  fontWeight: 600,
                  color: '#373d3f',
                  formatter: function (w) {
                    return `¥${w.globals.seriesTotals.reduce((a, b) => a + b, 0).toFixed(2)}`
                  }
                }
              }
            }
          }
        }
      },
      pieChartSeries: []
    }
  },
  mounted () {
    this.queryNetAssetDashboard()
  },
  methods: {
    queryNetAssetDashboard () {
      this.loading = true
      this.$get('/business/web/queryNetAssetDashboard', {
        userId: this.currentUser.userId
      }).then((r) => {
        this.loading = false
        if (r.data.code === 0) {
          const data = r.data

          this.totalAssets = data.totalAssets || 0
          this.totalLiabilities = data.totalLiabilities || 0
          this.netAssets = data.netAssets || 0
          this.accountsCount = data.accountsCount || 0
          this.debtsCount = data.debtsCount || 0
          this.fixedAssetsCount = data.fixedAssetsCount || 0

          // 计算饼图数据 - 假设流动资产为总资产减去固定资产
          const currentAssets = this.totalAssets - (this.fixedAssetsCount > 0 ? 5000000 : 0) // 根据实际情况调整
          const fixedAssetsValue = this.fixedAssetsCount > 0 ? 5000000 : 0

          this.pieChartSeries = [
            currentAssets,
            fixedAssetsValue,
            this.totalLiabilities
          ]
        }
      }).catch(() => {
        this.$message.error('获取数据失败')
        this.loading = false
      })
    },

    formatAmount(amount) {
      return parseFloat(amount || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>.net-asset-dashboard-container {
  padding: 16px;
  background-color: #f5f7fa;
}

.statistic-card {
  border-radius: 8px !important;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: transform 0.2s ease;
}

.statistic-card:hover {
  transform: translateY(-1px);
}

.category-item {
  text-align: center;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.category-item:hover {
  background: #f0f5ff;
  transform: translateY(-1px);
}

.category-item h4 {
  margin: 8px 0;
  color: #333;
}

.category-item p {
  margin: 4px 0 0 0;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ant-col {
    margin-bottom: 12px;
  }
}
</style>
