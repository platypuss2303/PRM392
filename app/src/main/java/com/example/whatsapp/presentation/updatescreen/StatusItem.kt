package com.example.whatsapp.presentation.updatescreen

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whatsapp.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.whatsapp.presentation.bottomnavigation.BottomNavigation
import com.example.whatsapp.presentation.chatbox.ChatDesign
import com.example.whatsapp.presentation.chatbox.ChatDesignModel

@Composable
fun MyStatus(){
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box {
            Image(painter = painterResource(id = R.drawable.bhuvan_bam),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(shape = CircleShape),
                contentScale = ContentScale.Crop)
            Icon(painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(25.dp)
                    .align(Alignment.BottomEnd).
                    padding(2.dp)
                    .background(color = colorResource(id = R.color.light_green),
                        shape = RoundedCornerShape(12.dp)
                    )
            )

        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = "My Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "Tap to add status update", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        }
    }
}

data class StatusData(val image: Int, val name: String, val time: String)

@Composable
fun StatusItem(statusData: StatusData){
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = statusData.image),
            contentDescription = null,
            modifier = Modifier.size(60.dp).padding(4.dp).clip(CircleShape),
            contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
           Text(text = statusData.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
           Text(text = statusData.time, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

    }
}