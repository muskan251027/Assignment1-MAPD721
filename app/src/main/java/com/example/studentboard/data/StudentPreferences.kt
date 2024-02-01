package com.example.studentboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudentPreferences private constructor(private val context: Context)  {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("studentInfo")
    companion object {
        @Volatile
        private var instance: StudentPreferences? = null

        fun getInstance(context: Context): StudentPreferences {
            return instance ?: synchronized(this) {
                instance ?: StudentPreferences(context).also {
                    instance = it
                }
            }
        }

        private object Keys {
            val USERNAME = stringPreferencesKey("username")
            val EMAIL = stringPreferencesKey("email")
            val ID = stringPreferencesKey("id")
        }
    }

    // To get the student information
    val studentInfo: Flow<StudentInfo> = context.dataStore.data.map { preferences ->
        StudentInfo(
            username = preferences[Keys.USERNAME] ?: "",
            email = preferences[Keys.EMAIL] ?: "",
            id = preferences[Keys.ID]?.toIntOrNull() ?: 676
        )
    }

    // to save student information
    suspend fun saveStudentInfo(username: String, email: String, id: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USERNAME] = username
            preferences[Keys.EMAIL] = email
            preferences[Keys.ID] = id.toString()
        }
    }

    // to clear student information
    suspend fun clearStudentInfo() {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.USERNAME)
            preferences.remove(Keys.EMAIL)
            preferences.remove(Keys.ID)
        }
    }
}
