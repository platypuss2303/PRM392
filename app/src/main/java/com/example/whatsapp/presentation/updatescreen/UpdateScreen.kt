package com.example.whatsapp.presentation.updatescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whatsapp.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.whatsapp.presentation.bottomnavigation.BottomNavigation


@Composable
@Preview(showSystemUi = true)
fun UpdateScreen() {
    val scrollState = rememberScrollState()
    val sampleStatus = listOf(
        StatusData(image = R.drawable.bhuvan_bam, name = "Bhuvan Bam", time = "10:00 AM"),
        StatusData(image = R.drawable.mrbeast, name = "Mr.Beast", time = "2 min ago"),
        StatusData(image = R.drawable.rashmika, name = "Huu Huy", time = "15 min ago"),
    )
    val sampleChannels = listOf(
        Channels(
            image = R.drawable.neat_roots,
            name = "Neat Roots",
            description = "This is Neat Roots"
        ),
        Channels(
            image = R.drawable.neat_roots,
            name = "Neat Roots",
            description = "This is Neat Roots"
        ),
        Channels(
            image = R.drawable.neat_roots,
            name = "Neat Roots",
            description = "This is Neat Roots"
        )

    )


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}, containerColor = colorResource(id = R.color.light_green),
                modifier = Modifier.size(65.dp),
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                    contentDescription = null
                )

            }
        }, bottomBar = { BottomNavigation() },
        topBar = { TopBar() }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Status",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
            MyStatus()
            sampleStatus.forEach { data ->
                StatusItem(statusData = data)
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.Gray)
            Text(
                text = "Channels",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = "Stay updated on topics that matter to you. Find your channels to follow below")
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Find channels to follow")

                Spacer(modifier = Modifier.height(16.dp))
                sampleChannels.forEach { data ->
                    ChannelItemDesign(channels = data)
                }


            }
        }

    }
}