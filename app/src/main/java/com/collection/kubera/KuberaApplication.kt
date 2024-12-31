/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.collection.kubera

import android.app.Application
import android.util.Log
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.FirebaseApp
import timber.log.Timber


class KuberaApplication : Application() {
    companion object {
        const val JETNEWS_APP_URI = "https://developer.android.com/jetnews"
    }

    override fun onCreate() {
        super.onCreate()
        println("KuberaApplication")

        FirebaseApp.initializeApp(this)?.let {
            Log.d("FirebaseInit", "Firebase successfully initialized")
        }
        installProvider()
        Timber.plant(Timber.DebugTree())
    }


    fun installProvider() {
        try {
            ProviderInstaller.installIfNeeded(applicationContext)
        } catch (e: GooglePlayServicesRepairableException) {
            // Prompt user to update Google Play Services
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Handle case where Google Play Services is not available
        } catch (e: Exception) {
            // Log unexpected errors
            e.printStackTrace()
        }
    }
}
