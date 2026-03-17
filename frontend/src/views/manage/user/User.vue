<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="用户名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.name"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="用户编号"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.code"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="联系方式"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.phone"/>
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
      </div>
      <!-- 表格区域 -->
      <a-table bordered ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="operation" slot-scope="text, record">
          <a-icon type="eye" theme="twoTone" twoToneColor="#1890ff" @click="showDetail(record)" title="查看详情" style="margin-right: 10px;"></a-icon>
        </template>
      </a-table>
    </div>
    <!-- 详情 Modal -->
    <a-modal
      v-model="detailModal.visible"
      :title="detailModal.title"
      :width="900"
      :footer="null"
      @cancel="handleDetailClose"
      centered
      :bodyStyle="{ padding: '24px' }">
      <div class="detail-container">
        <div class="detail-header">
          <div class="header-avatar">
            <a-avatar v-if="detailModal.data.images" shape="circle" size="80" icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + detailModal.data.images.split(',')[0]" />
            <a-avatar v-else shape="circle" size="80" icon="user" style="background-color: #1890ff;" />
          </div>
          <div class="header-info">
            <div class="user-name">{{ detailModal.data.name || '匿名用户' }}</div>
            <div class="user-code">用户编号：{{ detailModal.data.code || '- -' }}</div>
          </div>
        </div>

        <a-divider style="margin: 16px 0" />

        <div class="detail-content">
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">
                <a-icon type="phone" style="color: #1890ff;" />
                <span>联系方式</span>
              </div>
              <div class="info-value">
                <span>{{ detailModal.data.phone || '- -' }}</span>
              </div>
            </div>

            <div class="info-item">
              <div class="info-label">
                <a-icon type="mail" style="color: #52c41a;" />
                <span>邮箱地址</span>
              </div>
              <div class="info-value">
                <span>{{ detailModal.data.mail || '- -' }}</span>
              </div>
            </div>

            <div class="info-item">
              <div class="info-label">
                <a-icon type="calendar" style="color: #faad14;" />
                <span>出生日期</span>
              </div>
              <div class="info-value">
                <span>{{ detailModal.data.birthday || '- -' }}</span>
              </div>
            </div>

            <div class="info-item">
              <div class="info-label">
                <a-icon type="clock-circle" style="color: #722ed1;" />
                <span>注册时间</span>
              </div>
              <div class="info-value">
                <span>{{ detailModal.data.createDate || '- -' }}</span>
              </div>
            </div>
          </div>

          <div class="content-section" style="margin-top: 24px;">
            <div class="section-title">
              <a-icon type="home" style="color: #1890ff; margin-right: 8px;" />
              <span>住址信息</span>
            </div>
            <div class="section-body">
              <p class="text-content">{{ detailModal.data.address || '暂无住址信息' }}</p>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'user',
  components: {RangeDate},
  data () {
    return {
      advanced: false,
      userAdd: {
        visiable: false
      },
      userEdit: {
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
      detailModal: {
        visible: false,
        title: '用户详情',
        data: {}
      }
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '用户编号',
        dataIndex: 'code',
        ellipsis: true
      }, {
        title: '用户名称',
        dataIndex: 'name',
        ellipsis: true
      }, {
        title: '联系方式',
        dataIndex: 'phone',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '出生日期',
        dataIndex: 'birthday',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '邮箱地址',
        dataIndex: 'mail',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '头像',
        dataIndex: 'images',
        customRender: (text, record, index) => {
          if (!record.images) return <a-avatar shape="square" icon="user" />
          return <a-popover>
            <template slot="content">
              <a-avatar shape="square" size={132} icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images.split(',')[0] } />
            </template>
            <a-avatar shape="square" icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images.split(',')[0] } />
          </a-popover>
        }
      }, {
        title: '住址信息',
        dataIndex: 'address',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '注册时间',
        dataIndex: 'createDate',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    showDetail (record) {
      this.detailModal.data = { ...record }
      this.detailModal.visible = true
    },
    handleDetailClose () {
      this.detailModal.visible = false
      this.detailModal.data = {}
    },
    editStatus (row, status) {
      this.$post('/business/user-info/account/status', { staffId: row.id, status }).then((r) => {
        this.$message.success('修改成功')
        this.fetch()
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.userAdd.visiable = true
    },
    handleuserAddClose () {
      this.userAdd.visiable = false
    },
    handleuserAddSuccess () {
      this.userAdd.visiable = false
      this.$message.success('新增产品成功')
      this.search()
    },
    edit (record) {
      this.$refs.userEdit.setFormValues(record)
      this.userEdit.visiable = true
    },
    handleuserEditClose () {
      this.userEdit.visiable = false
    },
    handleuserEditSuccess () {
      this.userEdit.visiable = false
      this.$message.success('修改产品成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
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
          that.$delete('/business/user-info/' + ids).then(() => {
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
      if (params.type === undefined) {
        delete params.type
      }
      this.$get('/business/user-info/page', {
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

.detail-container {
  .detail-header {
    display: flex;
    align-items: center;
    padding: 16px;
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
    border-radius: 8px;
    margin-bottom: 16px;

    .header-avatar {
      margin-right: 24px;

      .ant-avatar {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }

    .header-info {
      flex: 1;

      .user-name {
        font-size: 20px;
        font-weight: 600;
        color: #262626;
        margin-bottom: 8px;
      }

      .user-code {
        font-size: 14px;
        color: #8c8c8c;
      }
    }
  }

  .detail-content {
    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;

      .info-item {
        display: flex;
        flex-direction: column;
        padding: 16px;
        background: #f5f5f5;
        border-radius: 6px;
        border: 1px solid #e8e8e8;

        .info-label {
          display: flex;
          align-items: center;
          font-size: 13px;
          color: #8c8c8c;
          margin-bottom: 8px;
          font-weight: 500;

          span {
            margin-left: 6px;
          }
        }

        .info-value {
          font-size: 15px;
          color: #262626;
          font-weight: 500;
        }
      }
    }

    .content-section {
      .section-title {
        display: flex;
        align-items: center;
        font-size: 16px;
        font-weight: 600;
        color: #262626;
        margin-bottom: 12px;
        padding: 8px 12px;
        background: #f5f5f5;
        border-radius: 4px;
        border-left: 4px solid #1890ff;
      }

      .section-body {
        padding: 16px;
        background: #fafafa;
        border-radius: 6px;
        border: 1px solid #e8e8e8;
        min-height: 60px;

        .text-content {
          margin: 0;
          line-height: 1.8;
          color: #262626;
          font-size: 14px;
        }
      }
    }
  }
}
</style>
