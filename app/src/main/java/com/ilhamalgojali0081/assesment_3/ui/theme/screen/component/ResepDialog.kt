package com.ilhamalgojali0081.assesment_3.ui.theme.screen.component

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ilhamalgojali0081.assesment_3.R
import com.ilhamalgojali0081.assesment_3.model.Resep
import com.ilhamalgojali0081.assesment_3.ui.theme.Assesment_3Theme
import androidx.compose.runtime.LaunchedEffect


@Composable
fun ResepDialog(
    initialImageUri: Uri? = null,
    initialRecipe: Resep? = null,
    onDismissRequest: () -> Unit,
    onConfirmation: (String?, String, String, String, Uri?) -> Unit
) {
    val context = LocalContext.current

    var namaResep by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var ingridient by remember { mutableStateOf("") }
    var currentImageUri by remember { mutableStateOf(initialImageUri) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri: Uri? ->
        currentImageUri = uri
        if (uri == null) {
            Toast.makeText(context,
                "Tidak ada gambar terpilih.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(initialRecipe) {
        if (initialRecipe != null) {
            namaResep = initialRecipe.title
            deskripsi = initialRecipe.description
            ingridient = initialRecipe.ingredients
            if (initialRecipe.image_url != null && currentImageUri == null) {
                try {
                    currentImageUri = Uri.parse(initialRecipe.image_url)
                } catch (e: Exception) {
                    Log.e("ResepDialog", "Invalid URI: ${initialRecipe.image_url}")
                    currentImageUri = null
                }
            }
        }
    }

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (currentImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(currentImageUri)
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.gambar_belum_ada))
                        }
                    }
                }

                OutlinedTextField(
                    value = namaResep,
                    onValueChange = { namaResep = it },
                    label = {
                        Text(text = stringResource(R.string.nama))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(4.dp)
                )
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = {
                        Text(text = stringResource(R.string.deskripsi))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                )
                OutlinedTextField(
                    value = ingridient,
                    onValueChange = { ingridient = it },
                    label = {
                        Text(text = stringResource(R.string.bahan_bahan))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.batal))
                    }
                    Button(
                        onClick = { onConfirmation(initialRecipe?.id, namaResep,
                            deskripsi, ingridient, currentImageUri) },
                        enabled = namaResep.isNotEmpty() && deskripsi.isNotEmpty()
                                && ingridient.isNotEmpty() && currentImageUri != null,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.simpan))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ResepDialogPreview() {
    Assesment_3Theme {
        ResepDialog(
            initialImageUri = Uri.parse("android.resource://com." +
                    "ilhamalgojali0081.assesment_3/" + R.drawable.ic_launcher_background),
            onDismissRequest = { },
            onConfirmation = { _, _, _, _, _ -> }
        )
    }
}
