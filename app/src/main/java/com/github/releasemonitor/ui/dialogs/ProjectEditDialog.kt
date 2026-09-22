package com.github.releasemonitor.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.releasemonitor.model.Project
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.TextField

@Composable
fun ProjectEditDialog(
    project: Project?,
    onDismiss: () -> Unit,
    onSave: (title: String, repo: String, currentVer: String, notes: String) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    var title by remember { mutableStateOf(project?.title ?: "") }
    var repo by remember { mutableStateOf(project?.repo ?: "") }
    var currentVer by remember { mutableStateOf(project?.currentVer ?: "") }
    var notes by remember { mutableStateOf(project?.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (project == null) "录入新监控项目" else "编辑监控项目",
                    style = MaterialTheme.typography.titleLarge
                )

                TextField(
                    value = title,
                    onValueChange = { title = it; errorMessage = "" },
                    label = "项目标题 (*必填)",
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = repo,
                    onValueChange = { repo = it; errorMessage = "" },
                    label = "GitHub 仓库 (*owner/repo)",
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = currentVer,
                    onValueChange = { currentVer = it },
                    label = "当前版本 (选填)",
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "备注说明 (选填)",
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (project != null && onDelete != null) {
                        Button(
                            onClick = { onDelete(project.id); onDismiss() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text("删除", color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("取消", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "请输入项目标题"
                                return@Button
                            }
                            if (repo.isBlank()) {
                                errorMessage = "请输入 GitHub 仓库"
                                return@Button
                            }
                            onSave(title.trim(), repo.trim(), currentVer.trim(), notes.trim())
                            onDismiss()
                        }
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}
