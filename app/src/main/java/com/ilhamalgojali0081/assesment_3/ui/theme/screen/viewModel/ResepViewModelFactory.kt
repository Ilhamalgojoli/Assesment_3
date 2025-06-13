import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.ilhamalgojali0081.assesment_3.ui.theme.screen.AplicationResep
import com.ilhamalgojali0081.assesment_3.viewmodel.ResepViewModel

class ResepViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ResepViewModel::class.java)) {
            val application = extras[
                ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
            ] as AplicationResep
            val apiService = application.resepApi.service
            val viewModel = ResepViewModel(apiService)
            viewModel.initContext(application.applicationContext)
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
