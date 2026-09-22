package com.github.releasemonitor.data

import android.content.Context
import android.content.SharedPreferences
import com.github.releasemonitor.model.Project
import org.json.JSONArray

class StorageManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("release_monitor_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROJECTS = "projects_json"
        private const val KEY_AUTO_CHECK = "auto_check_on_launch"
        private const val KEY_TOKEN = "github_token"
    }

    var isAutoCheckOnLaunch: Boolean
        get() = prefs.getBoolean(KEY_AUTO_CHECK, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_CHECK, value).apply()

    var githubToken: String
        get() = prefs.getString(KEY_TOKEN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    fun getProjects(): List<Project> {
        val jsonStr = prefs.getString(KEY_PROJECTS, null) ?: return emptyList()
        val list = mutableListOf<Project>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                list.add(Project.fromJson(array.getJSONObject(i)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveProjects(projects: List<Project>) {
        val array = JSONArray()
        projects.forEach { array.put(it.toJson()) }
        prefs.edit().putString(KEY_PROJECTS, array.toString()).apply()
    }

    fun addOrUpdateProject(project: Project) {
        val list = getProjects().toMutableList()
        val index = list.indexOfFirst { it.id == project.id }
        if (index != -1) {
            list[index] = project
        } else {
            list.add(0, project)
        }
        saveProjects(list)
    }

    fun deleteProject(projectId: String) {
        val list = getProjects().filterNot { it.id == projectId }
        saveProjects(list)
    }
}
