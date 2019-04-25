package apps.ecooksey.rpeloadcalc

import android.app.Application

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @JvmStatic
        lateinit var instance: MainApplication
            private set
    }
}