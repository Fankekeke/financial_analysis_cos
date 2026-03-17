
<template>
  <div class="ai-chat-container">
    <a-card :bordered="false" class="chat-card">
      <!-- 标题区域 -->
      <div class="chat-header">
        <h2 class="chat-title">
          <a-icon type="message" theme="filled" style="color: #1890ff; margin-right: 12px;" />
          党员知识问答
        </h2>
      </div>
      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messageContainer">
        <div
          v-for="(message, index) in messages"
          :key="index"
          class="message"
          :class="message.type"
        >
          <div class="message-avatar">
            <a-icon v-if="message.type === 'user'" type="user" />
            <a-icon v-else type="robot"/>
          </div>
          <div class="message-content">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>

        <!-- 加载中状态 -->
        <div v-if="loading" class="message ai">
          <div class="message-avatar">
            <a-icon type="robot" />
          </div>
          <div class="message-content">
            <a-spin tip="AI 正在思考中..." />
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <a-textarea
          v-model="inputMessage"
          placeholder="请输入您的问题，按 Enter 键发送..."
          :rows="3"
          @pressEnter="handleSendMessage"
          :disabled="loading"
        />
        <a-button
          type="primary"
          style="margin-top: 20px"
          @click="handleSendMessage"
          :loading="loading"
          class="send-btn"
        >
          <a-icon type="send" /> 发送
        </a-button>
      </div>
    </a-card>
  </div>
</template>

<script>import { mapState } from 'vuex'
import moment from 'moment'

export default {
  name: 'AiChat',
  data () {
    return {
      inputMessage: '',
      messages: [
        {
          type: 'ai',
          content: '您好！我是党员知识问答助手，有任何关于党员知识的问题都可以问我哦！',
          time: moment().format('HH:mm:ss')
        }
      ],
      loading: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  mounted () {
    this.scrollToBottom()
  },
  methods: {
    handleSendMessage () {
      if (!this.inputMessage.trim()) {
        this.$message.warning('请输入问题内容')
        return
      }

      if (this.loading) {
        return
      }

      // 添加用户消息
      const userMessage = {
        type: 'user',
        content: this.inputMessage.trim(),
        time: moment().format('HH:mm:ss')
      }
      this.messages.push(userMessage)

      const question = this.inputMessage.trim()
      this.inputMessage = ''
      this.loading = true

      // 调用后端 API 发送问题获取 AI 回答
      this.$get('/business/ai/aliTyqw', {
        content: question,
        userId: this.currentUser ? this.currentUser.userId : ''
      }).then((r) => {
        if (r.data.code === 0 && r.data.msg) {
          const aiMessage = {
            type: 'ai',
            content: r.data.msg || '抱歉，暂时无法回答问题',
            time: moment().format('HH:mm:ss')
          }
          this.messages.push(aiMessage)
        }
      }).catch((error) => {
        console.error('AI 问答错误:', error)
        this.$message.error('网络错误，请稍后重试')
      }).finally(() => {
        this.loading = false
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      })

      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    scrollToBottom () {
      this.$nextTick(() => {
        const container = this.$refs.messageContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-header {
  padding: 16px 20px;
  background-color: #fafafa;
  border-bottom: 1px solid #e8e8e8;

  .chat-title {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: #333;
    display: flex;
    align-items: center;
  }
}
.ai-chat-container {
  height: calc(100vh - 120px);
  padding: 16px;
  background-color: #f5f5f5;
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 3px;
  box-shadow: rgba(0, 0, 0, 0.1) 0px 4px 6px -1px, rgba(0, 0, 0, 0.06) 0px 2px 4px -1px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #fafafa;
}

.message {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;

  &.user {
    flex-direction: row-reverse;

    .message-avatar {
      margin-left: 12px;
      margin-right: 0;
      background-color: #1890ff;
      color: white;
    }

    .message-content {
      .message-text {
        background-color: #1890ff;
        color: white;
        border-radius: 3px 3px 0 3px;
      }

      .message-time {
        text-align: right;
        color: #999;
      }
    }
  }

  &.ai {
    .message-avatar {
      margin-right: 12px;
      background-color: #52c41a;
      color: white;
      font-size: 24px;
    }

    .message-content {
      .message-text {
        background-color: white;
        color: #333;
        border-radius: 3px 3px 3px 0;
        border: 1px solid #e8e8e8;
      }

      .message-time {
        text-align: left;
        color: #999;
      }
    }
  }
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 20px;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
}

.message-text {
  padding: 12px 16px;
  word-wrap: break-word;
  white-space: pre-wrap;
  box-shadow: rgba(0, 0, 0, 0.1) 0px 4px 6px -1px, rgba(0, 0, 0, 0.06) 0px 2px 4px -1px;
}

.message-time {
  font-size: 12px;
  margin-top: 4px;
}

.chat-input-area {
  padding: 16px;
  background-color: white;
  border-top: 1px solid #e8e8e8;
  border-radius: 0 0 3px 3px;

  .ant-textarea {
    margin-bottom: 12px;
  }

  .send-btn {
    float: right;
  }
}
</style>
