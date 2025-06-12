package com.ilhamalgojali0081.assesment_3.ui.theme.screen.component

import android.content.ContentResolver
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.ilhamalgojali0081.assesment_3.R
import com.ilhamalgojali0081.assesment_3.ui.theme.Assesment_3Theme

@Composable
fun ResepDialog(
    onDismissRequest:() -> Unit,
    onConfirmation:(String, String, String, Bitmap) -> Unit
) {
    val context = LocalContext.current

    var namaResep by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var ingridient by remember { mutableStateOf("") }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCropper(context.contentResolver, it)
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
                OutlinedTextField(
                    value = namaResep,
                    onValueChange = { namaResep = it },
                    label = {
                        Text(text = stringResource(R.string.nama))
                    },
                    maxLines = 1,
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
                        Text(text = stringResource(R.string.nama))
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(4.dp)
                )
                OutlinedTextField(
                    value = ingridient,
                    onValueChange = { ingridient = it },
                    label = {
                        Text(text = stringResource(R.string.bahan_bahan))
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(4.dp)
                )

                Button(
                    onClick = {
                        val options = CropImageContractOptions(
                            null, CropImageOptions(
                                imageSourceIncludeGallery = true,
                                imageSourceIncludeCamera = true
                            )
                        )

                        launcher.launch(options)
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = stringResource(R.string.upload))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text( text = stringResource(R.string.batal) )
                    }
                    OutlinedButton(
                        onClick = { onConfirmation(namaResep, deskripsi, ingridient, bitmap!!) },
                        enabled = namaResep.isNotEmpty()
                                && deskripsi.isNotEmpty()
                                && ingridient.isNotEmpty()
                                && bitmap != null,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text( text = stringResource(R.string.batal) )
                    }
                }
            }
        }
    }
}

private fun getCropper(
    resolver: ContentResolver,
    result: CropImageView.CropResult
):Bitmap? {
    if (!result.isSuccessful){
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }
    val uri = result.uriContent ?: return null

    return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Assesment_3Theme {
        ResepDialog(
            onDismissRequest = { },
            onConfirmation = { _,_,_,_ -> }
        )
    }
}