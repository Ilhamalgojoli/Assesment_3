package com.ilhamalgojali0081.assesment_3.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilhamalgojali0081.assesment_3.model.Resep
import com.ilhamalgojali0081.assesment_3.model.ResepRequest
import com.ilhamalgojali0081.assesment_3.network.ResepApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.BufferedSink
import okio.source
import android.content.ContentResolver

sealed class ResepUiState {
    object Loading : ResepUiState()
    data class Success(val recipes: List<Resep>) : ResepUiState()
    data class Error(val message: String) : ResepUiState()
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val resepRequestAdapter = moshi.adapter(ResepRequest::class.java)

class ResepViewModel(private val apiService: ResepApiService) : ViewModel() {

    private val _uiState = MutableStateFlow<ResepUiState>(ResepUiState.Loading)
    val uiState: StateFlow<ResepUiState> = _uiState.asStateFlow()

    private val _selectedRecipe = MutableStateFlow<Resep?>(null)
    val selectedRecipe: StateFlow<Resep?> get() = _selectedRecipe.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private lateinit var applicationContext: Context

    fun initContext(context: Context) {
        applicationContext = context.applicationContext
    }

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = ResepUiState.Loading
            try {
                val result = apiService.getRecipes()
                _uiState.value = ResepUiState.Success(result)
            } catch (e: IOException) {
                _uiState.value = ResepUiState.Error("Kesalahan jaringan: " +
                        (e.message ?: "Tidak diketahui"))
            } catch (e: HttpException) {
                _uiState.value = ResepUiState.Error("Kesalahan server: " +
                        (e.message ?: "Tidak diketahui"))
            } catch (e: Exception) {
                _uiState.value = ResepUiState.Error("Kesalahan tak terduga: " +
                        (e.message ?: "Tidak diketahui"))
            }
        }
    }

    fun loadRecipeById(id: String) {
        viewModelScope.launch {
            _uiState.value = ResepUiState.Loading
            _selectedRecipe.value = null
            _actionMessage.value = null

            try {
                val recipe = apiService.getRecipeById(id)
                _selectedRecipe.value = recipe
                _uiState.value = ResepUiState.Success(emptyList())
            } catch (e: IOException) {
                _uiState.value = ResepUiState.Error("Kesalahan jaringan saat memuat resep: " +
                        (e.message ?: "Tidak diketahui"))
            } catch (e: HttpException) {
                _uiState.value = ResepUiState.Error("Kesalahan server saat memuat resep: " +
                        (e.message ?: "Tidak diketahui"))
            } catch (e: Exception) {
                _uiState.value = ResepUiState.Error("Kesalahan tak terduga saat memuat resep: " +
                        (e.message ?: "Tidak diketahui"))
            }
        }
    }

    fun addRecipe(
        title: String,
        description: String,
        ingredient: String,
        imageUri: Uri?,
        userName: String,
        userEmail: String
    ) {
        viewModelScope.launch {
            _actionMessage.value = "Menambahkan resep..."
            try {
                if (imageUri == null) {
                    _actionMessage.value = "Gambar resep tidak boleh kosong."
                    return@launch
                }

                val resepRequest = ResepRequest(
                    title = title,
                    description = description,
                    ingredients = ingredient,
                    image_url = "",
                    user_name = userName,
                    user_email = userEmail
                )

                val recipeDataJson = resepRequestAdapter.toJson(resepRequest)
                val recipeDataBody =
                    recipeDataJson.toRequestBody("application/json".toMediaTypeOrNull())

                val imagePart = uriToMultipartBodyPart(imageUri, "image", applicationContext)

                apiService.addRecipe(recipeDataBody, imagePart)
                _actionMessage.value = "Resep berhasil ditambahkan!"
                loadRecipes()
            } catch (e: IOException) {
                _actionMessage.value = "Terjadi kesalahan jaringan saat menambahkan resep: " +
                        (e.message ?: "Tidak diketahui")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _actionMessage.value = "Terjadi kesalahan server saat menambahkan resep " +
                        "(${e.code()}): ${errorBody ?: e.message ?: "Tidak diketahui"}"
            } catch (e: Exception) {
                _actionMessage.value = "Terjadi kesalahan tak terduga: " +
                        (e.message ?: "Tidak diketahui")
            }
        }
    }

    fun updateRecipe(
        id: String,
        title: String,
        description: String,
        ingredient: String,
        imageUri: Uri?,
        userName: String,
        userEmail: String
    ) {
        viewModelScope.launch {
            _actionMessage.value = "Memperbarui resep..."
            try {
                val resepRequest = ResepRequest(
                    title = title,
                    description = description,
                    ingredients = ingredient,
                    image_url = selectedRecipe.value?.image_url ?: "",
                    user_name = userName,
                    user_email = userEmail
                )

                val recipeDataJson = resepRequestAdapter.toJson(resepRequest)
                val recipeDataBody =
                    recipeDataJson.toRequestBody("application/json".toMediaTypeOrNull())

                val imagePart = if (imageUri != null && imageUri.toString().startsWith("content://")) {
                    uriToMultipartBodyPart(imageUri, "image", applicationContext)
                } else {
                    null
                }

                if (imagePart != null) {
                    apiService.updateRecipe(id, recipeDataBody, imagePart)
                } else {
                    val emptyImagePart = MultipartBody.Part.createFormData("image", "")
                    apiService.updateRecipe(id, recipeDataBody, emptyImagePart)
                }

                _actionMessage.value = "Resep berhasil diperbarui!"
                loadRecipes()
                loadRecipeById(id)
            } catch (e: IOException) {
                _actionMessage.value = "Terjadi kesalahan jaringan saat memperbarui resep: ${e.message ?: "Tidak diketahui"}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _actionMessage.value = "Kesalahan server saat memperbarui (${e.code()}): ${errorBody ?: e.message}"
            } catch (e: Exception) {
                _actionMessage.value = "Kesalahan tak terduga: ${e.message}"
            }
        }
    }

    fun deleteRecipe(id: String) {
        viewModelScope.launch {
            _actionMessage.value = "Menghapus resep..."
            try {
                apiService.deleteRecipe(id)
                _actionMessage.value = "Resep berhasil dihapus!"
                loadRecipes()
            } catch (e: IOException) {
                _actionMessage.value = "Terjadi kesalahan jaringan saat menghapus resep: " +
                        (e.message ?: "Tidak diketahui")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _actionMessage.value = "Terjadi kesalahan server saat menghapus resep " +
                        "(${e.code()}): ${errorBody ?: e.message ?: "Tidak diketahui"}"
            } catch (e: Exception) {
                _actionMessage.value = "Terjadi kesalahan tak terduga: " +
                        (e.message ?: "Tidak diketahui")
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}

fun uriToMultipartBodyPart(uri: Uri, partName: String, context: Context): MultipartBody.Part {
    val contentResolver = context.contentResolver
    val fileName = getFileName(uri, contentResolver) ?: "image.jpg"
    val mediaType = contentResolver.getType(uri)?.toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull()

    val requestBody = object : RequestBody() {
        override fun contentType() = mediaType

        override fun contentLength() = -1L

        override fun writeTo(sink: BufferedSink) {
            contentResolver.openInputStream(uri)?.source()?.use { source ->
                sink.writeAll(source)
            }
        }
    }
    return MultipartBody.Part.createFormData(partName, fileName, requestBody)
}

fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
    var name: String? = null
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                name = it.getString(nameIndex)
            }
        }
    }
    return name
}
