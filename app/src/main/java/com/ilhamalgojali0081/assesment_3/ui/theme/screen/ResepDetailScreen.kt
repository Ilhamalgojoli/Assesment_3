package com.ilhamalgojali0081.assesment_3.ui.theme.screen

import ResepViewModelFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ilhamalgojali0081.assesment_3.R
import com.ilhamalgojali0081.assesment_3.model.User
import com.ilhamalgojali0081.assesment_3.network.UserDataStore
import com.ilhamalgojali0081.assesment_3.ui.theme.screen.component.ResepDialog
import com.ilhamalgojali0081.assesment_3.viewmodel.ResepUiState
import com.ilhamalgojali0081.assesment_3.viewmodel.ResepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResepDetailScreen(
    navController: NavController,
    recipeId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: ResepViewModel = viewModel(factory = ResepViewModelFactory())
    val selectedRecipe by viewModel.selectedRecipe.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var currentEditImageUri: Uri? by remember { mutableStateOf(null) }


    LaunchedEffect(recipeId) {
        viewModel.loadRecipeById(recipeId)
    }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            if (it == context.getString(R.string.recipe_deleted_success) ||
                it == context.getString(R.string.recipe_updated_success)) {
                navController.popBackStack()
            }
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.recipe_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_desc)
                        )
                    }
                },
                actions = {
                    selectedRecipe?.let { recipe ->
                        if (user.email == recipe.user_email) {
                            IconButton(onClick = {
                                showEditDialog = true
                                currentEditImageUri = try {
                                    Uri.parse(recipe.image_url)
                                } catch (e: Exception) {
                                    null
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit_recipe_button)
                                )
                            }
                            IconButton(onClick = { showDeleteConfirmationDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_recipe_button)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val uiState = viewModel.uiState.collectAsState().value) {
            is ResepUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.loading_recipe), modifier = Modifier.padding(top = 80.dp))
                }
            }
            is ResepUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(stringResource(R.string.error_loading_recipe, uiState.message))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadRecipeById(recipeId) }) {
                            Text(stringResource(R.string.try_again))
                        }
                    }
                }
            }
            is ResepUiState.Success -> {
                selectedRecipe?.let { recipe ->
                    Column(
                        modifier = modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(recipe.image_url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(R.string.gambar, recipe.title),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.loading_img),
                                error = painterResource(id = R.drawable.broken_image_24),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.posted_by, recipe.user_name, recipe.user_email),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.description_section),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = recipe.description,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.ingredients_section),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = recipe.ingredients,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.error_loading_recipe, "Resep tidak ditemukan atau kosong."))
                    }
                }
            }
        }
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text(stringResource(R.string.confirm_delete_title)) },
            text = { Text(stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    selectedRecipe?.id?.let {
                        viewModel.deleteRecipe(it)
                    }
                    showDeleteConfirmationDialog = false
                }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }

    // Dialog Edit Resep
    if (showEditDialog) {
        selectedRecipe?.let { recipeToEdit ->
            val imageUriForDialog = try {
                Uri.parse(recipeToEdit.image_url)
            } catch (e: Exception) {
                null
            }

            ResepDialog(
                initialImageUri = imageUriForDialog,
                initialRecipe = recipeToEdit,
                onDismissRequest = {
                    showEditDialog = false
                }
            ) { recipeIdToUpdate, title, description, ingredient, imageUri ->
                if (recipeIdToUpdate != null) {
                    viewModel.updateRecipe(
                        id = recipeIdToUpdate,
                        title = title,
                        description = description,
                        ingredient = ingredient,
                        imageUri = imageUri,
                        userName = user.username,
                        userEmail = user.email
                    )
                } else {
                    Toast.makeText(context, "Error: Tidak dapat mengupdate resep tanpa ID.", Toast.LENGTH_SHORT).show()
                }
                showEditDialog = false
            }
        }
    }
}
