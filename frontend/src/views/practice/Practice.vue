
<template>
  <div class="page-container">
    <a-row :gutter="16" class="content-wrapper">
      <a-col :span="12" :offset="6">
        <a-card
          :loading="loading"
          :bordered="false"
          class="practice-card"
          :bodyStyle="{ padding: '24px' }"
        >
          <div slot="title" class="card-header">
            <a-icon type="file-text" theme="filled" style="color: #1890ff; font-size: 24px;" />
            <span class="header-title">{{ bankName }}</span>
            <a-divider type="vertical" style="height: 24px; margin: 0 12px;" />
            <span class="header-subtitle">在线答题</span>

            <div class="header-actions">
              <a-tag color="blue">
                <a-icon type="clock-circle" /> 限时：{{ timeLimit }}分钟
              </a-tag>
              <a-tag color="green">
                <a-icon type="check-circle" /> 总题数：{{ questions.length }}
              </a-tag>
            </div>
          </div>

          <!-- 答题进度 -->
          <div class="progress-section" v-if="questions.length > 0" style="margin-bottom: 15px">
            <a-progress
              :percent="progressPercent"
              :stroke-color="progressColor"
              status="active"
              :format="progressFormat"
            />
          </div>

          <!-- 题目列表 -->
          <div class="question-list" v-if="questions.length > 0 && !submitted">
            <div
              v-for="(question, qIndex) in questions"
              :key="question.id"
              class="question-item"
              :class="{ 'current-question': currentQuestionIndex === qIndex }"
            >
              <div class="question-header">
                <div class="question-info">
                  <a-tag :color="getQuestionTypeColor(question.type)" class="question-type">
                    {{ question.type }}
                  </a-tag>
                  <span class="question-title">
                    第{{ qIndex + 1 }}题：{{ question.title }}
                  </span>
                </div>
                <div class="question-score">
                  <a-tag color="orange">
                    <a-icon type="star" theme="filled" /> {{ question.score }}分
                  </a-tag>
                </div>
              </div>

              <div class="question-options">
                <div
                  v-for="(option, oIndex) in question.options"
                  :key="oIndex"
                  class="option-item"
                  :class="{
                    'selected': isOptionSelected(question, option),
                    'correct': submitted && isCorrectOption(question, option),
                    'incorrect': submitted && isUserWrongOption(question, option)
                  }"
                  @click="selectOption(question, option)"
                >
                  <div class="option-mark">
                    <a-checkbox
                      :checked="isOptionSelected(question, option)"
                      :indeterminate="false"
                      disabled
                    />
                  </div>
                  <div class="option-content">
                    <span class="option-label">{{ getOptionLabel(oIndex) }}</span>
                    <span class="option-text">{{ option }}</span>
                  </div>
                  <div class="option-status" v-if="submitted">
                    <a-icon
                      v-if="isCorrectOption(question, option)"
                      type="check-circle"
                      theme="filled"
                      style="color: #52c41a; font-size: 20px;"
                    />
                    <a-icon
                      v-else-if="isUserWrongOption(question, option)"
                      type="close-circle"
                      theme="filled"
                      style="color: #ff4d4f; font-size: 20px;"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 题目列表 -->
          <div class="question-list" v-if="questions.length > 0 && submitted">
            <div
              v-for="(question, qIndex) in questions"
              :key="question.id"
              class="question-item"
              :class="{ 'current-question': currentQuestionIndex === qIndex }"
            >
              <div class="question-header">
                <div class="question-info">
                  <a-tag :color="getQuestionTypeColor(question.type)" class="question-type">
                    {{ question.type }}
                  </a-tag>
                  <span class="question-title">
                    第{{ qIndex + 1 }}题：{{ question.title }}
                  </span>
                </div>
                <div class="question-score">
                  <a-tag color="orange">
                    <a-icon type="star" theme="filled" /> {{ question.score }}分
                  </a-tag>
                </div>
              </div>

              <div class="question-options">
                <div
                  v-for="(option, oIndex) in question.options"
                  :key="oIndex"
                  class="option-item"
                  :class="{
                    'selected': isOptionSelected(question, option),
                    'correct': submitted && isCorrectOption(question, option),
                    'incorrect': submitted && isUserWrongOption(question, option)
                  }"
                  @click="selectOption(question, option)"
                >
                  <div class="option-mark">
                    <a-checkbox
                      :checked="isOptionSelected(question, option)"
                      :indeterminate="false"
                      disabled
                    />
                  </div>
                  <div class="option-content">
                    <span class="option-label">{{ getOptionLabel(oIndex) }}</span>
                    <span class="option-text">{{ option }}</span>
                  </div>
                  <div class="option-status" v-if="submitted">
                    <a-icon
                      v-if="isCorrectOption(question, option)"
                      type="check-circle"
                      theme="filled"
                      style="color: #52c41a; font-size: 20px;"
                    />
                    <a-icon
                      v-else-if="isUserWrongOption(question, option)"
                      type="close-circle"
                      theme="filled"
                      style="color: #ff4d4f; font-size: 20px;"
                    />
                  </div>
                </div>
              </div>

              <!-- 答案解析 -->
              <div class="answer-explanation" v-if="submitted">
                <div class="explanation-box">
                  <div class="explanation-header">
                    <a-icon type="book" style="color: #52c41a; font-size: 18px; margin-right: 8px;" />
                    <span class="explanation-title">答案解析</span>

                    <div class="explanation-actions">
                      <a-button
                        size="small"
                        :type="question.isCollected ? 'primary' : 'default'"
                        @click="toggleCollection(question)"
                        class="collect-btn"
                      >
                        <a-icon :type="question.isCollected ? 'star' : 'star-o'" />
                        {{ question.isCollected ? '已收藏' : '收藏题目' }}
                      </a-button>
                    </div>
                  </div>

                  <div class="explanation-content">
                    <div class="correct-answer" v-if="question.correctAnswer && question.correctAnswer.length > 0">
                      <div class="answer-label correct-label">
                        <a-icon type="check-circle" theme="filled" style="margin-right: 6px;" />
                        <strong>正确答案：</strong>
                      </div>
                      <div class="answer-tags">
                        <a-tag
                          v-for="(ans, aIndex) in question.correctAnswer"
                          :key="aIndex"
                          color="green"
                          class="answer-tag"
                        >
                          {{ getOptionLabel(question.options.indexOf(ans)) }}. {{ ans }}
                        </a-tag>
                      </div>
                    </div>

                    <div class="user-answer" v-if="question.userAnswer && question.userAnswer.length > 0">
                      <div class="answer-label user-label">
                        <a-icon type="user" style="margin-right: 6px;" />
                        <strong>您的答案：</strong>
                      </div>
                      <div class="answer-tags">
                        <a-tag
                          v-for="(uAns, uIndex) in (Array.isArray(question.userAnswer) ? question.userAnswer : [question.userAnswer])"
                          :key="uIndex"
                          :color="question.isCorrect ? 'green' : 'red'"
                          class="answer-tag"
                        >
                          {{ getOptionLabel(question.options.indexOf(uAns)) }}. {{ uAns }}
                        </a-tag>
                        <span class="result-badge">
                          <a-tag :color="question.isCorrect ? 'success' : 'error'" class="result-tag">
                            <a-icon :type="question.isCorrect ? 'check' : 'close'" />
                            {{ question.isCorrect ? '正确' : '错误' }}
                          </a-tag>
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <a-empty
            v-if="!loading && questions.length === 0"
            description="暂无题目"
            class="empty-state"
          />

          <!-- 提交按钮 -->
          <div class="form-actions" v-if="questions.length > 0 && !submitted">
            <a-button
              key="submit"
              type="primary"
              :loading="submitting"
              @click="handleSubmit"
              class="submit-btn"
              size="large"
            >
              <a-icon type="check-circle" /> 提交答案
            </a-button>
            <a-popconfirm
              title="确定要退出答题吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleExit"
            >
              <a-button
                key="exit"
                class="exit-btn"
                size="large"
              >
                <a-icon type="close-circle" /> 退出答题
              </a-button>
            </a-popconfirm>
          </div>

          <!-- 结果展示 -->
          <div class="result-section" v-if="submitted && resultData">
            <a-divider orientation="left">
              <a-icon type=" Trophy" theme="filled" style="color: #faad14;" />
              答题结果
            </a-divider>

            <a-row :gutter="16" class="result-stats">
              <a-col :span="6">
                <a-statistic
                  title="总得分"
                  :value="resultData.totalScore"
                  :value-style="{ color: resultData.pass ? '#52c41a' : '#ff4d4f' }"
                  suffix="分"
                >
                  <template #prefix>
                    <a-icon type="trophy" theme="filled" style="color: #faad14;" />
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="6">
                <a-statistic
                  title="正确率"
                  :value="resultData.accuracy"
                  :value-style="{ color: '#1890ff' }"
                  suffix="%"
                >
                  <template #prefix>
                    <a-icon type="pie-chart" />
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="6">
                <a-statistic
                  title="用时"
                  :value="resultData.timeSpent"
                  :value-style="{ color: '#722ed1' }"
                  suffix="秒"
                >
                  <template #prefix>
                    <a-icon type="clock-circle" />
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="6">
                <a-statistic
                  title="状态"
                  :value="resultData.pass ? '通过' : '未通过'"
                  :value-style="{ color: resultData.pass ? '#52c41a' : '#ff4d4f' }"
                >
                  <template #prefix>
                    <a-badge :status="resultData.pass ? 'success' : 'error'" />
                  </template>
                </a-statistic>
              </a-col>
            </a-row>

            <div class="result-actions">
<!--              <a-button type="primary" @click="handleRetake" size="large">-->
<!--                <a-icon type="reload" /> 重新答题-->
<!--              </a-button>-->
              <a-button @click="handleBackToBank" size="large">
                <a-icon type="arrow-left" /> 返回题库
              </a-button>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>import { mapState } from 'vuex'

export default {
  name: 'Practice',
  data () {
    return {
      loading: false,
      submitting: false,
      bankId: null,
      bankName: '',
      questions: [],
      userAnswers: {},
      submitted: false,
      timeLimit: 30,
      startTime: null,
      endTime: null,
      resultData: null,
      currentQuestionIndex: 0
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    progressPercent () {
      if (this.questions.length === 0) return 0
      const answeredCount = Object.keys(this.userAnswers).length
      return Math.round((answeredCount / this.questions.length) * 100)
    },
    progressColor () {
      if (this.progressPercent === 100) return '#52c41a'
      if (this.progressPercent >= 50) return '#1890ff'
      return '#faad14'
    }
  },
  created () {
    this.bankId = this.$route.query.bankId
    this.bankName = this.$route.query.bankName || '在线答题'
    this.startTime = new Date().getTime()
  },
  mounted () {
    if (this.bankId) {
      this.loadQuestions()
    } else {
      this.$message.error('缺少题库信息')
      this.$router.push('/bank')
    }
  },
  methods: {
    loadQuestions () {
      this.loading = true
      this.$get(`/business/collection-info/queryCollectionListByBank`, {
        bankId: this.bankId
      }).then((r) => {
        console.log('题目数据:', r)
        if (r.data.code === 0 && r.data.data) {
          this.questions = r.data.data.map(item => {
            const info = item.collectionInfo
            const options = item.collectionOptionsList || []

            return {
              id: info.id,
              code: info.code,
              type: info.questionType,
              score: info.score,
              title: info.desc,
              tags: info.tags,
              options: options.map(opt => opt.content),
              correctAnswer: options.filter(opt => opt.isCorrect === 1).map(opt => opt.content),
              userAnswer: info.questionType === '多选题' ? [] : null,
              explanation: info.desc,
              isCollected: false
            }
          })
          console.log('处理后的题目:', this.questions)
          this.$message.success(`加载了 ${this.questions.length} 道题目`)
        } else {
          this.$message.error(r.data.msg || '加载题目失败')
        }
      }).catch((error) => {
        console.error('加载题目错误:', error)
        this.$message.error('网络错误，请稍后重试')
      }).finally(() => {
        this.loading = false
      })
    },

    getQuestionTypeColor (type) {
      const colors = {
        '单选题': 'blue',
        '多选题': 'purple',
        '判断题': 'green'
      }
      return colors[type] || 'default'
    },

    getOptionLabel (index) {
      const labels = ['A', 'B', 'C', 'D', 'E', 'F']
      return labels[index] || `${index + 1}`
    },

    selectOption (question, option) {
      if (this.submitted) return

      if (question.type === '单选题') {
        this.$set(this.userAnswers, question.id, option)
        question.userAnswer = option
      } else if (question.type === '多选题') {
        if (!question.userAnswer) {
          question.userAnswer = []
        }
        const index = question.userAnswer.indexOf(option)
        if (index > -1) {
          question.userAnswer.splice(index, 1)
        } else {
          question.userAnswer.push(option)
        }
        this.$set(this.userAnswers, question.id, [...question.userAnswer])
      }
    },

    isOptionSelected (question, option) {
      if (!question.userAnswer) return false
      if (question.type === '单选题') {
        return question.userAnswer === option
      } else {
        return question.userAnswer.includes(option)
      }
    },

    isCorrectOption (question, option) {
      if (!question.correctAnswer) return false
      if (question.type === '单选题') {
        return question.correctAnswer === option
      } else {
        return question.correctAnswer.includes(option)
      }
    },

    isUserWrongOption (question, option) {
      if (!question.userAnswer) return false
      return question.userAnswer.includes(option) && !this.isCorrectOption(question, option)
    },

    toggleCollection (question) {
      if (!this.currentUser) {
        this.$message.warning('请先登录')
        return
      }

      const action = question.isCollected ? '取消收藏' : '收藏'

      this.$post('/business/user-favorites', {
        topicId: question.id,
        userId: this.currentUser.userId
      }).then((r) => {
        if (r.data.code === 0) {
          question.isCollected = !question.isCollected
          this.$message.success(`${action}成功！`)
        } else {
          this.$message.error(r.data.msg || `${action}失败`)
        }
      }).catch((error) => {
        console.error('收藏操作错误:', error)
        this.$message.error('网络错误，请稍后重试')
      })
    },

    handleSubmit () {
      const unansweredCount = this.questions.filter(q => !q.userAnswer ||
        (Array.isArray(q.userAnswer) && q.userAnswer.length === 0)
      ).length

      if (unansweredCount > 0) {
        this.$modal.confirm({
          title: '确认提交',
          content: `还有 ${unansweredCount} 道题目未作答，确定要提交吗？`,
          onOk: () => {
            this.submitAnswer()
          }
        })
      } else {
        this.submitAnswer()
      }
    },

    submitAnswer () {
      this.submitting = true
      this.endTime = new Date().getTime()

      // 计算答题结果
      let totalScore = 0
      let correctCount = 0
      const maxScore = this.questions.reduce((sum, q) => sum + q.score, 0)

      const answerRecords = this.questions.map(question => {
        let isCorrect = false

        if (question.type === '单选题') {
          // 单选题：完全匹配
          isCorrect = question.userAnswer === question.correctAnswer[0]
        } else if (question.type === '多选题') {
          // 多选题：选项完全一致（不考虑顺序）
          const userAns = [...(question.userAnswer || [])].sort()
          const correctAns = [...question.correctAnswer].sort()
          isCorrect = userAns.length === correctAns.length &&
            userAns.every((val, idx) => val === correctAns[idx])
        }

        if (isCorrect) {
          correctCount++
          totalScore += question.score
        }

        question.isCorrect = isCorrect

        // 构建单题记录
        return {
          questionId: question.id,
          questionCode: question.code,
          questionTitle: question.title,
          questionType: question.type,
          options: question.options,
          correctAnswer: question.correctAnswer,
          userAnswer: question.userAnswer,
          score: question.score,
          isCorrect: isCorrect
        }
      })

      const accuracy = this.questions.length > 0
        ? Math.round((correctCount / this.questions.length) * 100)
        : 0

      const timeSpent = Math.round((this.endTime - this.startTime) / 1000)

      // 构建完整的答题记录 JSON
      const practiceRecord = {
        bankId: this.bankId,
        bankName: this.bankName,
        userId: this.currentUser ? this.currentUser.userId : null,
        totalScore: totalScore,
        maxScore: maxScore,
        correctCount: correctCount,
        totalCount: this.questions.length,
        accuracy: accuracy,
        timeSpent: timeSpent,
        pass: totalScore >= maxScore * 0.6,
        answerRecords: answerRecords
      }

      console.log('答题记录 JSON:', JSON.stringify(practiceRecord, null, 2))

      // 保存到数据库
      this.$post('/business/answer-record', {
        bankId: this.bankId,
        totalScore: totalScore,
        userId: this.currentUser ? this.currentUser.userId : null,
        score: maxScore,
        correctCount: correctCount,
        totalCount: this.questions.length,
        accuracy: accuracy,
        timeSpent: timeSpent,
        pass: totalScore >= maxScore * 0.6 ? 1 : 0,
        answerDetail: JSON.stringify(answerRecords)
      }).then((r) => {
        console.log('保存答题记录结果:', r)
      }).catch((error) => {
        console.error('保存答题记录错误:', error)
      })

      // 设置结果数据
      this.resultData = {
        totalScore: totalScore,
        maxScore: maxScore,
        correctCount: correctCount,
        totalCount: this.questions.length,
        accuracy: accuracy,
        timeSpent: timeSpent,
        pass: totalScore >= maxScore * 0.6
      }

      this.submitted = true
      this.submitting = false

      this.$message.success(`提交成功！得分：${totalScore}分`)

      // 滚动到顶部查看结果
      this.$nextTick(() => {
        window.scrollTo({ top: 0, behavior: 'smooth' })
      })
    },

    handleExit () {
      this.$message.info('已退出答题')
      this.$router.push('/bank')
    },

    handleRetake () {
      this.$router.push({
        path: '/practice',
        query: {
          bankId: this.bankId,
          bankName: this.bankName
        }
      })
    },

    handleBackToBank () {
      this.$router.push('/bank')
    },

    progressFormat (percent) {
      const answeredCount = Math.round((percent / 100) * this.questions.length)
      return `${answeredCount}/${this.questions.length}`
    }
  }
}
</script>

<style scoped lang="less">.page-container {
  padding: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f6f6f6 100%);
  min-height: calc(100vh - 64px);
}

.content-wrapper {
  .practice-card {
    border-radius: 3px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);

    .card-header {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;
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

      .header-actions {
        margin-left: auto;
        display: flex;
        gap: 8px;
      }
    }

    .progress-section {
      margin-bottom: 24px;
    }

    .question-list {
      .question-item {
        padding: 24px;
        margin-bottom: 24px;
        background: #fafafa;
        border-radius: 3px;
        border: 2px solid transparent;
        transition: all 0.3s;

        &.current-question {
          border-color: #1890ff;
          background: #e6f7ff;
        }

        .question-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;

          .question-info {
            display: flex;
            align-items: center;
            gap: 12px;

            .question-type {
              font-size: 14px;
              font-weight: 600;
            }

            .question-title {
              font-size: 16px;
              font-weight: 600;
              color: #333;
            }
          }

          .question-score {
            .ant-tag {
              font-size: 14px;
              padding: 4px 12px;
            }
          }
        }

        .question-options {
          .option-item {
            display: flex;
            align-items: center;
            padding: 16px;
            margin-bottom: 12px;
            background: white;
            border: 2px solid #e8e8e8;
            border-radius: 3px;
            cursor: pointer;
            transition: all 0.3s;

            &:hover {
              border-color: #1890ff;
              background: #e6f7ff;
              transform: translateX(4px);
            }

            &.selected {
              border-color: #1890ff;
              background: #e6f7ff;
            }

            &.correct {
              border-color: #52c41a;
              background: #f6ffed;
            }

            &.incorrect {
              border-color: #ff4d4f;
              background: #fff1f0;
            }

            .option-mark {
              margin-right: 12px;
            }

            .option-content {
              flex: 1;
              display: flex;
              gap: 8px;

              .option-label {
                font-weight: bold;
                color: #1890ff;
                min-width: 24px;
              }

              .option-text {
                flex: 1;
                color: #333;
              }
            }

            .option-status {
              margin-left: 12px;
            }
          }
        }

        .answer-explanation {
          margin-top: 20px;
          padding-top: 20px;
          border-top: 2px dashed #e8e8e8;
        }
      }
    }

    .empty-state {
      padding: 60px 0;
    }

    .form-actions {
      display: flex;
      gap: 16px;
      justify-content: center;
      padding-top: 24px;
      border-top: 2px solid #f0f0f0;

      .submit-btn,
      .exit-btn {
        min-width: 160px;
        height: 44px;
        font-size: 16px;
        border-radius: 3px;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
        }
      }

      .submit-btn {
        background: linear-gradient(135deg, #6ec527 0%, #149817 100%);
        border: none;

        &:hover {
          box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
        }
      }
    }

    .result-section {
      margin-top: 32px;
      padding-top: 32px;

      .result-stats {
        margin-bottom: 32px;

        .ant-statistic {
          text-align: center;
        }
      }

      .result-actions {
        display: flex;
        gap: 16px;
        justify-content: center;
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
    .practice-card {
      .card-header {
        flex-direction: column;
        align-items: flex-start;

        .header-actions {
          margin-left: 0;
          width: 100%;
          margin-top: 12px;
        }
      }

      .question-list {
        .question-item {
          .question-header {
            flex-direction: column;
            align-items: flex-start;
            gap: 12px;

            .question-score {
              align-self: flex-end;
            }
          }
        }
      }
    }
  }
}

.answer-explanation {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px dashed #e8e8e8;

  .explanation-box {
    background: linear-gradient(135deg, #f6ffed 0%, #fafafa 100%);
    border: 2px solid #b7eb8f;
    border-radius: 3px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(82, 196, 26, 0.1);

    .explanation-header {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 2px solid #d9f7be;

      .explanation-title {
        font-size: 16px;
        font-weight: 600;
        color: #52c41a;
      }
    }

    .explanation-content {
      .correct-answer {
        margin-bottom: 16px;

        .answer-label {
          display: flex;
          align-items: center;
          margin-bottom: 10px;
          font-size: 14px;

          &.correct-label {
            color: #52c41a;

            strong {
              font-size: 15px;
            }
          }

          &.user-label {
            color: #1890ff;

            strong {
              font-size: 15px;
            }
          }
        }

        .answer-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          padding-left: 30px;

          .answer-tag {
            margin: 0;
            font-size: 13px;
            padding: 4px 12px;
          }
        }
      }

      .user-answer {
        margin-bottom: 16px;

        .answer-label {
          display: flex;
          align-items: center;
          margin-bottom: 10px;
          font-size: 14px;
        }

        .answer-tags {
          display: flex;
          flex-wrap: wrap;
          align-items: center;
          gap: 8px;
          padding-left: 30px;

          .answer-tag {
            margin: 0;
            font-size: 13px;
            padding: 4px 12px;
          }

          .result-badge {
            .result-tag {
              font-size: 13px;
              padding: 4px 12px;
              font-weight: 600;
            }
          }
        }
      }

      .explanation-text {
        background: white;
        padding: 16px;
        border-radius: 3px;
        border-left: 4px solid #52c41a;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);

        .explanation-label {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          color: #52c41a;
          font-size: 14px;
          font-weight: 600;
        }

        .explanation-detail {
          color: #333;
          line-height: 1.8;
          margin: 0;
          padding-left: 30px;
          text-indent: -30px;
        }
      }
    }
  }
}

.explanation-box {
  .explanation-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 2px solid #d9f7be;

    .explanation-title {
      font-size: 16px;
      font-weight: 600;
      color: #52c41a;
    }

    .explanation-actions {
      .collect-btn {
        display: flex;
        align-items: center;
        gap: 6px;
        transition: all 0.3s;

        &:hover {
          transform: scale(1.05);
        }

        &.ant-btn-primary {
          background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
          border: none;

          &:hover {
            background: linear-gradient(135deg, #ffc53d 0%, #ffd666 100%);
          }
        }
      }
    }
  }
}
</style>
