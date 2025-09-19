<template>
    <div class="image-analysis-management modern-gradient-bg image-analysis-theme">
        <div class="page-header colorful-page-header">
            <h2>图片解析管理</h2>
            <a-button type="primary" class="colorful-btn-primary" @click="showCreateModal">
                <template #icon>
                    <PlusOutlined />
                </template>
                新增解析记录
            </a-button>
        </div>

        <!-- 搜索栏 -->
        <a-card class="search-card colorful-search-card fade-in-up" :bordered="false">
            <a-form layout="inline" :model="searchForm" @finish="handleSearch" class="colorful-form">
                <a-form-item label="聊天ID">
                    <a-input v-model:value="searchForm.chatId" placeholder="请输入聊天ID" allow-clear />
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
        <a-card :bordered="false" class="table-card colorful-card scale-in colorful-table">
            <template #title>
                <div class="table-header">
                    <div class="table-title">
                        <h3>图片解析列表</h3>
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
                    <LoadingSpinner text="正在加载图片解析..." type="wave" theme="elegant" />
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
                    <template v-else-if="column.key === 'imageTxt'">
                        <a-tooltip>
                            <template #title>
                                <JsonViewer :content="record.imageTxt" :show-toolbar="false" max-height="200px" />
                            </template>
                            <span>{{ getJSONSummary(record.imageTxt, 50) }}</span>
                        </a-tooltip>
                    </template>
                    <template v-else-if="column.key === 'imagePath'">
                        <a-image v-if="record.imagePath" :width="60" :height="60"
                            :src="getImageDisplayUrl(record.imagePath)"
                            style="border-radius: 4px; object-fit: cover;" />
                        <span v-else>-</span>
                    </template>
                    <template v-else-if="column.key === 'tokenQuantity'">
                        <a-tag color="blue">{{ record.tokenQuantity }}</a-tag>
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
                    <a-col :span="12">
                        <a-form-item label="用户ID" name="userId">
                            <a-input-number v-model:value="formData.userId" placeholder="请输入用户ID" style="width: 100%" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="聊天ID" name="chatId">
                            <a-input v-model:value="formData.chatId" placeholder="请输入聊天ID" />
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row :gutter="16">
                    <a-col :span="12">
                        <a-form-item label="Token数量" name="tokenQuantity">
                            <a-input-number v-model:value="formData.tokenQuantity" placeholder="请输入Token数量"
                                style="width: 100%" :min="0" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="状态" name="isDelete">
                            <a-select v-model:value="formData.isDelete" placeholder="请选择状态">
                                <a-select-option :value="0">正常</a-select-option>
                                <a-select-option :value="1">已删除</a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-form-item label="图片路径" name="imagePath">
                    <a-input v-model:value="formData.imagePath" placeholder="请输入图片路径或URL" />
                    <template #suffix>
                        <a-tooltip title="路径将自动添加前缀: http://localhost:8123/api/imageAnalysis/display">
                            <span style="color: #999; font-size: 12px;">?</span>
                        </a-tooltip>
                    </template>
                </a-form-item>
                <a-form-item label="图片解析文本" name="imageTxt">
                    <a-textarea v-model:value="formData.imageTxt" placeholder="请输入图片解析文本" :rows="6" />
                </a-form-item>
            </a-form>
        </a-modal>

        <!-- 查看详情弹窗 -->
        <a-modal v-model:open="viewModalVisible" title="查看图片解析详情" :width="900" :footer="null">
            <a-descriptions :column="2" bordered>
                <a-descriptions-item label="ID">{{ viewData.id }}</a-descriptions-item>
                <a-descriptions-item label="用户ID">{{ viewData.userId }}</a-descriptions-item>
                <a-descriptions-item label="聊天ID">{{ viewData.chatId }}</a-descriptions-item>
                <a-descriptions-item label="Token数量">
                    <a-tag color="blue">{{ viewData.tokenQuantity }}</a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="创建时间">{{ formatTime(viewData.createTime) }}</a-descriptions-item>
                <a-descriptions-item label="更新时间">{{ formatTime(viewData.updateTime) }}</a-descriptions-item>
                <a-descriptions-item label="状态">
                    <a-tag :color="viewData.isDelete === 0 ? 'green' : 'red'">
                        {{ viewData.isDelete === 0 ? '正常' : '已删除' }}
                    </a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="编辑时间">{{ formatTime(viewData.editTime) }}</a-descriptions-item>
                <a-descriptions-item label="图片预览" :span="2">
                    <a-image v-if="viewData.imagePath" :width="200" :src="getImageDisplayUrl(viewData.imagePath)"
                        style="border-radius: 8px;" />
                    <span v-else>无图片</span>
                </a-descriptions-item>
                <a-descriptions-item label="图片路径" :span="2">
                    <a-typography-text copyable>{{ getImageDisplayUrl(viewData.imagePath) || '-' }}</a-typography-text>
                </a-descriptions-item>
                <a-descriptions-item label="解析文本" :span="2">
                    <JsonViewer :content="viewData.imageTxt" max-height="400px" />
                </a-descriptions-item>
            </a-descriptions>
        </a-modal>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import api from '@/api'
import type { TableColumnsType } from 'ant-design-vue'
import { getImageDisplayUrl, extractImagePath } from '@/utils/image'
import JsonViewer from '@/components/JsonViewer.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { getJSONSummary } from '@/utils/json'
import { formatTime } from '@/utils/time'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dataSource = ref<API.ImageAnalysis[]>([])
const modalVisible = ref(false)
const viewModalVisible = ref(false)
const modalTitle = ref('新增图片解析')
const editingId = ref<number | null>(null)

// 搜索表单
const searchForm = reactive({
    chatId: '',
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
const formData = reactive<API.ImageAnalysis>({
    userId: undefined,
    chatId: '',
    imageTxt: '',
    imagePath: '',
    tokenQuantity: undefined,
    isDelete: 0
})

// 查看详情数据
const viewData = reactive<API.ImageAnalysis>({})

// 表单引用
const formRef = ref()

// 表单验证规则
const formRules = {
    userId: [{ required: true, message: '请输入用户ID', type: 'number' }],
    chatId: [{ required: true, message: '请输入聊天ID' }]
}

// 表格列配置
const columns: TableColumnsType = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '用户ID', dataIndex: 'userId', key: 'userId', width: 100 },
    { title: '聊天ID', dataIndex: 'chatId', key: 'chatId', width: 150 },
    { title: '图片预览', dataIndex: 'imagePath', key: 'imagePath', width: 100 },
    { title: 'Token数量', dataIndex: 'tokenQuantity', key: 'tokenQuantity', width: 100 },
    { title: '解析文本', dataIndex: 'imageTxt', key: 'imageTxt', width: 200 },
    { title: '状态', dataIndex: 'isDelete', key: 'isDelete', width: 80 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 160 },
    { title: '操作', key: 'action', width: 150, fixed: 'right' }
]

// 加载数据
const loadData = async () => {
    loading.value = true
    try {
        // 检查是否有搜索参数
        const hasSearchParams = searchForm.chatId || searchForm.userId !== undefined

        let response: any

        if (hasSearchParams) {
            // 有参数时使用 listImageAnalysisByPage 接口 (POST /imageAnalysis/list/page/vo)
            const params: API.ImageAnalysisQueryRequest = {
                pageNum: pagination.current,
                pageSize: pagination.pageSize,
                chatId: searchForm.chatId || undefined,
                userId: searchForm.userId
            }

            console.log('图片解析-有参数查询-发送的参数:', params)
            response = await api.tupianjiexiguanli.listImageAnalysisByPage(params)
            console.log('图片解析-有参数查询-API完整响应:', response)

            if (response && response.data && response.data.data) {
                console.log('图片解析-有参数查询-响应数据:', response.data.data)
                dataSource.value = response.data.data.records || []
                pagination.total = response.data.data.totalRow || 0
            } else {
                console.warn('图片解析-有参数查询-响应数据格式异常:', response)
                message.warning('响应数据格式异常')
            }
        } else {
            // 无参数时使用 page 接口 (GET /imageAnalysis/page)
            const params = {
                page: {
                    pageNumber: pagination.current,
                    pageSize: pagination.pageSize
                }
            }

            console.log('图片解析-无参数查询-发送的参数:', params)
            response = await api.tupianjiexiguanli.page(params)
            console.log('图片解析-无参数查询-API完整响应:', response)

            if (response && response.data) {
                console.log('图片解析-无参数查询-响应数据:', response.data)
                dataSource.value = response.data.records || []
                pagination.total = response.data.totalRow || 0
            } else {
                console.warn('图片解析-无参数查询-响应数据格式异常:', response)
                message.warning('响应数据格式异常')
            }
        }
    } catch (error) {
        console.error('图片解析-加载数据失败:', error)
        if (error.response) {
            console.error('图片解析-错误响应:', error.response)
            message.error(`服务器错误: ${error.response.status} - ${error.response.statusText}`)
        } else if (error.request) {
            console.error('图片解析-网络错误:', error.request)
            message.error('网络连接失败，请检查网络或后端服务')
        } else {
            console.error('图片解析-其他错误:', error.message)
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
    modalTitle.value = '新增图片解析'
    editingId.value = null
    resetFormData()
    modalVisible.value = true
}

// 编辑记录
const editRecord = async (record: API.ImageAnalysis) => {
    modalTitle.value = '编辑图片解析'
    editingId.value = record.id!

    try {
        const response = await api.tupianjiexiguanli.getInfo({ id: record.id! })
        if (response.data) {
            const data = { ...response.data }
            // 在表单中显示原始路径（不带前缀），方便编辑
            if (data.imagePath) {
                data.imagePath = extractImagePath(data.imagePath)
            }
            Object.assign(formData, data)
        }
        modalVisible.value = true
    } catch (error) {
        console.error('获取详情失败:', error)
        message.error('获取详情失败')
    }
}

// 查看记录
const viewRecord = async (record: API.ImageAnalysis) => {
    try {
        const response = await api.tupianjiexiguanli.getInfo({ id: record.id! })
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
        await api.tupianjiexiguanli.remove({ id })
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

        // 准备提交数据，确保图片路径是原始路径（不带前缀）
        const submitData = { ...formData }
        if (submitData.imagePath) {
            // 如果用户输入了完整URL，提取原始路径
            submitData.imagePath = extractImagePath(submitData.imagePath)
        }

        if (editingId.value) {
            await api.tupianjiexiguanli.update({ ...submitData, id: editingId.value })
            message.success('更新成功')
        } else {
            await api.tupianjiexiguanli.save(submitData)
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
        imageTxt: '',
        imagePath: '',
        tokenQuantity: undefined,
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
.image-analysis-management {
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

/* 图片预览样式优化 */
:deep(.ant-image) {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
}

:deep(.ant-image:hover) {
    transform: scale(1.05);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
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

:deep(.ant-input, .ant-input-number, .ant-select-selector, .ant-input-affix-wrapper) {
    border-radius: 6px;
    transition: all 0.2s ease;
}

:deep(.ant-input:focus, .ant-input-number:focus, .ant-select-focused .ant-select-selector, .ant-input-affix-wrapper:focus) {
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

/* 标签样式优化 */
:deep(.ant-tag) {
    border-radius: 12px;
    font-weight: 500;
    padding: 2px 8px;
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
    .image-analysis-management {
        max-width: 100%;
        padding: 20px;
    }
}

@media (max-width: 768px) {
    .image-analysis-management {
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
    
    .search-card, .table-card {
        border-radius: 8px;
        margin: 0 -4px 16px;
    }
    
    .search-card:hover {
        transform: none;
    }
    
    .table-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
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
    
    :deep(.ant-image:hover) {
        transform: none;
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
}
</style>
