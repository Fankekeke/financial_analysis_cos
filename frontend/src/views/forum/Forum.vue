<template>
  <div class="single-page" style="padding: 0 400px;">
    <div style="background: #f9f9f9;padding: 0 50px">
      <post></post>
    </div>
  </div>
</template>
<script>
import {mapState} from 'vuex'
import moment from 'moment'
import Post from './Post.vue'
moment.locale('zh-cn')

export default {
  components: { Post },
  name: 'HomePage',
  data () {
    return {
    }
  },
  computed: {
    ...mapState({
      multipage: state => state.setting.multipage,
      user: state => state.account.user
    })
  },
  methods: {
  }
}
</script>
<style lang="less" scoped>
</style>
