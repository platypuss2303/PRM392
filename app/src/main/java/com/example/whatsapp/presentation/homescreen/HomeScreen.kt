package com.example.whatsapp.presentation.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whatsapp.R
import androidx.compose.ui.graphics.Color

@Composable
@Preview(showSystemUi = true)
fun HomeScreen(){
    Scaffold (
        floatingActionButton = {
        FloatingActionButton(onClick = {}, containerColor = colorResource(id = R.color.light_green), modifier = Modifier.size(64.dp), contentColor = Color.White) {
            Icon(painter = painterResource(id = R.drawable.chat_icon), contentDescription = null, modifier = Modifier.size(28.dp))

        }
    }){
        Column(modifier = Modifier.padding(it)) {

        }
    }



}