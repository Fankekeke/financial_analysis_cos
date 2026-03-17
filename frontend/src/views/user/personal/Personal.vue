<template>
  <div class="page-container">
    <a-row :gutter="16" class="content-wrapper" style="margin: 0 auto">
      <a-col :span="12" :offset="6">
        <a-card
          :loading="loading"
          :bordered="false"
          class="personal-card"
          :bodyStyle="{ padding: '24px' }"
        >
          <div slot="title" class="card-header">
            <a-icon type="user" theme="filled" style="color: #1890ff; font-size: 24px;" />
            <span class="header-title">个人信息</span>
            <a-divider type="vertical" style="height: 24px; margin: 0 12px;" />
            <span class="header-subtitle">完善您的个人资料</span>
          </div>

          <a-form :form="form" layout="vertical" class="personal-form">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label='用户编号' v-bind="formItemLayout" class="form-item-enhanced">
                  <div class="input-wrapper disabled">
                    <a-icon type="idcard" style="color: #1890ff; margin-right: 8px;" />
                    <a-input disabled v-decorator="[
                      'code',
                    ]" class="custom-input"/>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label='注册时间' v-bind="formItemLayout" class="form-item-enhanced">
                  <div class="input-wrapper disabled">
                    <a-icon type="clock-circle" style="color: #52c41a; margin-right: 8px;" />
                    <a-input disabled v-decorator="[
                      'createDate',
                    ]" class="custom-input"/>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label='用户名称' v-bind="formItemLayout" class="form-item-enhanced">
                  <div class="input-wrapper">
                    <a-icon type="user" style="color: #1890ff; margin-right: 8px;" />
                    <a-input v-decorator="[
                      'name',
                      { rules: [{ required: true, message: '请输入用户名称!' }] }
                    ]" placeholder="请输入您的姓名" class="custom-input"/>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label='邮箱地址' v-bind="formItemLayout" class="form-item-enhanced">
                  <div class="input-wrapper">
                    <a-icon type="mail" style="color: #722ed1; margin-right: 8px;" />
                    <a-input v-decorator="[
                      'mail'
                    ]" placeholder="example@email.com" class="custom-input"/>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label='联系方式' v-bind="formItemLayout" class="form-item-enhanced">
                  <div class="input-wrapper">
                    <a-icon type="phone" style="color: #fa8c16; margin-right: 8px;" />
                    <a-input v-decorator="[
                      'phone'
                    ]" placeholder="请输入手机号码" class="custom-input"/>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label='省份' v-bind="formItemLayout" class="form-item-enhanced">
                  <div class="input-wrapper">
                    <a-icon type="environment" style="color: #13c2c2; margin-right: 8px;" />
                    <a-input v-decorator="[
                      'province'
                    ]" placeholder="请输入省份" class="custom-input"/>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label='市' v-bind="formItemLayout" class="form-item-enhanced">
                  <div class="input-wrapper">
                    <a-icon type="map" style="color: #eb2f96; margin-right: 8px;" />
                    <a-input v-decorator="[
                      'city'
                    ]" placeholder="请输入城市" class="custom-input"/>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label='区' v-bind="formItemLayout" class="form-item-enhanced">
                  <div class="input-wrapper">
                    <a-icon type="cluster" style="color: #f5222d; margin-right: 8px;" />
                    <a-input v-decorator="[
                      'area'
                    ]" placeholder="请输入区县" class="custom-input"/>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label='联系地址' v-bind="formItemLayout" class="form-item-enhanced">
                  <div class="input-wrapper">
                    <a-icon type="home" style="color: #2f54eb; margin-right: 8px;" />
                    <a-input v-decorator="[
                      'address'
                    ]" placeholder="请输入详细地址" class="custom-input"/>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="24">
                <a-form-item label='头像' v-bind="formItemLayout" class="form-item-enhanced avatar-item">
                  <div class="avatar-wrapper">
                    <a-upload
                      name="avatar"
                      action="http://127.0.0.1:9527/file/fileUpload/"
                      list-type="picture-card"
                      :file-list="fileList"
                      @preview="handlePreview"
                      @change="picHandleChange"
                      class="custom-upload"
                    >
                      <div v-if="fileList.length < 1">
                        <a-icon type="plus" style="font-size: 24px; color: #1890ff;" />
                        <div class="ant-upload-text" style="margin-top: 8px; color: #666;">
                          点击上传头像
                        </div>
                      </div>
                    </a-upload>
                    <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel" class="avatar-preview-modal">
                      <img alt="example" style="width: 100%" :src="previewImage" />
                    </a-modal>
                  </div>
                </a-form-item>
              </a-col>
            </a-row>

            <div class="form-actions">
              <a-button
                key="submit"
                type="primary"
                :loading="loading"
                @click="handleSubmit"
                class="submit-btn"
                size="large"
              >
                <a-icon type="check-circle" /> 确认修改
              </a-button>
              <a-button
                key="reset"
                @click="handleReset"
                class="reset-btn"
                size="large"
              >
                <a-icon type="reload" /> 重置
              </a-button>
            </div>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'User',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      form: this.$form.createForm(this),
      formItemLayout,
      loading: false,
      courseInfo: [],
      dataLoading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      expertInfo: null
    }
  },
  mounted () {
    this.getExpertInfo(this.currentUser.userId)
  },
  methods: {
    isDuringDate (beginDateStr, endDateStr, curDataStr) {
      let curDate = new Date(curDataStr)
      let beginDate = new Date(beginDateStr)
      let endDate = new Date(endDateStr)
      if (curDate >= beginDate && curDate <= endDate) {
        return true
      }
      return false
    },
    getListData (value) {
      let listData = []
      this.courseInfo.forEach(item => {
        if ((moment(value).format('YYYY-MM-DD')) === (moment(item.createDate).format('YYYY-MM-DD'))) {
          listData.push({type: 'success', content: item.code})
        }
      })
      return listData || []
    },
    getExpertInfo (userId) {
      this.dataLoading = true
      this.$get(`/business/user-info/user/${userId}`).then((r) => {
        this.expertInfo = r.data.user
        this.setFormValues(this.expertInfo)
        this.courseInfo = r.data.order
        this.dataLoading = false
      })
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    setFormValues ({...expert}) {
      this.rowId = expert.id
      let fields = ['code', 'name', 'phone', 'province', 'city', 'area', 'mail', 'images', 'address', 'createDate']
      let obj = {}
      Object.keys(expert).forEach((key) => {
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(expert['images'])
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = expert[key]
        }
      })
      this.form.setFieldsValue(obj)
    },
    handleSubmit () {
      // 获取图片List
      let images = []
      this.fileList.forEach(image => {
        if (image.response !== undefined) {
          images.push(image.response)
        } else {
          images.push(image.name)
        }
      })
      this.form.validateFields((err, values) => {
        values.id = this.rowId
        values.images = images.length > 0 ? images.join(',') : null
        if (!err) {
          this.loading = true
          this.$put('/business/user-info', {
            ...values
          }).then((r) => {
            this.$message.success('更新成功')
            this.loading = false
            this.getExpertInfo(this.currentUser.userId)
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="less">.page-container {
  padding: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f5f5f6 100%);
  min-height: calc(100vh - 64px);
}

.content-wrapper {
  .personal-card {
    border-radius: 3px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);

    .card-header {
      display: flex;
      align-items: center;
      padding: 16px 0;
      border-bottom: 2px solid #f0f0f0;
      margin-bottom: 24px;

      .header-title {
        font-size: 24px;
        font-weight: bold;
        color: #333;
        margin-left: 12px;
      }

      .header-subtitle {
        font-size: 14px;
        color: #999;
        font-weight: normal;
      }
    }
  }

  .personal-form {
    .form-item-enhanced {
      margin-bottom: 24px;

      /deep/ .ant-form-item-label > label {
        font-weight: 600;
        color: #555;
        font-size: 14px;
      }

      .input-wrapper {
        position: relative;
        display: flex;
        align-items: center;

        &.disabled {
          .custom-input {
            background: #fafafa;
            color: #999;
          }
        }

        .custom-input {
          flex: 1;
          border-radius: 3px;
          transition: all 0.3s;

          &:hover {
            border-color: #1890ff;
          }

          &:focus {
            box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
          }
        }
      }

      &.avatar-item {
        .avatar-wrapper {
          .custom-upload {
            /deep/ .ant-upload-select-picture-card {
              border-radius: 12px;
              border: 2px dashed #d9d9d9;
              transition: all 0.3s;

              &:hover {
                border-color: #1890ff;
                box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
              }
            }
          }
        }
      }
    }

    .form-actions {
      display: flex;
      gap: 16px;
      justify-content: center;
      padding-top: 24px;
      border-top: 1px solid #f0f0f0;

      .submit-btn {
        min-width: 160px;
        height: 44px;
        font-size: 16px;
        border-radius: 3px;
        background: linear-gradient(135deg, #2dcb84 0%, #09813d 100%);
        border: none;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
        }
      }

      .reset-btn {
        min-width: 120px;
        height: 44px;
        font-size: 16px;
        border-radius: 3px;
        transition: all 0.3s;

        &:hover {
          border-color: #d9d9d9;
          color: #666;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .content-wrapper {
    .personal-card {
      .card-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
      }
    }

    .personal-form {
      .form-actions {
        flex-direction: column;

        .submit-btn,
        .reset-btn {
          width: 100%;
        }
      }
    }
  }
}
</style>
