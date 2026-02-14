package com.collection.kubera.di

import com.collection.kubera.data.repository.ReportFileHelper
import com.collection.kubera.data.repository.ReportFileHelperImpl
import com.collection.kubera.data.repository.ReportRepository
import com.collection.kubera.data.repository.ReportRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReportModule {

    @Binds
    @Singleton
    abstract fun bindReportFileHelper(impl: ReportFileHelperImpl): ReportFileHelper

    companion object {
        @Provides
        @Singleton
        fun provideReportRepository(): ReportRepository = ReportRepositoryImpl()
    }
}
