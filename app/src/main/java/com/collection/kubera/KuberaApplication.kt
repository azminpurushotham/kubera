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
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltAndroidApp
class KuberaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)?.let {
            Log.d("FirebaseInit", "Firebase successfully initialized")
        }
        Timber.plant(Timber.DebugTree())
        // Run ProviderInstaller off main thread to avoid blocking (contacts Google Play Services)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            installProvider()
        }
    }

    private fun installProvider() {
        try {
            ProviderInstaller.installIfNeeded(applicationContext)
        } catch (e: GooglePlayServicesRepairableException) {
            Timber.w(e, "ProviderInstaller: Google Play Services needs update")
        } catch (e: GooglePlayServicesNotAvailableException) {
            Timber.w(e, "ProviderInstaller: Google Play Services not available")
        } catch (e: Exception) {
            Timber.w(e, "ProviderInstaller: unexpected error")
        }
    }
}
