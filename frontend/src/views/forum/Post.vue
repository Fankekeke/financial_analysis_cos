<template>
  <div style="border: none">
    <div style="margin-left: -14px;margin-right: -14px;">
      <div class="head-info-card" style="padding: 12px">
        <a-row>
          <a-col :span="24">
            <a-input-search placeholder="搜索贴子" v-show="!postDetailShow"
                            style="width: 200px;margin-top: 10px;float: right" @search="onSearch"/>
          </a-col>
        </a-row>
        <a-tabs :activeKey="tabKey" tab-position="top" @change="tabChange" v-show="!postDetailShow">
          <a-tab-pane v-for="item in tagList" :key="item.id" :tab="item.name">
            <a-skeleton active v-if="loading"/>
            <div v-if="!loading">
              <a-list item-layout="vertical" size="large" :pagination="pagination" :data-source="postList">
                <a-list-item slot="renderItem" key="item.title" slot-scope="item, index">
                  <template slot="actions">
                    <span key="message">
                      <a-icon type="message" style="margin-right: 8px"/>
                      <span v-if="item.collect === 0">{{ item.reply }}</span>
                      <span v-else>{{ item.reply / item.collect }}</span> 回复
                    </span>
                    <span key="star">
                      <a-icon type="star" style="margin-right: 8px"/>
                      {{ item.collect }} 收藏
                    </span>
                    <span key="to-top">
                      <a-icon type="to-top" style="margin-right: 8px"/>
                      {{ timeFormat(item.createDate) }}
                    </span>
                  </template>
                  <a-list-item-meta :description="item.content.slice(0, 100) + '...'">
                    <a slot="title" @click="postReplyDetail(item)">{{ item.title }}</a>
                    <a-avatar shape="square" slot="avatar" icon="user"
                              :src="'http://127.0.0.1:9527/imagesWeb/' + item.userImages"/>
                  </a-list-item-meta>
                </a-list-item>
              </a-list>
            </div>
          </a-tab-pane>
        </a-tabs>
        <div v-if="postDetailShow && postDetail !== null" style="margin: 18px">
          <div style="margin-bottom: 10px">
            <a-breadcrumb>
              <a-breadcrumb-item><a @click="postDetailShow = false">返回</a></a-breadcrumb-item>
              <a-breadcrumb-item>{{ tabName }}</a-breadcrumb-item>
            </a-breadcrumb>
          </div>
          <p
            style="font-size: 22px;color: black;font-weight: 500;line-height: 150%;margin: 25px 50px;margin-top: 50px">
            {{ postDetail.title }}
            <a-icon type="form" style="cursor: pointer" v-if="user.userId === postDetail.userId"
                    @click="edit(postDetail)"/>
          </p>
          <div style="margin: 25px 50px;font-size: 13px">
            <a-icon v-if="collectUser === 0" type="heart" style="margin-right: 10px;cursor: pointer"
                    @click="collectUserCheck(0)"/>
            <a-icon v-if="collectUser > 0" type="heart" style="margin-right: 10px;color: red;cursor: pointer"
                    @click="collectUserCheck(1)"/>
            {{ postDetail.username }} 关注
            <a-divider type="vertical"/>
            <a-icon type="eye" style="margin-right: 10px;margin-left: 40px"/>
            {{ postDetail.pageviews }} 访问
            <a-divider type="vertical"/>
            <a-icon type="message" style="margin-right: 10px"/>
            <span v-if="postDetail.collect === 0">{{ postDetail.reply }}</span>
            <span v-else>{{ postDetail.reply / postDetail.collect }}</span> 回复
            <a-divider type="vertical"/>
            <a-icon v-if="collectPost === 0" type="star" style="margin-right: 10px;cursor: pointer"
                    @click="collectPostCheck(0)"/>
            <a-icon v-if="collectPost > 0" type="star" style="margin-right: 10px;color: red;cursor: pointer"
                    @click="collectPostCheck(1)"/>
            {{ postDetail.collect }} 收藏
            <a-divider type="vertical"/>
            {{ timeFormat(postDetail.createDate) }}
          </div>
          <div
            style="margin: 25px 50px;font-size: 15px;line-height: 1.6;word-break: break-word;letter-spacing: 1px;text-indent: 30px">
            {{ postDetail.content }}
          </div>
          <div style="margin: 25px 50px;height: 100px">
            <a-upload
              name="avatar"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="fileList"
              @preview="handlePreview"
            >
            </a-upload>
            <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
              <img alt="example" style="width: 100%" :src="previewImage"/>
            </a-modal>
          </div>
          <div style="margin: 25px 50px;">
            <a-list
              class="comment-list"
              :pagination="pagination"
              :header="`${replyList.length} 回复`"
              item-layout="horizontal"
              :data-source="replyList"
            >
              <a-list-item slot="renderItem" slot-scope="item, index">
                <a-comment :author="item.username" shape="square"
                           :avatar="'http://127.0.0.1:9527/imagesWeb/' + item.images" @click="pushToDetail(item.userId)">
                  <template slot="actions">
                    <span @click="replyUserAdd(item)">回复</span>
                  </template>
                  <p slot="content" style="white-space: pre-line;">
                    {{ item.content }}
                  </p>
                  <a-tooltip slot="datetime" :title="item.sendCreate">
                    <span>{{ timeFormat(item.sendCreate) }}</span>
                  </a-tooltip>
                </a-comment>
              </a-list-item>
            </a-list>
            <div style="margin-bottom: 200px;margin-top: 50px">
              <a-textarea
                v-model="replyContent"
                placeholder="Controlled autosize"
                :rows="5"
              />
              <a-button type="primary" style="float: right;margin-top: 15px" @click="commit">
                提交
              </a-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <post-add
      v-if="postAdd.visiable"
      @close="handlepostAddClose"
      @success="handlepostAddSuccess"
      :postAddVisiable="postAdd.visiable"
      :tagList="tagListData">
    </post-add>
    <post-edit
      ref="postEdit"
      @close="handlepostEditClose"
      @success="handlepostEditSuccess"
      :postEditVisiable="postEdit.visiable"
      :tagList="tagListData">
    </post-edit>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import PostAdd from '../user/post/PostAdd'
import PostEdit from '../user/post/PostEdit'

export default {
  name: 'Post',
  components: {PostAdd, PostEdit},
  computed: {
    ...mapState({
      user: state => state.account.user
    })
  },
  data () {
    return {
      postList: [],
      replyList: [],
      postDetail: null,
      postDetailShow: false,
      pagination: {
        pageSize: 10
      },
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      replyContent: '',
      replyUser: null,

      tagList: [],
      tagListData: [],
      tabName: '',
      tabKey: '',
      collectPost: 0,
      collectUser: 0,
      postAdd: {
        visiable: false
      },
      postEdit: {
        visiable: false
      }
    }
  },
  watch: {
    // replyContent: function (value) {
    //   if (value === '') {
    //     this.replyUser = null
    //   }
    // }
  },
  mounted () {
    this.getTagList()
  },
  methods: {
    pushToDetail (userId) {
      this.$router.push({
        path: '/userDetail',
        query: {
          id: userId
        }
      })
    },
    recommendList () {
      this.loading = true
      this.$get(`/business/post-info/recommend/${this.user.userId}`).then((r) => {
        this.postList = r.data.data
        setTimeout(() => {
          this.loading = false
        }, 500)
      })
    },
    collectUserCheck (deleteFlag) {
      this.$post(`/business/focus-info`, {
        userId: this.user.userId,
        collectUserId: this.postDetail.userId,
        deleteFlag
      }).then((r) => {
        this.postReplyDetail(this.postDetail)
        this.$message.success(deleteFlag === 0 ? '关注成功！' : '取消关注成功！')
      })
    },
    collectPostCheck (deleteFlag) {
      this.$post(`/business/collect-info`, {userId: this.user.userId, postId: this.postDetail.id, deleteFlag}).then((r) => {
        this.postReplyDetail(this.postDetail)
        this.$message.success(deleteFlag === 0 ? '收藏贴子成功！' : '取消收藏成功！')
      })
    },
    collectByUser (postId) {
      this.$get(`/business/post-info/collcet`, {userId: this.user.userId, postId}).then((r) => {
        this.collectPost = r.data.collect
        this.collectUser = r.data.focus
      })
    },
    commit () {
      if (this.replyContent !== '') {
        let data = {
          userId: this.user.userId,
          content: this.replyContent,
          postId: this.postDetail.id,
          replyUserId: this.replyUser
        }
        this.$post(`/business/reply-info`, data).then((r) => {
          if (r.data.code === 500) {
            this.$message.error(r.data.msg)
          } else {
            this.postReplyDetail(this.postDetail)
            this.replyContent = ''
          }
        })
      } else {
        this.$message.error('请填写评论！')
      }
    },
    replyUserAdd (reply) {
      this.replyUser = reply.userId
      this.replyContent = this.replyContent + '@' + reply.username
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
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    handleCancel () {
      this.previewVisible = false
    },
    postReplyDetail (post) {
      this.postInfoDetail(post.id)
      this.collectByUser(post.id)
      this.replyUser = []
      this.fileList = []
      this.$get(`/business/reply-info/list/${post.id}`).then((r) => {
        this.replyList = r.data.data
        this.postDetailShow = true
      })
    },
    postInfoDetail (postId) {
      this.$get(`/business/post-info/${postId}`).then((r) => {
        this.postDetail = r.data
        this.imagesInit(this.postDetail.images)
      })
    },
    tabChange (key) {
      this.tabName = this.tagList.find(o => o.id === key).name
      this.tabKey = key
      if (key !== 9999 && key !== -1) {
        this.getPostList(key)
        if (this.tagList[this.tagList.length - 1].id === 9999) {
          this.tagList.pop()
        }
      }
      if (key === -1) {
        this.recommendList()
      }
    },
    getPostList (tagId) {
      this.loading = true
      this.$get(`/business/post-info/tag/${tagId}`).then((r) => {
        this.postList = r.data.data
        setTimeout(() => {
          this.loading = false
        }, 500)
      })
    },
    getTagList () {
      this.$get('/business/tag-info/list').then((r) => {
        this.tagList = [{id: -1, name: '推荐'}]
        this.tagList.push.apply(this.tagList, r.data.data)
        console.log(this.tagList)
        if (this.tagList.length !== 0) {
          this.tabChange(this.tagList[0].id)
        }
        let tagListData = []
        r.data.data.forEach(item => {
          tagListData.push({label: item.name, value: item.id})
        })
        this.tagListData = tagListData
      })
    },
    timeFormat (time) {
      var nowTime = new Date()
      var day = nowTime.getDate()
      var hours = parseInt(nowTime.getHours())
      var minutes = nowTime.getMinutes()
      // 开始分解付入的时间
      var timeday = time.substring(8, 10)
      var timehours = parseInt(time.substring(11, 13))
      var timeminutes = time.substring(14, 16)
      // eslint-disable-next-line camelcase
      var d_day = Math.abs(day - timeday)
      // eslint-disable-next-line camelcase
      var d_hours = hours - timehours
      // eslint-disable-next-line camelcase
      var d_minutes = Math.abs(minutes - timeminutes)
      // eslint-disable-next-line camelcase
      if (d_day <= 1) {
        // eslint-disable-next-line camelcase
        switch (d_day) {
          case 0:
            // eslint-disable-next-line camelcase
            if (d_hours === 0 && d_minutes > 0) {
              // eslint-disable-next-line camelcase
              return d_minutes + '分钟前'
              // eslint-disable-next-line camelcase
            } else if (d_hours === 0 && d_minutes === 0) {
              return '1分钟前'
            } else {
              // eslint-disable-next-line camelcase
              return Math.abs(d_hours) + '小时前'
            }
            // eslint-disable-next-line no-unreachable
            break
          case 1:
            // eslint-disable-next-line camelcase
            if (d_hours < 0) {
              // eslint-disable-next-line camelcase
              return (24 + d_hours) + '小时前'
            } else {
              // eslint-disable-next-line camelcase
              return d_day + '天前'
            }
            // eslint-disable-next-line no-unreachable
            break
        }
        // eslint-disable-next-line camelcase
      } else if (d_day > 1 && d_day < 10) {
        // eslint-disable-next-line camelcase
        return d_day + '天前'
      } else {
        return time
      }
    },
    add () {
      this.postAdd.visiable = true
    },
    handlepostAddClose () {
      this.postAdd.visiable = false
    },
    handlepostAddSuccess () {
      this.postAdd.visiable = false
      this.$message.success('新增贴子成功')
      this.getPostList(this.tabKey)
    },
    edit (record) {
      this.$refs.postEdit.setFormValues(record)
      this.postEdit.visiable = true
    },
    handlepostEditClose () {
      this.postEdit.visiable = false
    },
    handlepostEditSuccess () {
      this.postEdit.visiable = false
      this.$message.success('修改贴子成功')
      this.postReplyDetail(this.postDetail)
    },
    onSearch (key) {
      if (key !== '') {
        this.loading = true
        if (this.tagList[this.tagList.length - 1].id !== 9999) {
          this.tagList.push({id: 9999, name: '搜索'})
        }
        this.tabKey = 9999
        this.tabName = '搜索'
        this.$get(`/business/post-info/list/${key}`).then((r) => {
          this.postList = r.data.data
          setTimeout(() => {
            this.loading = false
          }, 500)
        })
      }
    }
  }
}
</script>
<style lang="less" scoped>.scenic-carousel-wrapper {
  border-radius: 2px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  position: relative;
  height: 360px;

  // 轮播图基础样式
  ::v-deep .ant-carousel .slick-slider {
    height: 100%;
  }

  ::v-deep .ant-carousel .slick-list {
    height: 100%;
  }

  ::v-deep .ant-carousel .slick-track {
    height: 100%;
  }

  ::v-deep .ant-carousel .slick-slide {
    height: 360px;
    position: relative;
  }
}

.carousel-slide {
  position: relative;
  height: 100%;

  .carousel-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .slide-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to right, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.3) 50%, transparent 100%);

    .slide-content {
      position: absolute;
      top: 50%;
      left: 5%;
      transform: translateY(-50%);
      color: white;
      max-width: 60%;
      z-index: 2;

      .slide-title {
        color: white;
        font-size: 28px;
        font-weight: 700;
        margin-bottom: 12px;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
      }

      .slide-description {
        font-size: 16px;
        margin-bottom: 24px;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
        line-height: 1.5;
      }

      .explore-btn {
        border-radius: 24px;
        padding: 0 28px;
        height: 42px;
        font-weight: 500;
        background: rgba(255, 255, 255, 0.2);
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: white;
        font-size: 15px;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
        }
      }
    }
  }
}
</style>

<style lang="less" scoped>.scenic-carousel-wrapper {
  border-radius: 2px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  position: relative;
  height: 360px;

  // 轮播图基础样式
  ::v-deep .ant-carousel .slick-slider {
    height: 100%;
  }

  ::v-deep .ant-carousel .slick-list {
    height: 100%;
  }

  ::v-deep .ant-carousel .slick-track {
    height: 100%;
  }

  ::v-deep .ant-carousel .slick-slide {
    height: 360px;
    position: relative;
  }
}

.carousel-slide {
  position: relative;
  height: 100%;

  .carousel-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .slide-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to right, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.3) 50%, transparent 100%);

    .slide-content {
      position: absolute;
      top: 50%;
      left: 5%;
      transform: translateY(-50%);
      color: white;
      max-width: 60%;
      z-index: 2;

      .slide-title {
        color: white;
        font-size: 28px;
        font-weight: 700;
        margin-bottom: 12px;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
      }

      .slide-description {
        font-size: 16px;
        margin-bottom: 24px;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
        line-height: 1.5;
      }

      .explore-btn {
        border-radius: 24px;
        padding: 0 28px;
        height: 42px;
        font-weight: 500;
        background: rgba(255, 255, 255, 0.2);
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: white;
        font-size: 15px;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
        }
      }
    }
  }
}
</style>
