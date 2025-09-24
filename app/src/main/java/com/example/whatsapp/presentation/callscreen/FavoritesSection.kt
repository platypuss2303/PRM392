package com.example.whatsapp.presentation.callscreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp.R

@Composable
@Preview(showSystemUi = true)
fun FavoritesSection() {
    val sampleFavorites = listOf(
        FavoritesContact(image = R.drawable.ashish_chanchlani, name = "Ashish Chanchlani"),
        FavoritesContact(image = R.drawable.mrbeast, name = "Mr Beast"),
        FavoritesContact(image = R.drawable.akshay_kumar, name = "Akshay Kumar"),
        FavoritesContact(image = R.drawable.bhuvan_bam, name = "Bhuvan Bam"),
        FavoritesContact(image = R.drawable.sharukh_khan, name = "Sharukh Khan"),
    )
    Column {
        Text(
            text = "Favorites",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        )
        {
            sampleFavorites.forEach {
                FavoritesItem(favorites = it)
            }
        }
    }
}

data class FavoritesContact(
    val image: Int, val name: String
)






