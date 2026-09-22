package com.github.releasemonitor.model

import org.json.JSONObject

data class Project(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val repo: String,
    val currentVer: String = "",
    val latestVer: String = "",
    val notes: String = "",
    val status: String = "未检测",
    val lastChecked: String = "",
    val releaseUrl: String = "",
    val releaseBody: String = "",
    val publishedAt: String = ""
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("title", title)
            put("repo", repo)
            put("currentVer", currentVer)
            put("latestVer", latestVer)
            put("notes", notes)
            put("status", status)
            put("lastChecked", lastChecked)
            put("releaseUrl", releaseUrl)
            put("releaseBody", releaseBody)
            put("publishedAt", publishedAt)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): Project {
            return Project(
                id = json.optString("id", System.currentTimeMillis().toString()),
                title = json.optString("title", ""),
                repo = json.optString("repo", ""),
                currentVer = json.optString("currentVer", ""),
                latestVer = json.optString("latestVer", ""),
                notes = json.optString("notes", ""),
                status = json.optString("status", "未检测"),
                lastChecked = json.optString("lastChecked", ""),
                releaseUrl = json.optString("releaseUrl", ""),
                releaseBody = json.optString("releaseBody", ""),
                publishedAt = json.optString("publishedAt", "")
            )
        }
    }
}
