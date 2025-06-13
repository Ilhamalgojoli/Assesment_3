package com.ilhamalgojali0081.assesment_3.ui.theme.screen

import ResepViewModelFactory
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.ilhamalgojali0081.assesment_3.BuildConfig
import com.ilhamalgojali0081.assesment_3.R
import com.ilhamalgojali0081.assesment_3.model.User
import com.ilhamalgojali0081.assesment_3.network.UserDataStore
import com.ilhamalgojali0081.assesment_3.ui.theme.Assesment_3Theme
import com.ilhamalgojali0081.assesment_3.ui.theme.screen.component.ListResep
import com.ilhamalgojali0081.assesment_3.ui.theme.screen.component.ProfileDialog
import com.ilhamalgojali0081.assesment_3.ui.theme.screen.component.ResepDialog
import com.ilhamalgojali0081.assesment_3.viewmodel.ResepUiState
import com.ilhamalgojali0081.assesment_3.viewmodel.ResepViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: ResepViewModel = viewModel(factory = ResepViewModelFactory())
    LaunchedEffect(context) {
        viewModel.initContext(context)
    }

    var showResepDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    var selectedImageUri: Uri? by remember { mutableStateOf(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
        if (selectedImageUri != null) {
            showResepDialog = true
        } else {
            Toast.makeText(context, "Tidak ada gambar terpilih. Silakan " +
                    "coba lagi.", Toast.LENGTH_SHORT).show()
        }
    }

    val actionMessage by viewModel.actionMessage.collectAsState()
    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.loadRecipes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(
                        onClick = {
                            if (user.email.isEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    signIn(context, dataStore)
                                }
                            } else {
                                showProfileDialog = true
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(R.string.profile),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedImageUri = null
                    imagePickerLauncher.launch("image/*")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.tambah_resep)
                )
            }
        }
    ) { innerPadding ->
        ScreenContent(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding),
            onRecipeClick = { recipeId ->
                navController.navigate("recipeDetail/${recipeId}")
            }
        )

        if (showResepDialog) {
            val currentUri = selectedImageUri
            if (currentUri != null) {
                ResepDialog(
                    initialImageUri = currentUri,
                    initialRecipe = null,
                    onDismissRequest = {
                        showResepDialog = false
                        selectedImageUri = null
                    }
                ) { recipeIdFromDialog, title, description, ingredient, finalUri ->
                    viewModel.addRecipe(
                        title = title,
                        description = description,
                        ingredient = ingredient,
                        imageUri = finalUri,
                        userName = user.username,
                        userEmail = user.email
                    )
                    showResepDialog = false
                    selectedImageUri = null
                }
            } else {
                Toast.makeText(context, "Terjadi masalah dengan gambar. " +
                        "Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                showResepDialog = false
            }
        }

        if (showProfileDialog) {
            ProfileDialog(
                user = user,
                onDismissRequest = { showProfileDialog = false },
                onCofirmation = {
                    CoroutineScope(Dispatchers.IO).launch {
                        signOut(context, dataStore)
                    }
                    showProfileDialog = false
                }
            )
        }
    }
}

@Composable
fun ScreenContent(
    viewModel: ResepViewModel,
    modifier: Modifier = Modifier,
    onRecipeClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is ResepUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ResepUiState.Success -> {
            val recipes = (uiState as ResepUiState.Success).recipes
            if (recipes.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.no_recipes_found))
                }
            } else {
                LazyVerticalGrid(
                    modifier = modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(recipes) { resep ->
                        ListResep(
                            resep = resep,
                            modifier = Modifier.clickable { onRecipeClick(resep.id) }
                        )
                    }
                }
            }
        }
        is ResepUiState.Error -> {
            val errorMessage = (uiState as ResepUiState.Error).message
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Error: $errorMessage")
                Button(
                    onClick = { viewModel.loadRecipes() },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }
        }
    }
}

private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
        Toast.makeText(context, "Sign-in error: ${e.errorMessage}", Toast.LENGTH_LONG).show()
    }
}

private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
) {
    val credential = result.credential

    if (credential is CustomCredential
        && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val username = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(username, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException){
            Log.e("SIGN-IN", "Error parsing Google ID token: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
        Toast.makeText(context, "Signed out successfully!", Toast.LENGTH_SHORT).show()
    } catch (e: ClearCredentialException){
        Log.e("SIGN-IN", "Error signing out: ${e.message}")
        Toast.makeText(context, "Sign-out error: ${e.errorMessage}", Toast.LENGTH_LONG).show()
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Assesment_3Theme {
        MainScreen(navController = NavController(LocalContext.current))
    }
}
