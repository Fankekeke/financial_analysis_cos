<template>
  <div style="background:#ECECEC; padding:30px;margin-top: 30px;width: 100%">
    <a-row :gutter="55">
      <a-col v-if="collectList.length === 0" :span="24">
        <div style="color: rgba(0,0,0,.85);font-size: 24px;line-height: 1.8;text-align: center;height: 250px;">
          <p style="margin-top: 100px">暂无关注记录</p>
        </div>
      </a-col>
      <a-col :span="4" v-for="(item, index) in collectList" :key="index">
        <a-card hoverable :bordered="false">
          <a-card-meta>
            <div slot="title">
              <p>{{ item.collectUsername }}</p>
              <a-tag v-if="item.collectRoleFlag == 1" color="#48ad20">用户</a-tag>
              <a-tag v-if="item.collectRoleFlag == 2" color="#2eabff">用户</a-tag>
            </div>
            <p slot="description">{{ item.content }}</p>
            <a-avatar shape="square" slot="avatar" :size="80" icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + item.collectAvatar" />
          </a-card-meta>
          <template slot="actions" class="ant-card-actions">
            <a-icon key="delete" type="delete" @click="collectPostCheck(1, item.collectUserId)"/>
          </template>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import {mapState} from 'vuex'
export default {
  name: 'Focus',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      loading: false,
      collectList: []
    }
  },
  mounted () {
    this.getCollectList()
  },
  methods: {
    collectPostCheck (deleteFlag, collectUserId) {
      this.$post(`/business/focus-info`, {userId: this.currentUser.userId, collectUserId, deleteFlag}).then((r) => {
        this.getCollectList()
        this.$message.success(deleteFlag === 0 ? '关注成功！' : '取消关注成功！')
      })
    },
    getCollectList () {
      this.$get(`/business/focus-info/list/${this.currentUser.userId}`).then((r) => {
        this.collectList = r.data.data
      })
    }
  }
}
</script>

<style scoped>

</style>
