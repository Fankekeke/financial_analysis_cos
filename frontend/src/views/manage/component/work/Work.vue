<template>
  <div>
    <a-row :gutter="15">
      <a-col :span="8">
        <div class="user-basic">
          <img :src="'http://127.0.0.1:9527/imagesWeb/' + userBasic.avatar" alt="头像">
          <h3>{{ userBasic.userName }}</h3>
          <p>{{ userBasic.organization }}</p>
        </div>

        <!-- 里程碑数据 -->
        <div class="milestone-stats">
          <div class="stat-item">
            <span class="value">{{ milestoneData.totalQuestions }}</span>
            <span class="label">总题数</span>
          </div>
          <div class="stat-item">
            <span class="value">{{ milestoneData.totalLearningMinutes }}</span>
            <span class="label">学习时长 (分钟)</span>
          </div>
<!--          <div class="stat-item">-->
<!--            <span class="value">{{ milestoneData.currentIntegral }}</span>-->
<!--            <span class="label">当前积分</span>-->
<!--          </div>-->
<!--          <div class="stat-item">-->
<!--            <span class="value">{{ milestoneData.integralLevel }}</span>-->
<!--            <span class="label">积分等级</span>-->
<!--          </div>-->
          <div class="stat-item">
            <span class="value">{{ milestoneData.adoptedQuestions }}</span>
            <span class="label">已采纳题数</span>
          </div>
        </div>

        <!-- 今日推荐 -->
        <div class="recommendations">
          <div v-for="item in todayRecommendations" :key="item.id" class="recommend-item">
            <h4>{{ item.title }}</h4>
            <p>{{ item.summary }}</p>
            <span class="tag">{{ item.matchTag }}</span>
            <span class="date">{{ item.publishDate }}</span>
          </div>
        </div>

        <!-- 支部热点话题 -->
        <div class="hot-topics">
          <div v-for="topic in branchHotTopics" :key="topic.topicId" class="topic-item">
            <span class="rank">{{ topic.rankChange >= 0 ? '+' + topic.rankChange : topic.rankChange }}</span>
            <h4>{{ topic.title }}</h4>
            <span class="views">{{ topic.viewCount }} 次浏览</span>
            <span class="score">{{ topic.hotScore }} 热度</span>
          </div>
        </div>
      </a-col>
      <a-col :span="16">
        <ai-chat/>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import moment from 'moment'
import AiChat from "./AiChat.vue";
export default {
  name: 'Work',
  components: {AiChat},
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      dataLoading: false,
      userBasic: {}, // 用户基本信息
      milestoneData: {}, // 里程碑统计数据
      todayRecommendations: [], // 今日推荐
      branchHotTopics: [] // 支部热点话题
    }
  },
  mounted () {
    this.dataInit()
  },
  methods: {
    dataInit () {
      this.dataLoading = true
      this.$get(`/business/web/userHome`, {
        userId: this.currentUser.userId
      }).then((r) => {
        // 用户基本信息
        this.userBasic = r.data.data.userBasic || {}

        // 里程碑统计数据
        this.milestoneData = r.data.data.milestone || {}

        // 今日推荐
        this.todayRecommendations = r.data.data.todayRecommendations || []

        // 支部热点话题
        this.branchHotTopics = r.data.data.branchHotTopics || []
        this.dataLoading = false
      })
    }
  }
}
</script>

<style scoped lang="less">
.user-basic {
  padding: 24px;
  border-radius: 2px;
  text-align: center;
  color: #fff;
  margin-bottom: 20px;
  box-shadow: rgba(0, 0, 0, 0.1) 0px 4px 6px -1px, rgba(0, 0, 0, 0.06) 0px 2px 4px -1px;

  img {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    border: 3px solid rgba(255, 255, 255, 0.5);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    object-fit: cover;
  }

  h3 {
    font-size: 20px;
    font-weight: 600;
    margin: 12px 0 6px;
  }

  p {
    font-size: 13px;
    opacity: 0.9;
  }
}

.milestone-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
  margin-bottom: 20px;

  .stat-item {
    background: #fff;
    padding: 16px;
    border-radius: 3px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    border: 1px solid #e8ecff;

    .value {
      display: block;
      font-size: 28px;
      font-weight: 600;
      color: #4a90e2;
      margin-bottom: 6px;
    }

    .label {
      font-size: 12px;
      color: #666;
    }
  }

  .progress-bar {
    grid-column: 1 / -1;
    background: #f0f2f5;
    border-radius: 3px;
    padding: 4px 12px;
    position: relative;

    .progress {
      display: block;
      height: 12px;
      background: linear-gradient(90deg, #4a90e2, #6ba3e8);
      border-radius: 6px;
      transition: width 0.3s ease;
    }

    span {
      position: absolute;
      right: 12px;
      top: 50%;
      transform: translateY(-50%);
      font-size: 12px;
      font-weight: 500;
      color: #666;
    }
  }
}

.recommendations {
  margin-bottom: 20px;

  .recommend-item {
    background: #fff;
    border-left: 3px solid #4a90e2;
    padding: 14px 16px;
    margin-bottom: 10px;
    border-radius: 2px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
    transition: all 0.2s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transform: translateX(3px);
    }

    h4 {
      font-size: 15px;
      font-weight: 600;
      color: #333;
      margin: 0 0 6px;
      line-height: 1.4;
    }

    p {
      font-size: 13px;
      color: #666;
      margin: 0 0 10px;
      line-height: 1.5;
    }

    .tag {
      display: inline-block;
      padding: 3px 10px;
      background: #e8f0fe;
      color: #4a90e2;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 500;
      margin-right: 10px;
    }

    .date {
      font-size: 12px;
      color: #999;
    }
  }
}

.hot-topics {
  .topic-item {
    background: #fff;
    padding: 14px 16px;
    margin-bottom: 10px;
    border-radius: 3px;
    border: 1px solid #e8ecff;
    display: flex;
    align-items: center;
    gap: 12px;
    transition: all 0.2s ease;

    &:hover {
      border-color: #4a90e2;
      box-shadow: 0 3px 10px rgba(74, 144, 226, 0.1);
    }

    .rank {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 32px;
      height: 32px;
      background: #fff8e1;
      color: #ff9800;
      border-radius: 50%;
      font-weight: 600;
      font-size: 14px;
      border: 2px solid #ffe082;
      flex-shrink: 0;
    }

    h4 {
      flex: 1;
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin: 0;
    }

    .views {
      font-size: 12px;
      color: #666;
      padding: 3px 8px;
      background: #f5f7fa;
      border-radius: 3px;
    }

    .score {
      font-size: 12px;
      color: #f44336;
      font-weight: 600;
      padding: 3px 8px;
      background: #ffebee;
      border-radius: 3px;
    }
  }
}
</style>
