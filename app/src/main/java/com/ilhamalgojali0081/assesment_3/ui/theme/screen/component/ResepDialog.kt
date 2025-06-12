package com.ilhamalgojali0081.assesment_3.ui.theme.screen.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ilhamalgojali0081.assesment_3.R

@Composable
fun ResepDialog(
    onDismissRequest:() -> Unit,
    onConfirmation:(String, String, String) -> Unit
) {
    var namaResep by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var ingridient by remember { mutableStateOf("") }

    val bitmap: Bitmap?

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
                    onClick = {  },
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
                        onClick = { onConfirmation(namaResep, deskripsi, ingridient) },
                        enabled = namaResep.isNotEmpty()
                                && deskripsi.isNotEmpty()
                                && ingridient.isNotEmpty(),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text( text = stringResource(R.string.batal) )
                    }
                }
            }
        }
    }
}