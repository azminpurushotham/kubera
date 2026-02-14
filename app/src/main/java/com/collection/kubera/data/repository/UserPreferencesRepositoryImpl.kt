package com.collection.kubera.data.repository

import android.content.Context
import com.collection.kubera.utils.PreferenceHelper
import com.collection.kubera.utils.USER_ID
import com.collection.kubera.utils.USER_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val prefs by lazy { PreferenceHelper.getPrefs(context) }

    override fun getUserId(): String = prefs.getString(USER_ID, "") ?: ""
    override fun getUserName(): String = prefs.getString(USER_NAME, "") ?: ""
}
