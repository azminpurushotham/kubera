package com.collection.kubera.di

import com.collection.kubera.data.repository.ReportFileHelper
import com.collection.kubera.data.repository.ReportFileHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReportModule {

    @Binds
    @Singleton
    abstract fun bindReportFileHelper(impl: ReportFileHelperImpl): ReportFileHelper
    // ReportRepository provided by CloudDataModule / LocalDataModule per flavor
}
