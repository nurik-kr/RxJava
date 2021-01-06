package kg.nurik.rxjava

import android.app.Application
import kg.nurik.rxjava.di.appModules
import org.koin.android.ext.android.startKoin

class SearchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, appModules)
    }
}