package com.ilhamalgojali0081.assesment_3.ui.theme.screen.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilhamalgojali0081.assesment_3.model.Resep
import com.ilhamalgojali0081.assesment_3.network.ApiStatus
import com.ilhamalgojali0081.assesment_3.network.ResepApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ResepViewModel: ViewModel() {
    var data = mutableStateOf(emptyList<Resep>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(){
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING

            try {
                data.value = ResepApi.service.getRecipes()
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("ResepViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }
}