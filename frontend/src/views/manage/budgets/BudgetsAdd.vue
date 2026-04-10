<template>
  <a-modal v-model="show" title="新增预算" @cancel="onClose" :width="650">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        提交
      </a-button>
    </template>
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="12">
          <a-form-item label='预算周期' v-bind="formItemLayout">
            <a-month-picker
              v-decorator="[
                'period',
                { rules: [{ required: true, message: '请选择预算周期!' }] }
              ]"              style="width: 100%"
              placeholder="选择年月"
              format="YYYY-MM"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='预算限额' v-bind="formItemLayout">
            <a-input-number
              v-decorator="[
                'amountLimit',
                { rules: [{ required: true, message: '请输入预算限额!' }] }
              ]"              style="width: 100%"
              :precision="2"
              :min="0"
              placeholder="请输入预算金额"
            />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='预警阈值' v-bind="formItemLayout">
            <a-slider
              v-decorator="[
                'alertThreshold',
                { rules: [{ required: true, message: '请选择预警阈值!' }], initialValue: 0.8 }
              ]"
              :marks="{0: '0%', 0.5: '50%', 0.8: '80%', 1: '100%'}"
              :step="0.01"
              :min="0"
              :max="1"
            />
          </a-form-item>
        </a-col>
<!--        <a-col :span="12">-->
<!--          <a-form-item label='是否触发80%预警' v-bind="formItemLayout">-->
<!--            <a-select v-decorator="[-->
<!--              'isAlerted80',-->
<!--              { rules: [{ required: true, message: '请选择状态!' }], initialValue: 0 }-->
<!--              ]">-->
<!--              <a-select-option :value="1">是</a-select-option>-->
<!--              <a-select-option :value="0">否</a-select-option>-->
<!--            </a-select>-->
<!--          </a-form-item>-->
<!--        </a-col>-->
<!--        <a-col :span="12">-->
<!--          <a-form-item label='是否触发100%预警' v-bind="formItemLayout">-->
<!--            <a-select v-decorator="[-->
<!--              'isAlerted100',-->
<!--              { rules: [{ required: true, message: '请选择状态!' }], initialValue: 0 }-->
<!--              ]">-->
<!--              <a-select-option :value="1">是</a-select-option>-->
<!--              <a-select-option :value="0">否</a-select-option>-->
<!--            </a-select>-->
<!--          </a-form-item>-->
<!--        </a-col>-->
        <a-col :span="24">
          <a-form-item label='备注' v-bind="formItemLayout">
            <a-textarea :rows="4" v-decorator="[
            'content',
             { rules: [{ required: false, message: '请输入备注!' }] }
            ]"/>
          </a-form-item>
        </a-col>
<!--        <a-col :span="24">-->
<!--          <a-form-item label='图册' v-bind="formItemLayout">-->
<!--            <a-upload-->
<!--              name="avatar"-->
<!--              action="http://127.0.0.1:9527/file/fileUpload/"-->
<!--              list-type="picture-card"-->
<!--              :file-list="fileList"-->
<!--              @preview="handlePreview"-->
<!--              @change="picHandleChange"-->
<!--            >-->
<!--              <div v-if="fileList.length < 8">-->
<!--                <a-icon type="plus" />-->
<!--                <div class="ant-upload-text">-->
<!--                  Upload-->
<!--                </div>-->
<!--              </div>-->
<!--            </a-upload>-->
<!--            <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">-->
<!--              <img alt="example" style="width: 100%" :src="previewImage" />-->
<!--            </a-modal>-->
<!--          </a-form-item>-->
<!--        </a-col>-->
      </a-row>
    </a-form>
  </a-modal>
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
  name: 'BulletinAdd',
  props: {
    bulletinAddVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.bulletinAddVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: ''
    }
  },
  methods: {
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
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      // 获取图片List
      let images = []
      this.fileList.forEach(image => {
        images.push(image.response)
      })
      this.form.validateFields((err, values) => {
        values.images = images.length > 0 ? images.join(',') : null
        values.userId = this.currentUser.userId
        values.period =  moment(values.period).format('YYYY-MM')
        if (!err) {
          this.loading = true
          this.$post('/business/budgets', {
            ...values
          }).then((r) => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
