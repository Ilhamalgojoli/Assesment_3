package com.ilhamalgojali0081.assesment_3.ui.theme.screen.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ilhamalgojali0081.assesment_3.R
import com.ilhamalgojali0081.assesment_3.model.Resep

@Composable
fun ListResep(resep: Resep) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, Color.Gray),
        contentAlignment = Alignment.BottomCenter
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(resep.image_url)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.gambar, resep.title),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(
                id = R.drawable.broken_image_24
            ),
            onError = {
                Log.e("ImageLoad", "Gagal load image: ${it.result.throwable.message}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(Color(0f, 0f, 0f, 0.5f))
                .padding(4.dp)
        ) {
            Text(
                text = resep.title,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
