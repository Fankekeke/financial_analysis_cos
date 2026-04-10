
<template>
  <div class="home-container">
    <!-- 统计概览 -->
    <a-row :gutter="16" style="margin-bottom: 24px;color: black">
      <a-col :span="8">
        <a-card :bordered="false" style="background: linear-gradient(135deg, #ffffff 0%, #f3f3f3 100%);">
          <div style="color: black; text-align: center;">
            <a-icon type="dollar" style="font-size: 28px; margin-bottom: 8px;" />
            <h3 style="margin: 0;">总资产</h3>
            <p style="margin: 8px 0 0 0; font-size: 24px; font-weight: bold;">¥{{ formatAmount(homeData.totalAssets) }}</p>
          </div>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card :bordered="false" style="background: linear-gradient(135deg, #ffffff 0%, #f3f3f3 100%);">
          <div style="color: black; text-align: center;">
            <a-icon type="arrow-down" style="font-size: 28px; margin-bottom: 8px;" />
            <h3 style="margin: 0;">总负债</h3>
            <p style="margin: 8px 0 0 0; font-size: 24px; font-weight: bold;">¥{{ formatAmount(homeData.totalLiabilities) }}</p>
          </div>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card :bordered="false" style="background: linear-gradient(135deg, #ffffff 0%, #f3f3f3 100%);">
          <div style="color: black; text-align: center;">
            <a-icon type="rise" style="font-size: 28px; margin-bottom: 8px;" />
            <h3 style="margin: 0;">净资产</h3>
            <p style="margin: 8px 0 0 0; font-size: 24px; font-weight: bold;">¥{{ formatAmount(homeData.totalNetAssets) }}</p>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 用户统计 -->
    <a-card title="用户统计" style="margin-bottom: 24px;">
      <a-table
        :columns="userColumns"
        :dataSource="homeData.userStats"
        :pagination="false"
        rowKey="userId"
      >
        <template slot="netAsset" slot-scope="text, record">
          ¥{{ formatAmount(text) }}
        </template>
        <template slot="fixedAssetValue" slot-scope="text, record">
          ¥{{ formatAmount(text) }}
        </template>
        <template slot="accountBalance" slot-scope="text, record">
          ¥{{ formatAmount(text) }}
        </template>
        <template slot="debtAmount" slot-scope="text, record">
          ¥{{ formatAmount(text) }}
        </template>
      </a-table>
    </a-card>

    <!-- 平台概览 -->
    <a-card title="平台概览" style="margin-bottom: 24px;">
      <a-row :gutter="16">
        <a-col :span="6">
          <div style="text-align: center; padding: 16px;">
            <a-statistic
              title="用户总数"
              :value="homeData.userCount"
              :value-style="{ fontSize: '24px', color: '#3f8600' }"
            />
          </div>
        </a-col>
        <a-col :span="6">
          <div style="text-align: center; padding: 16px;">
            <a-statistic
              title="交易总数"
              :value="homeData.totalTransactionCount"
              :value-style="{ fontSize: '24px', color: '#1890ff' }"
            />
          </div>
        </a-col>
        <a-col :span="6">
          <div style="text-align: center; padding: 16px;">
            <a-statistic
              title="总收入"
              :value="homeData.totalIncomeAllTime"
              :value-style="{ fontSize: '24px', color: '#52c41a' }"
              suffix="次"
            />
          </div>
        </a-col>
        <a-col :span="6">
          <div style="text-align: center; padding: 16px;">
            <a-statistic
              title="总支出"
              :value="homeData.totalExpenseAllTime"
              :value-style="{ fontSize: '24px', color: '#d4380d' }"
              suffix="次"
            />
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 当月财务 -->
    <a-card title="当月财务" style="margin-bottom: 24px;">
      <a-row :gutter="16">
        <a-col :span="8">
          <div style="text-align: center; padding: 16px; border-right: 1px solid #e8e8e8;">
            <a-statistic
              title="本月收入"
              :value="homeData.currentMonthIncome"
              :value-style="{ fontSize: '24px', color: '#52c41a' }"
              prefix="¥"
            />
          </div>
        </a-col>
        <a-col :span="8">
          <div style="text-align: center; padding: 16px; border-right: 1px solid #e8e8e8;">
            <a-statistic
              title="本月支出"
              :value="homeData.currentMonthExpense"
              :value-style="{ fontSize: '24px', color: '#d4380d' }"
              prefix="¥"
            />
          </div>
        </a-col>
        <a-col :span="8">
          <div style="text-align: center; padding: 16px;">
            <a-statistic
              title="本月净收入"
              :value="homeData.currentMonthNetIncome"
              :value-style="{ fontSize: '24px', color: homeData.currentMonthNetIncome >= 0 ? '#3f8600' : '#cf1322' }"
              prefix="¥"
            />
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script>import {mapState} from 'vuex'
export default {
  name: 'Home',
  computed: {
    ...mapState({
      multipage: state => state.setting.multipage,
      user: state => state.account.user
    })
  },
  data () {
    return {
      homeData: {
        totalAssets: 0,
        totalLiabilities: 0,
        totalNetAssets: 0,
        userCount: 0,
        totalTransactionCount: 0,
        totalIncomeAllTime: 0,
        totalExpenseAllTime: 0,
        currentMonthIncome: 0,
        currentMonthExpense: 0,
        currentMonthNetIncome: 0,
        averageIncomePerUser: 0,
        averageExpensePerUser: 0,
        userStats: []
      },
      userColumns: [
        {
          title: '用户名',
          dataIndex: 'userName',
          key: 'userName',
          scopedSlots: { customRender: 'userName' }
        },
        {
          title: '账户余额',
          dataIndex: 'accountBalance',
          key: 'accountBalance',
          scopedSlots: { customRender: 'accountBalance' }
        },
        {
          title: '固定资产价值',
          dataIndex: 'fixedAssetValue',
          key: 'fixedAssetValue',
          scopedSlots: { customRender: 'fixedAssetValue' }
        },
        {
          title: '负债金额',
          dataIndex: 'debtAmount',
          key: 'debtAmount',
          scopedSlots: { customRender: 'debtAmount' }
        },
        {
          title: '净资产',
          dataIndex: 'netAsset',
          key: 'netAsset',
          scopedSlots: { customRender: 'netAsset' }
        }
      ]
    }
  },
  mounted () {
    this.queryHomeByAdmin()
  },
  methods: {
    queryHomeByAdmin () {
      this.$get('/business/web/queryHomeByAdmin').then((r) => {
        if (r.data.code === 0) {
          this.homeData = r.data
        }
      }).catch(() => {
        this.$message.error('获取数据失败')
      })
    },
    formatAmount(amount) {
      return parseFloat(amount || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>.home-container {
  padding: 24px;
}

.ant-card {
  border-radius: 8px;
}

.ant-statistic-content-value {
  font-size: 24px !important;
}
</style>
