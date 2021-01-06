package kg.nurik.rxjava.di

import kg.nurik.rxjava.data.remote.RetrofitBuilder
import kg.nurik.rxjava.ui.MainViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module() {
    viewModel { MainViewModel(get()) }
}

val repositoryModule = module() {
    single { RetrofitBuilder.buildRetrofit() }
}

val appModules = listOf(viewModelModule, repositoryModule)