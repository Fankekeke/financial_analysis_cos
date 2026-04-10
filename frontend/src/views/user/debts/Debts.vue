<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="负债名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.debtName"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="用户名"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.userName"/>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="add">新增</a-button>
        <a-button @click="batchDelete">删除</a-button>
<!--        <a-button @click="batchDelete1">删除</a-button>-->
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="titleShow" slot-scope="text, record">
          <template>
            <a-badge status="processing" v-if="record.rackUp === 1"/>
            <a-badge status="error" v-if="record.rackUp === 0"/>
            <a-tooltip>
              <template slot="title">
                {{ record.title }}
              </template>
              {{ record.title.slice(0, 8) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="contentShow" slot-scope="text, record">
          <template>
            <a-tooltip>
              <template slot="title">
                {{ record.content }}
              </template>
              {{ record.content.slice(0, 30) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修 改"></a-icon>
          <a-icon type="eye" theme="twoTone" twoToneColor="#52c41a" @click="showDetail(record)" style="margin-left: 8px" title="查看详情"></a-icon>
        </template>
      </a-table>
    </div>
    <!-- 添加详情模态框 -->
    <a-modal v-model="detailVisible" title="负债详情" :footer="null" @cancel="closeDetail" width="600px">
      <a-card :bordered="false" style="box-shadow: 0 2px 8px rgba(0,0,0,0.1)">
        <!-- 卡片头部 -->
        <div style="display: flex; align-items: center; padding-bottom: 16px; border-bottom: 1px solid #e8e8e8; margin-bottom: 24px;">
          <a-icon type="bank" theme="twoTone" twoToneColor="#1890ff" style="font-size: 24px; margin-right: 12px;"/>
          <div>
            <h3 style="margin: 0; color: #1890ff; font-weight: 600;">{{ detailData.debtName || '未命名负债' }}</h3>
            <a-tag :color="detailData.debtType === '每月' ? '#1890ff' : '#52c41a'" style="margin-top: 8px;">
              {{ detailData.debtType }}还款
            </a-tag>
          </div>
        </div>

        <!-- 主要信息区域 -->
        <div style="margin-bottom: 24px;">
          <h4 style="margin: 0 0 16px 0; color: #333; font-weight: 600; display: flex; align-items: center;">
            <a-icon type="dollar" style="color: #d4380d; margin-right: 8px;"/>
            金额概览
          </h4>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; background: #f7f8fa; padding: 16px; border-radius: 8px;">
            <div style="text-align: center; padding: 12px; background: white; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
              <div style="color: #999; font-size: 14px; margin-bottom: 4px;">总金额</div>
              <div style="font-size: 18px; font-weight: bold; color: #d4380d;">¥{{ parseFloat(detailData.totalAmount || 0).toFixed(2) }}</div>
            </div>
            <div style="text-align: center; padding: 12px; background: white; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
              <div style="color: #999; font-size: 14px; margin-bottom: 4px;">剩余金额</div>
              <div style="font-size: 18px; font-weight: bold; color: #fa8c16;">¥{{ parseFloat(detailData.remainingAmount || 0).toFixed(2) }}</div>
            </div>
          </div>
        </div>

        <!-- 利率信息 -->
        <div style="margin-bottom: 24px;">
          <h4 style="margin: 0 0 16px 0; color: #333; font-weight: 600; display: flex; align-items: center;">
            <a-icon type="percentage" style="color: #52c41a; margin-right: 8px;"/>
            利率信息
          </h4>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
            <div style="padding: 12px; background: #f6ffed; border: 1px solid #b7eb8f; border-radius: 6px;">
              <div style="color: #999; font-size: 14px; margin-bottom: 4px;">年利率</div>
              <div style="font-size: 16px; font-weight: bold; color: #52c41a;">{{ parseFloat(detailData.interestRate || 0).toFixed(2) }}%</div>
            </div>
            <div style="padding: 12px; background: #e6f7ff; border: 1px solid #91d5ff; border-radius: 6px;">
              <div style="color: #999; font-size: 14px; margin-bottom: 4px;">月利率</div>
              <div style="font-size: 16px; font-weight: bold; color: #13c2c2;">{{ ((parseFloat(detailData.interestRate || 0) / 12)).toFixed(4) }}%</div>
            </div>
          </div>
        </div>

        <!-- 时间与用户信息 -->
        <div style="margin-bottom: 24px;">
          <h4 style="margin: 0 0 16px 0; color: #333; font-weight: 600; display: flex; align-items: center;">
            <a-icon type="calendar" style="color: #1890ff; margin-right: 8px;"/>
            时间与用户
          </h4>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
            <div style="padding: 12px; background: #fffbe6; border: 1px solid #ffe58f; border-radius: 6px;">
              <div style="color: #999; font-size: 14px; margin-bottom: 4px;">还款截止日</div>
              <div style="font-weight: bold;">{{ detailData.dueDate || '- -' }}</div>
            </div>
            <div style="padding: 12px; background: #f9f0ff; border: 1px solid #d3adf7; border-radius: 6px;">
              <div style="color: #999; font-size: 14px; margin-bottom: 4px;">创建时间</div>
              <div style="font-weight: bold;">{{ detailData.createdAt || '- -' }}</div>
            </div>
            <div style="grid-column: span 2; padding: 12px; background: #f0f5ff; border: 1px solid #adc6ff; border-radius: 6px;">
              <div style="color: #999; font-size: 14px; margin-bottom: 4px;">用户名</div>
              <div style="font-weight: bold;">{{ detailData.userName || '- -' }}</div>
            </div>
          </div>
        </div>

        <!-- 应还金额分析 -->
        <div v-if="detailData.debtType === '每月'" style="margin-bottom: 24px;">
          <h4 style="margin: 0 0 16px 0; color: #333; font-weight: 600; display: flex; align-items: center;">
            <a-icon type="calculator" style="color: #1890ff; margin-right: 8px;"/>
            还款计划预估
          </h4>
          <div style="background: linear-gradient(135deg, #768bef 0%, #764ba2 100%); padding: 20px; border-radius: 8px; color: white; text-align: center;">
            <div style="font-size: 24px; font-weight: bold; margin-bottom: 8px;">
              ¥{{ calculateMonthlyPayment().toFixed(2) }}
            </div>
            <div style="font-size: 14px; opacity: 0.9;">预计月供金额</div>
            <div style="margin-top: 12px; font-size: 12px; opacity: 0.8;">
              <a-tag color="white" style="color: #667eea;">等额本息计算</a-tag>
              <span style="margin-left: 8px;">基于当前利率和期限自动计算</span>
            </div>
          </div>
        </div>

        <!-- 备注 -->
        <div v-if="detailData.content" style="margin-bottom: 16px;">
          <h4 style="margin: 0 0 16px 0; color: #333; font-weight: 600; display: flex; align-items: center;">
            <a-icon type="message" style="color: #999; margin-right: 8px;"/>
            备注说明
          </h4>
          <a-alert type="info" :message="detailData.content" banner style="border-radius: 6px;"/>
        </div>
      </a-card>
    </a-modal>
    <bulletin-add
      v-if="bulletinAdd.visiable"
      @close="handleBulletinAddClose"
      @success="handleBulletinAddSuccess"
      :bulletinAddVisiable="bulletinAdd.visiable">
    </bulletin-add>
    <bulletin-edit
      ref="bulletinEdit"
      @close="handleBulletinEditClose"
      @success="handleBulletinEditSuccess"
      :bulletinEditVisiable="bulletinEdit.visiable">
    </bulletin-edit>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import BulletinAdd from './DebtsAdd.vue'
import BulletinEdit from './DebtsEdit.vue'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'Bulletin',
  components: {BulletinAdd, BulletinEdit, RangeDate},
  data () {
    return {
      advanced: false,
      bulletinAdd: {
        visiable: false
      },
      bulletinEdit: {
        visiable: false
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      userList: [],
      detailVisible: false,
      detailData: {}
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '负债名称',
        dataIndex: 'debtName',
        ellipsis: true,
        customRender: (text, row, index) => {
          if (text !== null && text !== '') {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '总金额',
        dataIndex: 'totalAmount',
        ellipsis: true,
        customRender: (text, row, index) => {
          if (text !== null) {
            return `¥${parseFloat(text).toFixed(2)}`
          } else {
            return '- -'
          }
        }
      }, {
        title: '剩余金额',
        dataIndex: 'remainingAmount',
        ellipsis: true,
        customRender: (text, row, index) => {
          if (text !== null) {
            return `¥${parseFloat(text).toFixed(2)}`
          } else {
            return '- -'
          }
        }
      }, {
        title: '年利率(%)',
        dataIndex: 'interestRate',
        ellipsis: true,
        customRender: (text, row, index) => {
          if (text !== null) {
            return `${parseFloat(text).toFixed(2)}%`
          } else {
            return '- -'
          }
        }
      }, {
        title: '还款截止日',
        dataIndex: 'dueDate',
        ellipsis: true,
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '还款类型',
        dataIndex: 'debtType',
        ellipsis: true,
        customRender: (text, row, index) => {
          if (text !== null) {
            return <a-tag color={text === '每月' ? 'blue' : 'cyan'}>{text}</a-tag>
          } else {
            return '- -'
          }
        }
      }, {
        title: '创建时间',
        dataIndex: 'createdAt',
        ellipsis: true,
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '头像',
        dataIndex: 'userImages',
        customRender: (text, record, index) => {
          if (!record.userImages) return <a-avatar shape="square" icon="user" />
          return <a-popover>
            <template slot="content">
              <a-avatar shape="square" size={132} icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.userImages } />
            </template>
            <a-avatar shape="square" icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.userImages } />
          </a-popover>
        }
      }, {
        title: '用户名',
        dataIndex: 'userName',
        ellipsis: true,
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '操作',
        dataIndex: 'operation',
        ellipsis: true,
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    showDetail (record) {
      this.detailData = { ...record }
      this.detailVisible = true
    },
    closeDetail () {
      this.detailVisible = false
      this.detailData = {}
    },
    // 计算月供（等额本息）
    calculateMonthlyPayment () {
      const record = this.detailData
      if (!record || !record.totalAmount || !record.interestRate || !record.dueDate) {
        return 0
      }

      const principal = parseFloat(record.totalAmount)
      const annualRate = parseFloat(record.interestRate) / 100
      const monthlyRate = annualRate / 12

      // 计算剩余期数（假设从创建日期开始，按月计算）
      const today = moment()
      const dueDate = moment(record.dueDate)
      let monthsLeft = dueDate.diff(today, 'months')

      // 确保至少有1期
      monthsLeft = Math.max(1, monthsLeft)

      // 等额本息计算公式: M = P[r(1+r)^n]/[(1+r)^n-1]
      const n = monthsLeft
      const r = monthlyRate
      const pow = Math.pow(1 + r, n)
      const monthlyPayment = principal * r * pow / (pow - 1)

      return monthlyPayment
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.bulletinAdd.visiable = true
    },
    handleBulletinAddClose () {
      this.bulletinAdd.visiable = false
    },
    handleBulletinAddSuccess () {
      this.bulletinAdd.visiable = false
      this.$message.success('新增负债成功')
      this.search()
    },
    edit (record) {
      this.$refs.bulletinEdit.setFormValues(record)
      this.bulletinEdit.visiable = true
    },
    handleBulletinEditClose () {
      this.bulletinEdit.visiable = false
    },
    handleBulletinEditSuccess () {
      this.bulletinEdit.visiable = false
      this.$message.success('修改负债成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete1 () {
      this.$get('/business/supplier-info/batchEditSupplierName').then((r) => {
      })
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/business/debts/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      params.userId = this.currentUser.userId
      this.$get('/business/debts/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
