
<template>
  <div class="multidimensional-statistics-container" style="width: 60%;margin:  0 auto">
    <!-- 页面标题 -->
    <a-card :bordered="false" style="margin-bottom: 16px; padding: 16px;">
      <div style="display: flex; align-items: center;">
        <a-icon type="bar-chart" theme="twoTone" twoToneColor="#1890ff" style="font-size: 24px; margin-right: 12px;" />
        <h3 style="margin: 0;">多维度财务分析</h3>
      </div>
    </a-card>

    <!-- 统计图表区域 -->
    <a-row :gutter="16">
      <!-- 饼图 - 支出占比 -->
      <a-col :span="8">
        <a-card title="支出分类占比" style="margin-bottom: 16px; height: 400px;" :bordered="false">
          <apexchart
            type="pie"
            height="320"
            :options="pieChartOptions"
            :series="pieChartSeries"
          />
        </a-card>
      </a-col>

      <!-- 折线图 - 收支趋势 -->
      <a-col :span="16">
        <a-card title="收支趋势分析" style="margin-bottom: 16px; height: 400px;" :bordered="false">
          <apexchart
            type="line"
            height="320"
            :options="lineChartOptions"
            :series="lineChartSeries"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 热力图 - 消费频率 -->
    <a-card title="消费频率热力图" style="height: 300px;" :bordered="false">
      <apexchart
        type="heatmap"
        height="220"
        :options="heatmapOptions"
        :series="heatmapSeries"
      />
    </a-card>
  </div>
</template>

<script>import {mapState} from 'vuex'
import moment from 'moment'

export default {
  name: 'MultidimensionalStatistics',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      loading: false,
      pieChart: [],
      lineChart: {
        '收入': [],
        '支出': [],
        '类别': []
      },
      heatmap: [],

      // 饼图配置
      pieChartOptions: {
        chart: {
          type: 'pie',
          toolbar: {
            show: false
          }
        },
        labels: ['123', '312'],
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
        colors: ['#52c41a', '#d4380d', '#1890ff', '#722ed1', '#faad14', '#eb2f96'],
        legend: {
          position: 'right'
        },
        dataLabels: {
          enabled: true,
          formatter: function (val, opts) {
            return opts.w.globals.labels[opts.seriesIndex] + ': ' + val.toFixed(1) + '%'
          }
        }
      },
      pieChartSeries: [],

      // 折线图配置
      lineChartOptions: {
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
          width: [3, 3]
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
            text: '金额 (¥)'
          },
          labels: {
            formatter: function (val) {
              return val.toFixed(0)
            }
          }
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return '¥' + val.toFixed(2)
            }
          }
        },
        colors: ['#52c41a', '#d4380d'],
        legend: {
          position: 'top'
        }
      },
      lineChartSeries: [],

      // 热力图配置
      heatmapOptions: {
        chart: {
          type: 'heatmap',
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          heatmap: {
            shadeIntensity: 0.5,
            colorScale: {
              ranges: [
                {from: 0, to: 0, color: '#ebedf0', name: '无'},
                {from: 1, to: 1, color: '#c6e48b', name: '低'},
                {from: 2, to: 5, color: '#7bc96f', name: '中'},
                {from: 6, to: 10, color: '#239a3b', name: '高'}
              ]
            }
          }
        },
        dataLabels: {
          enabled: false
        },
        xaxis: {
          type: 'category',
          categories: [],
          labels: {
            format: 'MMM'
          }
        },
        yaxis: {
          title: {
            text: '年份'
          }
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return '交易次数: ' + val
            }
          }
        }
      },
      heatmapSeries: []
    }
  },
  mounted () {
    this.queryMultidimensionalStatistics()
  },
  methods: {
    moment,
    queryMultidimensionalStatistics () {
      this.loading = true
      this.$get('/business/web/queryMultidimensionalStatistics', {
        userId: this.currentUser.userId
      }).then((r) => {
        this.loading = false
        if (r.data.code === 0) {
          const data = r.data

          // 处理饼图数据
          this.pieChart = data.pieChart || []
          this.pieChartOptions.labels = this.pieChart.map(item => item.name)
          this.pieChartSeries = this.pieChart.map(item => item.value)

          // 确保饼图配置中的labels被正确应用
          this.pieChartOptions = {
            ...this.pieChartOptions,
            labels: this.pieChart.map(item => item.name),
            dataLabels: {
              enabled: true,
              formatter: function (val, opts) {
                // 获取对应的数据项
                const seriesIndex = opts.seriesIndex
                const label = opts.w.globals.labels[seriesIndex]
                const value = opts.w.config.series[seriesIndex]
                return `${label}\n¥${value.toFixed(2)}`
              },
              style: {
                fontSize: '12px',
                fontWeight: 'bold'
              },
              background: {
                enabled: true,
                foreColor: '#fff',
                padding: 4,
                borderRadius: 2,
                borderWidth: 0
              }
            },
            legend: {
              show: true,
              position: 'bottom',
              offsetY: 10,
              itemMargin: {
                horizontal: 8,
                vertical: 4
              },
              markers: {
                width: 8,
                height: 8,
                radius: 4
              }
            }
          }

          // 处理折线图数据
          this.lineChart = data.lineChart || {}
          this.lineChartOptions.xaxis.categories = this.lineChart.类别 || []
          this.lineChartSeries = [
            {
              name: '收入',
              data: this.lineChart.收入 || []
            },
            {
              name: '支出',
              data: this.lineChart.支出.map(value => -value) || [] // 支出为负值
            }
          ]

          // 处理热力图数据
          this.heatmap = data.heatmap || []
          this.processHeatmapData()
        }
      }).catch(() => {
        this.$message.error('获取数据失败')
        this.loading = false
      })
    },

    processHeatmapData () {
      // 将热力图数据按月份和年份分组
      const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
      const years = [...new Set(this.heatmap.map(item => moment(item.date).year()))].sort().reverse()

      this.heatmapOptions.xaxis.categories = months

      // 创建热力图系列
      this.heatmapSeries = years.map(year => {
        const data = months.map(month => {
          const dateStr = `${year}-${(months.indexOf(month) + 1).toString().padStart(2, '0')}-01`
          const item = this.heatmap.find(h => h.date.startsWith(dateStr.substring(0, 7)))
          return item ? item.count : 0
        })

        return {
          name: year.toString(),
          data: data
        }
      })
    },

    formatAmount (amount) {
      return parseFloat(amount || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>.multidimensional-statistics-container {
  padding: 16px;
  background-color: #f5f7fa;
}

.ant-card-body {
  padding: 16px !important;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .ant-col-8 {
    margin-bottom: 16px;
  }

  .ant-col-16 {
    margin-bottom: 16px;
  }
}
</style>
