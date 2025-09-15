<template>
    <div class="user-feedback-management">
        <div class="page-header">
            <h2>用户反馈管理</h2>
            <a-button type="primary" @click="showCreateModal">
                <template #icon>
                    <PlusOutlined />
                </template>
                新增反馈
            </a-button>
        </div>

        <!-- 搜索栏 -->
        <a-card class="search-card" :bordered="false">
            <a-form layout="inline" :model="searchForm" @finish="handleSearch">
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

        <!-- 数据表格 -->
        <a-card :bordered="false">
            <a-spin :spinning="loading" tip="加载中...">
                <template #indicator>
                    <LoadingSpinner />
                </template>
                <a-table :dataSource="dataSource" :columns="columns" :loading="false" :pagination="pagination"
                    @change="handleTableChange" row-key="id">
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
                </template>
            </a-table>
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
                <a-descriptions-item label="创建时间">{{ viewData.createTime }}</a-descriptions-item>
                <a-descriptions-item label="更新时间">{{ viewData.updateTime }}</a-descriptions-item>
                <a-descriptions-item label="状态">
                    <a-tag :color="viewData.isDelete === 0 ? 'green' : 'red'">
                        {{ viewData.isDelete === 0 ? '正常' : '已删除' }}
                    </a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="编辑时间">{{ viewData.editTime }}</a-descriptions-item>
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
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, SearchOutlined } from '@ant-design/icons-vue'
import api from '@/api'
import type { TableColumnsType } from 'ant-design-vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dataSource = ref<API.FeedbackMessage[]>([])
const modalVisible = ref(false)
const viewModalVisible = ref(false)
const modalTitle = ref('新增用户反馈')
const editingId = ref<number | null>(null)

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

// 加载数据
const loadData = async () => {
    loading.value = true
    try {
        // 检查是否有搜索参数
        const hasSearchParams = searchForm.chatId || 
                               searchForm.messageType !== undefined || 
                               searchForm.userId !== undefined

        let response: any

        if (hasSearchParams) {
            // 有参数时使用新接口 listUserVoByPage (POST方法)
            const params: API.FeedbackQueryRequest = {
                pageNum: pagination.current,
                pageSize: pagination.pageSize,
                chatId: searchForm.chatId || undefined,
                messageType: searchForm.messageType,
                userId: searchForm.userId
            }

            console.log('用户反馈-使用新接口-发送的参数:', params)
            response = await api.yonghufankuiguanli.listUserVoByPage(params)
            console.log('用户反馈-新接口-API完整响应:', response)

            if (response && response.data && response.data.data) {
                console.log('用户反馈-新接口-响应数据:', response.data.data)
                dataSource.value = response.data.data.records || []
                pagination.total = response.data.data.totalRow || 0
            } else {
                console.warn('用户反馈-新接口-响应数据格式异常:', response)
                message.warning('响应数据格式异常')
            }
        } else {
            // 无参数时使用老接口 list1 (GET方法，获取所有数据)
            console.log('用户反馈-使用老接口-无参数查询')
            response = await api.yonghufankuiguanli.list1()
            console.log('用户反馈-老接口-API完整响应:', response)

            if (response && response.data) {
                console.log('用户反馈-老接口-响应数据:', response.data)
                const allData = response.data || []
                
                // 手动实现分页逻辑
                const startIndex = (pagination.current - 1) * pagination.pageSize
                const endIndex = startIndex + pagination.pageSize
                dataSource.value = allData.slice(startIndex, endIndex)
                pagination.total = allData.length
            } else {
                console.warn('用户反馈-老接口-响应数据格式异常:', response)
                message.warning('响应数据格式异常')
            }
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
})
</script>

<style scoped>
.user-feedback-management {
    padding: 24px;
    max-width: 1600px;
    margin: 0 auto;
    width: 100%;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.page-header h2 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
}

.search-card {
    margin-bottom: 16px;
}

/* 表格样式优化 */
:deep(.ant-table-wrapper) {
    .ant-table {
        font-size: 14px;
    }
    
    .ant-table-thead > tr > th {
        background: #fafafa;
        font-weight: 600;
        padding: 12px 8px;
    }
    
    .ant-table-tbody > tr > td {
        padding: 12px 8px;
    }
    
    .ant-table-scroll {
        overflow-x: auto;
    }
}

/* 响应式设计 */
@media (max-width: 768px) {
    .user-feedback-management {
        padding: 16px;
        max-width: 100%;
    }

    .page-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
    }
    
    :deep(.ant-table-wrapper) {
        .ant-table-thead > tr > th,
        .ant-table-tbody > tr > td {
            padding: 8px 4px;
            font-size: 12px;
        }
    }
}
</style>
