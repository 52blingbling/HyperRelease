package com.github.releasemonitor.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.releasemonitor.model.Project
import com.github.releasemonitor.ui.miuix.MiuixCard

@Composable
fun ReleaseDetailDialog(
    project: Project,
    onDismiss: () -> Unit,
    onMarkAsLatest: (Project) -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        MiuixCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = project.repo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("当前版本: ${project.currentVer.ifEmpty { "未设" }}")
                    Text(
                        text = "最新版本: ${project.latestVer.ifEmpty { "未获取" }}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text("发布时间: ${project.publishedAt.ifEmpty { "未知" }}  |  上次检测: ${project.lastChecked.ifEmpty { "未检测" }}")

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text("更新日志 (Changelog):", fontWeight = FontWeight.SemiBold)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = project.releaseBody.ifEmpty { "暂无更新日志" },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (project.releaseUrl.isNotBlank()) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(project.releaseUrl))
                                context.startActivity(intent)
                            }
                        ) {
                            Text("下载")
                        }
                    }

                    if (project.latestVer.isNotBlank()) {
                        Button(
                            onClick = {
                                onMarkAsLatest(project.copy(currentVer = project.latestVer, status = "已是最新"))
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("设为当前")
                        }
                    }

                    TextButton(onClick = onDismiss) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}
