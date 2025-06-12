package com.ilhamalgojali0081.assesment_3.ui.theme.screen.viewModel

import android.graphics.Bitmap
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResepViewModel: ViewModel() {
    var data = mutableStateOf(emptyList<Resep>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

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

    fun storeData(
        title: String, description: String
        ,ingridient: String, bitmap: Bitmap
        ,recipesWriter: String, userEmail: String
    ) {
        viewModelScope.launch {
            try {
                val result = ResepApi.service.storeRecipes(
                    title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                    description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    recipeWriter = recipesWriter.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody(),
                    userEmail = userEmail.toRequestBody("text/plain".toMediaTypeOrNull()),
                    createdAt = formatter.format(Date()).toRequestBody("text/plain".toMediaTypeOrNull()),
                    ingridient = ingridient.toRequestBody("text/plain".toMediaTypeOrNull()),
                )
                if (result.error == null)
                    retrieveData()
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part{
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size
        )

        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody
        )
    }

    fun clearMessage(){
        errorMessage.value = null
    }
}