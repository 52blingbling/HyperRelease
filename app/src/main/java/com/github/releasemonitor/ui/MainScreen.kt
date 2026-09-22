package com.github.releasemonitor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.releasemonitor.data.StorageManager
import com.github.releasemonitor.model.Project
import com.github.releasemonitor.network.GitHubReleaseChecker
import com.github.releasemonitor.ui.dialogs.ProjectEditDialog
import com.github.releasemonitor.ui.dialogs.ReleaseDetailDialog
import com.github.releasemonitor.ui.dialogs.SettingsDialog
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(storage: StorageManager) {
    val coroutineScope = rememberCoroutineScope()
    var projects by remember { mutableStateOf(storage.getProjects()) }
    var autoCheckOnLaunch by remember { mutableStateOf(storage.isAutoCheckOnLaunch) }
    var isCheckingAll by remember { mutableStateOf(false) }

    // 对话框状态
    var showAddDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<Project?>(null) }
    var viewingReleaseProject by remember { mutableStateOf<Project?>(null) }

    // 启动时自动检测逻辑
    LaunchedEffect(Unit) {
        if (autoCheckOnLaunch && projects.isNotEmpty()) {
            isCheckingAll = true
            val updated = projects.map { p ->
                GitHubReleaseChecker.checkRelease(p, storage.githubToken)
            }
            projects = updated
            storage.saveProjects(updated)
            isCheckingAll = false
        }
    }

    // 单项手动检测
    fun checkSingleProject(project: Project) {
        coroutineScope.launch {
            val index = projects.indexOfFirst { it.id == project.id }
            if (index != -1) {
                val updatedList = projects.toMutableList()
                updatedList[index] = project.copy(status = "检测中...")
                projects = updatedList

                val checked = GitHubReleaseChecker.checkRelease(project, storage.githubToken)
                val newList = projects.toMutableList()
                val targetIdx = newList.indexOfFirst { it.id == project.id }
                if (targetIdx != -1) {
                    newList[targetIdx] = checked
                    projects = newList
                    storage.saveProjects(newList)
                }
            }
        }
    }

    // 全量手动检测
    fun checkAllProjects() {
        if (isCheckingAll) return
        coroutineScope.launch {
            isCheckingAll = true
            val updated = projects.map { p ->
                GitHubReleaseChecker.checkRelease(p, storage.githubToken)
            }
            projects = updated
            storage.saveProjects(updated)
            isCheckingAll = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Release 监控",
                actions = {
                    IconButton(
                        onClick = { checkAllProjects() },
                        enabled = !isCheckingAll && projects.isNotEmpty()
                    ) {
                        if (isCheckingAll) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "手动全量检测")
                        }
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "录入项目")
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. 设置分组卡片 (MIUI 胶囊 Switch)
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = "打开软件自动检测",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "启动应用时自动检查已录入项目的最新版本",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoCheckOnLaunch,
                            onCheckedChange = {
                                autoCheckOnLaunch = it
                                storage.isAutoCheckOnLaunch = it
                            }
                        )
                    }
                }
            }

            // 统计栏
            item {
                val updateCount = projects.count { it.status == "有新版本" }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "已录入项目 (${projects.size})",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (updateCount > 0) {
                        Text(
                            text = "待更新: $updateCount",
                            color = Color(0xFFFF6900),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // 2. 空状态提示
            if (projects.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp)) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📦", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("暂未录入监控项目", fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "点击右上角 ＋ 按钮录入您的首个 GitHub 仓库",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. 项目卡片列表
            items(projects, key = { it.id }) { p ->
                val hasUpdate = p.status == "有新版本"
                val statusColor = when (p.status) {
                    "有新版本" -> Color(0xFFFF6900) // 小米橙
                    "已是最新" -> Color(0xFF00C48C) // 成功绿
                    "检测中..." -> Color(0xFF0070F0) // 科技蓝
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewingReleaseProject = p }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 标题与状态徽标
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = p.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = statusColor.copy(alpha = 0.12f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = p.status,
                                    color = statusColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // 仓库
                        Text(
                            text = p.repo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 版本对比
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("本地: ${p.currentVer.ifEmpty { "-" }}", fontSize = 12.sp)
                                Text("➔", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "最新: ${p.latestVer.ifEmpty { "-" }}",
                                    fontSize = 12.sp,
                                    fontWeight = if (hasUpdate) FontWeight.Bold else FontWeight.Normal,
                                    color = if (hasUpdate) Color(0xFFFF6900) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // 备注
                        if (p.notes.isNotBlank()) {
                            Text(
                                text = "备注: ${p.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }

                        // 底部操作栏
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (p.lastChecked.isNotBlank()) "上次: ${p.lastChecked}" else "未检测",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { checkSingleProject(p) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text("刷新", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { editingProject = p },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text("编辑", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    // 录入新项目弹窗
    if (showAddDialog) {
        ProjectEditDialog(
            project = null,
            onDismiss = { showAddDialog = false },
            onSave = { title, repo, currentVer, notes ->
                val newProject = Project(
                    title = title,
                    repo = GitHubReleaseChecker.normalizeRepo(repo),
                    currentVer = currentVer,
                    notes = notes
                )
                storage.addOrUpdateProject(newProject)
                projects = storage.getProjects()
                checkSingleProject(newProject)
            }
        )
    }

    // 设置弹窗
    if (showSettingsDialog) {
        SettingsDialog(
            initialToken = storage.githubToken,
            onDismiss = { showSettingsDialog = false },
            onSave = { newToken ->
                storage.githubToken = newToken
            }
        )
    }

    // 编辑已有项目弹窗
    editingProject?.let { p ->
        ProjectEditDialog(
            project = p,
            onDismiss = { editingProject = null },
            onSave = { title, repo, currentVer, notes ->
                val updated = p.copy(
                    title = title,
                    repo = GitHubReleaseChecker.normalizeRepo(repo),
                    currentVer = currentVer,
                    notes = notes
                )
                storage.addOrUpdateProject(updated)
                projects = storage.getProjects()
                editingProject = null
            },
            onDelete = { id ->
                storage.deleteProject(id)
                projects = storage.getProjects()
                editingProject = null
            }
        )
    }

    // 查看 Release 详情弹窗
    viewingReleaseProject?.let { p ->
        ReleaseDetailDialog(
            project = p,
            onDismiss = { viewingReleaseProject = null },
            onMarkAsLatest = { updated ->
                storage.addOrUpdateProject(updated)
                projects = storage.getProjects()
                viewingReleaseProject = null
            }
        )
    }
}
