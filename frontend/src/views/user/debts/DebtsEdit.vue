<template>
  <a-modal v-model="show" title="修改负债" @cancel="onClose" :width="650">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        修改
      </a-button>
    </template>
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">

        <a-col :span="12">
          <a-form-item label='负债名称' v-bind="formItemLayout">
            <a-input v-decorator="[
            'debtName',
            { rules: [{ required: true, message: '请输入负债名称!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='总金额' v-bind="formItemLayout">
            <a-input-number
              v-decorator="[
                'totalAmount',
                { rules: [{ required: true, message: '请输入总金额!' }] }
              ]"              style="width: 100%"
              :precision="2"
              :min="0"
              placeholder="请输入总金额"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='剩余金额' v-bind="formItemLayout">
            <a-input-number
              v-decorator="[
                'remainingAmount',
                { rules: [{ required: true, message: '请输入剩余金额!' }] }
              ]"              style="width: 100%"
              :precision="2"
              :min="0"
              placeholder="请输入剩余金额"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='年利率(%)' v-bind="formItemLayout">
            <a-input-number
              v-decorator="[
                'interestRate',
                { rules: [{ required: true, message: '请输入年利率!' }], initialValue: 0 }
              ]"              style="width: 100%"
              :precision="2"
              :min="0"
              :max="100"
              placeholder="请输入年利率"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='还款截止日' v-bind="formItemLayout">
            <a-date-picker
              v-decorator="[
                'dueDate',
                { rules: [{ required: true, message: '请选择还款截止日!' }] }
              ]"              style="width: 100%"
              placeholder="选择日期"
              format="YYYY-MM-DD"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='还款类型' v-bind="formItemLayout">
            <a-select v-decorator="[
              'debtType',
              { rules: [{ required: true, message: '请选择还款类型!' }] }
              ]">
              <a-select-option value="单次">单次</a-select-option>
              <a-select-option value="每月">每月</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='备注' v-bind="formItemLayout">
            <a-textarea :rows="4" v-decorator="[
            'content',
             { rules: [{ required: false, message: '请输入备注!' }] }
            ]"/>
          </a-form-item>
        </a-col>

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
  name: 'BulletinEdit',
  props: {
    bulletinEditVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.bulletinEditVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      rowId: null,
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
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    setFormValues ({...bulletin}) {
      this.rowId = bulletin.id
      let fields = ['debtName', 'totalAmount', 'remainingAmount', 'interestRate', 'dueDate', 'debtType', 'content']
      let obj = {}
      Object.keys(bulletin).forEach((key) => {
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(bulletin['images'])
        }
        if (key === 'rackUp' || key === 'type') {
          bulletin[key] = bulletin[key].toString()
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = bulletin[key]
        }
      })
      if (bulletin['dueDate'] != null) {
        obj['dueDate'] = moment(bulletin['dueDate'])
      }
      this.form.setFieldsValue(obj)
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
        if (image.response !== undefined) {
          images.push(image.response)
        } else {
          images.push(image.name)
        }
      })
      this.form.validateFields((err, values) => {
        values.id = this.rowId
        values.images = images.length > 0 ? images.join(',') : null
        values.dueDate =  moment(values.dueDate).format('YYYY-MM-DD')
        if (!err) {
          this.loading = true
          this.$put('/business/debts', {
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
