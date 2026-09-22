package com.github.releasemonitor.network

import com.github.releasemonitor.model.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object GitHubReleaseChecker {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun normalizeRepo(input: String): String {
        var s = input.trim()
        s = s.replace(Regex("^https?://github\\.com/"), "")
        s = s.replace(Regex("\\.git$"), "")
        s = s.trim('/', ' ')
        val parts = s.split("/")
        return if (parts.size >= 2) "${parts[0]}/${parts[1]}" else s
    }

    private fun cleanVersionTag(tag: String): String {
        return tag.trim().replace(Regex("^(release[-_]|ver[-_]|v[\\.\\-_]?)", RegexOption.IGNORE_CASE), "")
    }

    /**
     * 比对语义化版本号
     * 返回: true 表示 latestVer > currentVer
     */
    private fun isNewerVersion(currentVer: String, latestVer: String): Boolean {
        val cur = cleanVersionTag(currentVer)
        val lat = cleanVersionTag(latestVer)
        if (cur.isEmpty()) return false

        try {
            val curParts = cur.split(".", "-", "_").mapNotNull { it.toIntOrNull() }
            val latParts = lat.split(".", "-", "_").mapNotNull { it.toIntOrNull() }
            val maxLen = maxOf(curParts.size, latParts.size)
            for (i in 0 until maxLen) {
                val c = curParts.getOrElse(i) { 0 }
                val l = latParts.getOrElse(i) { 0 }
                if (l > c) return true
                if (l < c) return false
            }
            return false
        } catch (e: Exception) {
            return cur != lat
        }
    }

    suspend fun checkRelease(project: Project, token: String = ""): Project = withContext(Dispatchers.IO) {
        val cleanRepo = normalizeRepo(project.repo)
        val nowStr = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date())

        if (!cleanRepo.contains("/") || cleanRepo.split("/").size != 2) {
            return@withContext project.copy(
                status = "格式错误",
                lastChecked = nowStr
            )
        }

        val url = "https://api.github.com/repos/$cleanRepo/releases/latest"
        val builder = Request.Builder()
            .url(url)
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "Release-Monitor-MIUIX-Android")

        if (token.isNotBlank()) {
            builder.header("Authorization", "Bearer ${token.trim()}")
        }

        try {
            val response = client.newCall(builder.build()).execute()
            val code = response.code
            val bodyString = response.body?.string() ?: ""

            when (code) {
                200 -> {
                    val json = JSONObject(bodyString)
                    val rawTag = json.optString("tag_name", "")
                    val htmlUrl = json.optString("html_url", "")
                    val body = json.optString("body", "暂无更新日志")
                    val publishedAtRaw = json.optString("published_at", "")

                    val hasUpdate = isNewerVersion(project.currentVer, rawTag)
                    val status = if (hasUpdate) "有新版本" else if (project.currentVer.isNotBlank()) "已是最新" else "已获取"

                    project.copy(
                        repo = cleanRepo,
                        status = status,
                        latestVer = rawTag,
                        releaseUrl = htmlUrl,
                        releaseBody = body,
                        publishedAt = publishedAtRaw.take(10),
                        lastChecked = nowStr
                    )
                }
                404 -> project.copy(status = "未发布Release", lastChecked = nowStr)
                403 -> project.copy(status = "API限流", lastChecked = nowStr)
                else -> project.copy(status = "HTTP $code", lastChecked = nowStr)
            }
        } catch (e: Exception) {
            project.copy(
                status = "网络错误",
                lastChecked = nowStr
            )
        }
    }
}
