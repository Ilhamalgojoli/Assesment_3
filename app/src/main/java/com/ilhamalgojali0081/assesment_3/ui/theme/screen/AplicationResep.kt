package com.ilhamalgojali0081.assesment_3.ui.theme.screen

import android.app.Application
import com.ilhamalgojali0081.assesment_3.network.ResepApi

class AplicationResep : Application() {
    lateinit var resepApi: ResepApi
        private set

    override fun onCreate() {
        super.onCreate()
        resepApi = ResepApi(this)
    }
}