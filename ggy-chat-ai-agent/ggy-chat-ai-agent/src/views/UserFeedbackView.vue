<template>
    <div class="user-feedback-management elegant-gradient-bg user-feedback-theme">
        <div class="page-header colorful-page-header">
            <h2>用户反馈管理</h2>
            <a-space>
                <a-button type="default" @click="showStatsModal">
                    <template #icon>
                        <BarChartOutlined />
                    </template>
                    统计分析
                </a-button>
                <a-button type="primary" class="colorful-btn-primary" @click="showCreateModal">
                    <template #icon>
                        <PlusOutlined />
                    </template>
                    新增反馈
                </a-button>
            </a-space>
        </div>

        <!-- 统计概览卡片 -->
        <a-row :gutter="[16, 16]" class="stats-overview">
            <a-col :xs="12" :sm="12" :md="6" :lg="6">
                <a-card class="stats-card" :loading="loading">
                    <a-statistic
                        title="总反馈数"
                        :value="statsData.totalFeedbacks"
                        :value-style="{ color: '#3f8600', fontWeight: '600' }"
                        prefix="📊"
                    />
                </a-card>
            </a-col>
            <a-col :xs="12" :sm="12" :md="6" :lg="6">
                <a-card class="stats-card" :loading="loading">
                    <a-statistic
                        title="正常复制"
                        :value="statsData.normalCopy"
                        :value-style="{ color: '#1890ff', fontWeight: '600' }"
                        prefix="✅"
                    />
                </a-card>
            </a-col>
            <a-col :xs="12" :sm="12" :md="6" :lg="6">
                <a-card class="stats-card" :loading="loading">
                    <a-statistic
                        title="不满意反馈"
                        :value="statsData.dissatisfiedFeedback"
                        :value-style="{ color: '#ff4d4f', fontWeight: '600' }"
                        prefix="❌"
                    />
                </a-card>
            </a-col>
            <a-col :xs="12" :sm="12" :md="6" :lg="6">
                <a-card class="stats-card" :loading="loading">
                    <a-statistic
                        title="满意度"
                        :value="statsData.satisfactionRate"
                        suffix="%"
                        :precision="1"
                        :value-style="{ color: '#52c41a', fontWeight: '600' }"
                        prefix="🎯"
                    />
                </a-card>
            </a-col>
        </a-row>

        <!-- 搜索栏 -->
        <a-card class="search-card colorful-search-card fade-in-up" :bordered="false">
            <a-form layout="inline" :model="searchForm" @finish="handleSearch" class="colorful-form">
                <a-form-item label="聊天ID">
                    <a-input v-model:value="searchForm.chatId" placeholder="请输入聊天ID" allow-clear />
                </a-form-item>
                <a-form-item label="消息类型">
                    <a-select v-model:value="searchForm.messageType" placeholder="请选择消息类型" allow-clear
                        style="width: 150px">
                        <a-select-option :value="0">正常复制</a-select-option>
                        <a-select-option :value="1">不满意反馈</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="用户ID">
                    <a-input-number v-model:value="searchForm.userId" placeholder="请输入用户ID" style="width: 150px" />
                </a-form-item>
                <a-form-item>
                    <a-button type="primary" html-type="submit" :loading="loading">
                        <template #icon>
                            <SearchOutlined />
                        </template>
                        搜索
                    </a-button>
                    <a-button style="margin-left: 8px" @click="resetSearch">重置</a-button>
                </a-form-item>
            </a-form>
        </a-card>

        <!-- 统计分析弹窗 -->
        <a-modal v-model:open="statsModalVisible" title="反馈统计分析" :width="1200" :footer="null">
            <div class="stats-modal-content">
                <!-- 图表区域 -->
                <a-row :gutter="16" class="chart-row">
                    <a-col :span="12">
                        <a-card title="消息类型分布" class="chart-card">
                            <v-chart :option="pieChartOption" style="height: 300px;" />
                        </a-card>
                    </a-col>
                    <a-col :span="12">
                        <a-card title="按聊天ID分组统计" class="chart-card">
                            <v-chart :option="barChartOption" style="height: 300px;" />
                        </a-card>
                    </a-col>
                </a-row>
                
                <!-- 详细统计表格 -->
                <a-card title="详细统计数据" class="mt-4">
                    <a-table 
                        :dataSource="chatIdStats" 
                        :columns="statsColumns" 
                        :pagination="false"
                        size="small"
                        row-key="chatId"
                    >
                        <template #bodyCell="{ column, record }">
                            <template v-if="column.key === 'satisfactionRate'">
                                <a-progress 
                                    :percent="record.satisfactionRate" 
                                    size="small" 
                                    :status="record.satisfactionRate >= 80 ? 'success' : record.satisfactionRate >= 60 ? 'normal' : 'exception'"
                                />
                            </template>
                        </template>
                    </a-table>
                </a-card>
            </div>
        </a-modal>

        <!-- 数据表格 -->
        <a-card :bordered="false" class="table-card colorful-card scale-in colorful-table">
            <template #title>
                <div class="table-header">
                    <div class="table-title">
                        <h3>用户反馈列表</h3>
                        <a-tag v-if="!loading" color="blue">共 {{ pagination.total }} 条记录</a-tag>
                    </div>
                    <div class="table-actions">
                        <a-tooltip title="刷新数据">
                            <a-button type="text" :loading="loading" @click="loadData" :icon="h(ReloadOutlined)" />
                        </a-tooltip>
                    </div>
                </div>
            </template>
            <a-spin :spinning="loading" tip="正在加载数据...">
                <template #indicator>
                    <LoadingSpinner text="正在加载用户反馈..." type="pulse" theme="primary" />
                </template>
                <div class="table-container">
                    <a-table 
                        :dataSource="dataSource" 
                        :columns="columns" 
                        :loading="false" 
                        :pagination="pagination"
                        @change="handleTableChange" 
                        row-key="id"
                        :scroll="{ x: 1200 }"
                        size="middle"
                        :show-sorter-tooltip="false"
                    >
                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'action'">
                        <a-space>
                            <a-button type="link" size="small" @click="viewRecord(record)">查看</a-button>
                            <a-button type="link" size="small" @click="editRecord(record)">编辑</a-button>
                            <a-popconfirm title="确定要删除这条记录吗？" @confirm="deleteRecord(record.id)" ok-text="确定"
                                cancel-text="取消">
                                <a-button type="link" size="small" danger>删除</a-button>
                            </a-popconfirm>
                        </a-space>
                    </template>
                    <template v-else-if="column.key === 'messageType'">
                        <a-tag :color="record.messageType === 0 ? 'blue' : 'orange'">
                            {{ record.messageType === 0 ? '正常复制' : '不满意反馈' }}
                        </a-tag>
                    </template>
                    <template v-else-if="column.key === 'feedBackMessage'">
                        <a-tooltip :title="record.feedBackMessage">
                            <span>{{ record.feedBackMessage ? record.feedBackMessage.substring(0, 50) + '...' : '-'
                                }}</span>
                        </a-tooltip>
                    </template>
                    <template v-else-if="column.key === 'isDelete'">
                        <a-tag :color="record.isDelete === 0 ? 'green' : 'red'">
                            {{ record.isDelete === 0 ? '正常' : '已删除' }}
                        </a-tag>
                    </template>
                    <template v-else-if="column.key === 'createTime'">
                        <span>{{ formatTime(record.createTime) }}</span>
                    </template>
                </template>
                    </a-table>
                </div>
            </a-spin>
        </a-card>

        <!-- 创建/编辑弹窗 -->
        <a-modal v-model:open="modalVisible" :title="modalTitle" :width="800" @ok="handleSubmit" @cancel="handleCancel"
            :confirm-loading="submitLoading">
            <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
                <a-row :gutter="16">
                    <a-col :span="8">
                        <a-form-item label="用户ID" name="userId">
                            <a-input-number v-model:value="formData.userId" placeholder="请输入用户ID" style="width: 100%" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="8">
                        <a-form-item label="聊天ID" name="chatId">
                            <a-input v-model:value="formData.chatId" placeholder="请输入聊天ID" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="8">
                        <a-form-item label="消息类型" name="messageType">
                            <a-select v-model:value="formData.messageType" placeholder="请选择消息类型">
                                <a-select-option :value="0">正常复制</a-select-option>
                                <a-select-option :value="1">不满意反馈</a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-form-item label="反馈消息" name="feedBackMessage">
                    <a-textarea v-model:value="formData.feedBackMessage" placeholder="请输入反馈消息" :rows="4" />
                </a-form-item>
                <a-form-item label="结果结构" name="resultStructure">
                    <a-textarea v-model:value="formData.resultStructure" placeholder="请输入结果结构(JSON格式)" :rows="6" />
                </a-form-item>
                <a-form-item label="状态" name="isDelete">
                    <a-select v-model:value="formData.isDelete" placeholder="请选择状态">
                        <a-select-option :value="0">正常</a-select-option>
                        <a-select-option :value="1">已删除</a-select-option>
                    </a-select>
                </a-form-item>
            </a-form>
        </a-modal>

        <!-- 查看详情弹窗 -->
        <a-modal v-model:open="viewModalVisible" title="查看用户反馈详情" :width="900" :footer="null">
            <a-descriptions :column="2" bordered>
                <a-descriptions-item label="ID">{{ viewData.id }}</a-descriptions-item>
                <a-descriptions-item label="用户ID">{{ viewData.userId }}</a-descriptions-item>
                <a-descriptions-item label="聊天ID">{{ viewData.chatId }}</a-descriptions-item>
                <a-descriptions-item label="消息类型">
                    <a-tag :color="viewData.messageType === 0 ? 'blue' : 'orange'">
                        {{ viewData.messageType === 0 ? '正常复制' : '不满意反馈' }}
                    </a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="创建时间">{{ formatTime(viewData.createTime) }}</a-descriptions-item>
                <a-descriptions-item label="更新时间">{{ formatTime(viewData.updateTime) }}</a-descriptions-item>
                <a-descriptions-item label="状态">
                    <a-tag :color="viewData.isDelete === 0 ? 'green' : 'red'">
                        {{ viewData.isDelete === 0 ? '正常' : '已删除' }}
                    </a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="编辑时间">{{ formatTime(viewData.editTime) }}</a-descriptions-item>
                <a-descriptions-item label="反馈消息" :span="2">
                    <div style="max-height: 200px; overflow-y: auto; white-space: pre-wrap;">{{ viewData.feedBackMessage
                        }}
                    </div>
                </a-descriptions-item>
                <a-descriptions-item label="结果结构" :span="2">
                    <div style="max-height: 300px; overflow-y: auto;">
                        <pre style="background: #f6f8fa; padding: 12px; border-radius: 4px; margin: 0;">{{
                            viewData.resultStructure ? JSON.stringify(JSON.parse(viewData.resultStructure), null, 2) : '-' }}
                </pre>
                    </div>
                </a-descriptions-item>
            </a-descriptions>
        </a-modal>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, h } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, SearchOutlined, BarChartOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import api from '@/api'
import type { TableColumnsType } from 'ant-design-vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { formatTime } from '@/utils/time'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

// 注册必要的组件
use([
  CanvasRenderer,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dataSource = ref<API.FeedbackMessage[]>([])
const modalVisible = ref(false)
const viewModalVisible = ref(false)
const statsModalVisible = ref(false)
const modalTitle = ref('新增用户反馈')
const editingId = ref<number | null>(null)
const allFeedbackData = ref<API.FeedbackMessage[]>([])

// 搜索表单
const searchForm = reactive({
    chatId: '',
    messageType: undefined as number | undefined,
    userId: undefined as number | undefined
})

// 分页配置
const pagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: (total: number) => `共 ${total} 条记录`
})

// 表单数据
const formData = reactive<API.FeedbackMessage>({
    userId: undefined,
    chatId: '',
    messageType: 0,
    feedBackMessage: '',
    resultStructure: '',
    isDelete: 0
})

// 查看详情数据
const viewData = reactive<API.FeedbackMessage>({})

// 统计数据
const statsData = computed(() => {
    const total = allFeedbackData.value.length
    const normalCopy = allFeedbackData.value.filter(item => item.messageType === 0).length
    const dissatisfiedFeedback = allFeedbackData.value.filter(item => item.messageType === 1).length
    const satisfactionRate = total > 0 ? ((normalCopy / total) * 100) : 0
    
    return {
        totalFeedbacks: total,
        normalCopy,
        dissatisfiedFeedback,
        satisfactionRate
    }
})

// 按聊天ID分组的统计数据
const chatIdStats = computed(() => {
    const chatGroups = new Map<string, {
        chatId: string,
        totalCount: number,
        normalCount: number,
        dissatisfiedCount: number,
        satisfactionRate: number
    }>()
    
    allFeedbackData.value.forEach(item => {
        if (!item.chatId) return
        
        if (!chatGroups.has(item.chatId)) {
            chatGroups.set(item.chatId, {
                chatId: item.chatId,
                totalCount: 0,
                normalCount: 0,
                dissatisfiedCount: 0,
                satisfactionRate: 0
            })
        }
        
        const group = chatGroups.get(item.chatId)!
        group.totalCount++
        
        if (item.messageType === 0) {
            group.normalCount++
        } else if (item.messageType === 1) {
            group.dissatisfiedCount++
        }
        
        group.satisfactionRate = group.totalCount > 0 ? 
            Math.round((group.normalCount / group.totalCount) * 100) : 0
    })
    
    return Array.from(chatGroups.values()).sort((a, b) => b.totalCount - a.totalCount)
})

// 饼图配置
const pieChartOption = computed(() => ({
    title: {
        text: '消息类型分布',
        left: 'center',
        textStyle: {
            fontSize: 16
        }
    },
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
        bottom: '10%',
        left: 'center'
    },
    series: [
        {
            name: '消息类型',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '45%'],
            avoidLabelOverlap: false,
            label: {
                show: false,
                position: 'center'
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: '18',
                    fontWeight: 'bold'
                }
            },
            labelLine: {
                show: false
            },
            data: [
                {
                    value: statsData.value.normalCopy,
                    name: '正常复制',
                    itemStyle: { color: '#1890ff' }
                },
                {
                    value: statsData.value.dissatisfiedFeedback,
                    name: '不满意反馈',
                    itemStyle: { color: '#ff4d4f' }
                }
            ]
        }
    ]
}))

// 柱状图配置
const barChartOption = computed(() => {
    const top10Data = chatIdStats.value.slice(0, 10)
    
    return {
        title: {
            text: 'Top 10 聊天ID统计',
            left: 'center',
            textStyle: {
                fontSize: 16
            }
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            bottom: '5%',
            data: ['正常复制', '不满意反馈']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: top10Data.map(item => item.chatId.substring(0, 8) + '...'),
            axisLabel: {
                rotate: 45
            }
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name: '正常复制',
                type: 'bar',
                stack: 'total',
                data: top10Data.map(item => item.normalCount),
                itemStyle: { color: '#1890ff' }
            },
            {
                name: '不满意反馈',
                type: 'bar',
                stack: 'total',
                data: top10Data.map(item => item.dissatisfiedCount),
                itemStyle: { color: '#ff4d4f' }
            }
        ]
    }
})

// 统计表格列配置
const statsColumns: TableColumnsType = [
    {
        title: '聊天ID',
        dataIndex: 'chatId',
        key: 'chatId',
        width: 200,
        ellipsis: true
    },
    {
        title: '总反馈数',
        dataIndex: 'totalCount',
        key: 'totalCount',
        width: 100,
        sorter: (a: any, b: any) => a.totalCount - b.totalCount
    },
    {
        title: '正常复制',
        dataIndex: 'normalCount',
        key: 'normalCount',
        width: 100,
        sorter: (a: any, b: any) => a.normalCount - b.normalCount
    },
    {
        title: '不满意反馈',
        dataIndex: 'dissatisfiedCount',
        key: 'dissatisfiedCount',
        width: 120,
        sorter: (a: any, b: any) => a.dissatisfiedCount - b.dissatisfiedCount
    },
    {
        title: '满意度',
        dataIndex: 'satisfactionRate',
        key: 'satisfactionRate',
        width: 150,
        sorter: (a: any, b: any) => a.satisfactionRate - b.satisfactionRate
    }
]

// 表单引用
const formRef = ref()

// 表单验证规则
const formRules = {
    userId: [{ required: true, message: '请输入用户ID', type: 'number' }],
    chatId: [{ required: true, message: '请输入聊天ID' }],
    messageType: [{ required: true, message: '请选择消息类型', type: 'number' }]
}

// 表格列配置
const columns: TableColumnsType = [
    {
        title: 'ID',
        dataIndex: 'id',
        key: 'id',
        width: 80
    },
    {
        title: '用户ID',
        dataIndex: 'userId',
        key: 'userId',
        width: 100
    },
    {
        title: '聊天ID',
        dataIndex: 'chatId',
        key: 'chatId',
        width: 150
    },
    {
        title: '消息类型',
        dataIndex: 'messageType',
        key: 'messageType',
        width: 120
    },
    {
        title: '反馈消息',
        dataIndex: 'feedBackMessage',
        key: 'feedBackMessage',
        width: 200
    },
    {
        title: '状态',
        dataIndex: 'isDelete',
        key: 'isDelete',
        width: 80
    },
    {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        width: 160
    },
    {
        title: '操作',
        key: 'action',
        width: 150,
        fixed: 'right'
    }
]

// 加载所有数据用于统计
const loadAllData = async () => {
    try {
        const response = await api.yonghufankuiguanli.list1()
        if (response && response.data) {
            allFeedbackData.value = response.data
        }
    } catch (error) {
        console.error('加载统计数据失败:', error)
    }
}

// 显示统计分析弹窗
const showStatsModal = async () => {
    await loadAllData()
    statsModalVisible.value = true
}

// 加载数据
const loadData = async () => {
    loading.value = true
    try {
        // 统一使用 page1 接口 (/feedbackMessage/page)
        const params = {
            page: {
                pageNumber: pagination.current,
                pageSize: pagination.pageSize,
                // 添加搜索参数到page对象中
                ...(searchForm.chatId && { chatId: searchForm.chatId }),
                ...(searchForm.messageType !== undefined && { messageType: searchForm.messageType }),
                ...(searchForm.userId !== undefined && { userId: searchForm.userId })
            }
        }

        console.log('用户反馈-使用page1接口-发送的参数:', params)
        const response = await api.yonghufankuiguanli.page1(params)
        console.log('用户反馈-page1接口-API完整响应:', response)

        if (response && response.data) {
            console.log('用户反馈-page1接口-响应数据:', response.data)
            dataSource.value = response.data.records || []
            pagination.total = response.data.totalRow || 0
        } else {
            console.warn('用户反馈-page1接口-响应数据格式异常:', response)
            message.warning('响应数据格式异常')
        }
    } catch (error) {
        console.error('用户反馈-加载数据失败:', error)
        if (error.response) {
            console.error('用户反馈-错误响应:', error.response)
            message.error(`服务器错误: ${error.response.status} - ${error.response.statusText}`)
        } else if (error.request) {
            console.error('用户反馈-网络错误:', error.request)
            message.error('网络连接失败，请检查网络或后端服务')
        } else {
            console.error('用户反馈-其他错误:', error.message)
            message.error(`请求失败: ${error.message}`)
        }
    } finally {
        loading.value = false
    }
}

// 搜索
const handleSearch = () => {
    pagination.current = 1
    loadData()
}

// 重置搜索
const resetSearch = () => {
    searchForm.chatId = ''
    searchForm.messageType = undefined
    searchForm.userId = undefined
    pagination.current = 1
    loadData()
}

// 表格变化处理
const handleTableChange = (pag: any) => {
    pagination.current = pag.current
    pagination.pageSize = pag.pageSize
    loadData()
}

// 显示创建弹窗
const showCreateModal = () => {
    modalTitle.value = '新增用户反馈'
    editingId.value = null
    resetFormData()
    modalVisible.value = true
}

// 编辑记录
const editRecord = async (record: API.FeedbackMessage) => {
    modalTitle.value = '编辑用户反馈'
    editingId.value = record.id!

    try {
        const response = await api.yonghufankuiguanli.getInfo1({ id: record.id! })
        if (response.data) {
            Object.assign(formData, response.data)
        }
        modalVisible.value = true
    } catch (error) {
        console.error('获取详情失败:', error)
        message.error('获取详情失败')
    }
}

// 查看记录
const viewRecord = async (record: API.FeedbackMessage) => {
    try {
        const response = await api.yonghufankuiguanli.getInfo1({ id: record.id! })
        if (response.data) {
            Object.assign(viewData, response.data)
        }
        viewModalVisible.value = true
    } catch (error) {
        console.error('获取详情失败:', error)
        message.error('获取详情失败')
    }
}

// 删除记录
const deleteRecord = async (id: number) => {
    try {
        await api.yonghufankuiguanli.remove1({ id })
        message.success('删除成功')
        loadData()
    } catch (error) {
        console.error('删除失败:', error)
        message.error('删除失败')
    }
}

// 提交表单
const handleSubmit = async () => {
    try {
        await formRef.value.validate()
        submitLoading.value = true

        if (editingId.value) {
            // 编辑
            await api.yonghufankuiguanli.update1({ ...formData, id: editingId.value })
            message.success('更新成功')
        } else {
            // 新增
            await api.yonghufankuiguanli.save1(formData)
            message.success('创建成功')
        }

        modalVisible.value = false
        loadData()
    } catch (error) {
        console.error('提交失败:', error)
        message.error('提交失败')
    } finally {
        submitLoading.value = false
    }
}

// 取消弹窗
const handleCancel = () => {
    modalVisible.value = false
    resetFormData()
}

// 重置表单数据
const resetFormData = () => {
    Object.assign(formData, {
        userId: undefined,
        chatId: '',
        messageType: 0,
        feedBackMessage: '',
        resultStructure: '',
        isDelete: 0
    })
    formRef.value?.resetFields()
}

// 组件挂载时加载数据
onMounted(() => {
    loadData()
    loadAllData()
})
</script>

<style scoped>
.user-feedback-management {
    padding: 24px;
    max-width: 1600px;
    margin: 0 auto;
    width: 100%;
    background: #f5f5f5;
    min-height: 100vh;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px 0;
}

.page-header h2 {
    margin: 0;
    font-size: 28px;
    font-weight: 700;
    color: #1a1a1a;
    background: linear-gradient(135deg, #1890ff, #52c41a);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.search-card {
    margin-bottom: 20px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: none;
    transition: all 0.3s ease;
}

.search-card:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
}

/* 统计概览样式 */
.stats-overview {
    margin-bottom: 24px;
}

.stats-card {
    text-align: center;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border: none;
    transition: all 0.3s ease;
    overflow: hidden;
    background: linear-gradient(135deg, #fff, #fafafa);
}

.stats-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.table-card {
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    border: none;
    overflow: hidden;
    transition: all 0.3s ease;
}

.table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0;
}

.table-title {
    display: flex;
    align-items: center;
    gap: 12px;
}

.table-title h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #1a1a1a;
}

.table-actions {
    display: flex;
    align-items: center;
    gap: 8px;
}

.table-container {
    margin: -16px;
    margin-top: 0;
}

/* 统计分析弹窗样式 */
.stats-modal-content {
    padding: 16px 0;
}

.chart-row {
    margin-bottom: 24px;
}

.chart-card {
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-card :deep(.ant-card-head-title) {
    font-weight: 600;
    font-size: 16px;
}

.mt-4 {
    margin-top: 24px;
}

/* 表格样式优化 */
:deep(.ant-table-wrapper) {
    .ant-table {
        font-size: 14px;
        border-radius: 8px;
        overflow: hidden;
    }
    
    .ant-table-thead > tr > th {
        background: linear-gradient(135deg, #fafafa, #f0f0f0);
        font-weight: 600;
        padding: 16px 12px;
        border-bottom: 2px solid #e8e8e8;
        color: #1a1a1a;
        font-size: 14px;
    }
    
    .ant-table-tbody > tr {
        transition: all 0.2s ease;
    }
    
    .ant-table-tbody > tr:hover {
        background: #f8f9fa;
        transform: translateY(-1px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }
    
    .ant-table-tbody > tr > td {
        padding: 14px 12px;
        border-bottom: 1px solid #f0f0f0;
        vertical-align: middle;
    }
    
    .ant-table-scroll {
        overflow-x: auto;
    }
    
    .ant-pagination {
        margin: 24px 0 8px;
        text-align: center;
    }
}

/* 按钮样式优化 */
:deep(.ant-btn) {
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.2s ease;
}

:deep(.ant-btn-primary) {
    background: linear-gradient(135deg, #1890ff, #40a9ff);
    border: none;
    box-shadow: 0 2px 6px rgba(24, 144, 255, 0.3);
}

:deep(.ant-btn-primary:hover) {
    background: linear-gradient(135deg, #40a9ff, #1890ff);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
}

/* 表单样式优化 */
:deep(.ant-form-item-label > label) {
    font-weight: 500;
    color: #1a1a1a;
}

:deep(.ant-input, .ant-input-number, .ant-select-selector) {
    border-radius: 6px;
    transition: all 0.2s ease;
}

:deep(.ant-input:focus, .ant-input-number:focus, .ant-select-focused .ant-select-selector) {
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

/* 标签样式优化 */
:deep(.ant-tag) {
    border-radius: 12px;
    font-weight: 500;
    padding: 2px 8px;
}

/* 统计卡片样式优化 */
:deep(.ant-statistic-title) {
    font-size: 14px;
    font-weight: 500;
    color: #666;
    margin-bottom: 8px;
}

:deep(.ant-statistic-content) {
    font-size: 24px;
    font-weight: 600;
}

/* 模态框样式优化 */
:deep(.ant-modal-content) {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

:deep(.ant-modal-header) {
    background: linear-gradient(135deg, #fafafa, #f0f0f0);
    border-bottom: 1px solid #e8e8e8;
    padding: 20px 24px;
}

:deep(.ant-modal-title) {
    font-size: 18px;
    font-weight: 600;
    color: #1a1a1a;
}

/* 描述列表样式优化 */
:deep(.ant-descriptions) {
    .ant-descriptions-item-label {
        font-weight: 600;
        color: #1a1a1a;
        background: #fafafa;
    }
    
    .ant-descriptions-item-content {
        color: #4a4a4a;
    }
}

/* 响应式设计 */
@media (max-width: 1200px) {
    .user-feedback-management {
        max-width: 100%;
        padding: 20px;
    }
}

@media (max-width: 768px) {
    .user-feedback-management {
        padding: 16px;
        background: #fff;
    }

    .page-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
        padding: 16px 0;
    }
    
    .page-header h2 {
        font-size: 24px;
    }
    
    .stats-overview {
        margin-bottom: 16px;
    }
    
    .search-card, .table-card {
        border-radius: 8px;
        margin: 0 -4px 16px;
    }
    
    .stats-card:hover, .search-card:hover {
        transform: none;
    }
    
    .table-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }
    
    .chart-card {
        margin-bottom: 16px;
        border-radius: 8px;
    }
    
    :deep(.ant-table-wrapper) {
        .ant-table-thead > tr > th,
        .ant-table-tbody > tr > td {
            padding: 8px 6px;
            font-size: 12px;
        }
        
        .ant-table-tbody > tr:hover {
            transform: none;
        }
    }
    
    :deep(.ant-modal) {
        margin: 16px;
        max-width: calc(100vw - 32px);
    }
    
    :deep(.ant-statistic-content) {
        font-size: 20px;
    }
}

@media (max-width: 480px) {
    .page-header h2 {
        font-size: 20px;
    }
    
    :deep(.ant-form-item) {
        margin-bottom: 16px;
    }
    
    :deep(.ant-space-item) {
        margin-bottom: 8px;
    }
    
    :deep(.ant-statistic-content) {
        font-size: 18px;
    }
}
</style>
