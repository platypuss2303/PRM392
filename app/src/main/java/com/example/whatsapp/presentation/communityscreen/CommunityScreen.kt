package com.example.whatsapp.presentation.communityscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whatsapp.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.whatsapp.presentation.bottomnavigation.BottomNavigation

@Composable
@Preview(showSystemUi = true)
fun CommunityScreen(){
    val sampleCommunities = listOf(
        Communities(image = R.drawable.neat_roots, name = "Neat Roots", description = "We love plants"),
        Communities(image = R.drawable.neat_roots, name = "Neat Roots", description = "We love plants"),
        Communities(image = R.drawable.neat_roots, name = "Neat Roots", description = "We love plants"),
    )
    Scaffold(
        topBar = {TopBar()},
        bottomBar = {BottomNavigation()}
    ) {
        Column(modifier = Modifier.padding(it)) {
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.light_green)),
                modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = "Start a new Community", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

            }
            Text(text = "Your Communities", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            LazyColumn {
                items(sampleCommunities){
                    CommunityItemDesign(communities = it)
                }
            }

        }
    }
}

