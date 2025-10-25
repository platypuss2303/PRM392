package com.example.whatsapp.presentation.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whatsapp.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.whatsapp.presentation.bottomnavigation.BottomNavigation
import com.example.whatsapp.presentation.chatbox.ChatDesign
import com.example.whatsapp.presentation.chatbox.ChatDesignModel
import com.example.whatsapp.presentation.navigation.Routes
import com.example.whatsapp.presentation.viewmodel.BaseViewModel
import com.google.firebase.auth.FirebaseAuth


//@Composable
//@Preview(showSystemUi = true)
//fun HomeScreen() {
//    val chatData = listOf(
//        ChatDesignModel(image = R.drawable.salman_khan ,name = "Salman Khan", time = "10:00 AM", message = "Hello fenn !!" ),
//        ChatDesignModel(image = R.drawable.disha_patani ,name = "Dia Patina", time = "7:00 AM", message = "Oi doi oi !!" ),
//        ChatDesignModel(image = R.drawable.rashmika ,name = "Mika", time = "11:00 AM", message = "Oi doi oi !!" ),
//        ChatDesignModel(image = R.drawable.mrbeast, name = "Mr.Beast", time = "9:00 PM", message = "Join with us ?!"))
//
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {},
//                containerColor = colorResource(id = R.color.light_green),
//                modifier = Modifier.size(65.dp),
//                contentColor = Color.White
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.chat_icon),
//                    contentDescription = null,
//                    modifier = Modifier.size(28.dp)
//                )
//
//            }
//        }, bottomBar = {BottomNavigation()}
//    ){
//            Column(modifier = Modifier.padding(it)) {
//                Spacer(modifier = Modifier.height(16.dp))
//                Box(modifier = Modifier.fillMaxWidth()){
//                    Text(text = "SocialChat", fontSize = 28.sp, color = colorResource(id = R.color.light_green),
//                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp), fontWeight = FontWeight.Bold)
//                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
//                        IconButton(onClick = {}) {
//                            Icon(painter = painterResource(R.drawable.camera),
//                                contentDescription = null,
//                                modifier = Modifier.size(24.dp))
//
//                        }
//                        IconButton(onClick = {}) {
//                            Icon(painter = painterResource(R.drawable.search),
//                                contentDescription = null,
//                                modifier = Modifier.size(24.dp))
//
//                        }
//                        IconButton(onClick = {}) {
//                            Icon(painter = painterResource(R.drawable.more),
//                                contentDescription = null,
//                                modifier = Modifier.size(24.dp))
//
//                        }
//
//                    }
//                }
//                HorizontalDivider()
//                LazyColumn {
//                    items(chatData){
//                        ChatDesign(chatDesignModel = it)
//
//                    }
//                }
//            }
//
//
//    }
//}

@Composable
fun HomeScreen(navHostController: NavHostController, homeBaseViewModel: BaseViewModel){
    var showPopup by remember {
        mutableStateOf(false)

    }
    val chatData by homeBaseViewModel.chatList.collectAsState()

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if(userId != null){
        LaunchedEffect(userId) {
            homeBaseViewModel.getChatForUser(userId){
                chats ->

            }
        }
    }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(floatingActionButton =  {
        FloatingActionButton(
            onClick = {},
            containerColor = colorResource(id = R.color.light_green),
            contentColor = Color.White,
            modifier = Modifier.size(65.dp)
        ) {
            Icon(painter = painterResource(R.drawable.chat_icon),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.White)
        }
    }, bottomBar = {
        BottomNavigation(navHostController, selectedItem = 0, onClick = { index ->
            when(index){
                0 -> {navHostController.navigate(Routes.HomeScreen)}
                1 -> {navHostController.navigate(Routes.UpdateScreen)}
                2 -> {navHostController.navigate(Routes.CommunityScreen)}
                3 -> {navHostController.navigate(Routes.CallScreen)}
            }
        })
    }) {
        Column(modifier = Modifier.padding(it).background(color = Color.White)){
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()){
                var isSearching by remember { mutableStateOf(false) }
                var searchText by remember { mutableStateOf("") }
                var showMenu by remember { mutableStateOf(false) }

                if(isSearching){
                    TextField(value = searchText, onValueChange = {
                        searchText = it
                    },  placeholder = {
                        Text(text = "Search", color = Color.Gray)
                    }, singleLine = true, modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp).fillMaxWidth(0.8f),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
)

                }else {
                    Text(text = "WhatsApp", fontSize = 28.sp, color = colorResource(id = R.color.light_green),
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp), fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                        IconButton(onClick = {}) {
                            Icon(painter = painterResource(R.drawable.camera),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp))
                        }

                        if(isSearching){
                            IconButton(onClick = {
                                isSearching = false
                                searchText = ""
                            }) {
                                Icon(painter = painterResource(R.drawable.camera),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp))
                            }

                        }else{
                            IconButton(onClick = {
                                isSearching = true
                            }){
                                Icon(painter = painterResource(R.drawable.search),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }

                Icon(painter = painterResource(R.drawable.more), contentDescription = null, modifier = Modifier.size(24.dp))
                DropdownMenu(expanded = showMenu, onDismissRequest = {
                    showMenu = false
                }, modifier = Modifier.background(color = Color.White))  {
                    DropdownMenuItem(text = {Text(text = "News Group")}, onClick = {showMenu = false})
                    DropdownMenuItem(text = {Text(text = "News Broadcast")}, onClick = {showMenu = false})
                    DropdownMenuItem(text = {Text(text = "Linked Device")}, onClick = {showMenu = false})
                    DropdownMenuItem(text = {Text(text = "Starred Messages")}, onClick = {showMenu = false})
                    DropdownMenuItem(text = {Text(text = "Settings")}, onClick = {showMenu = false
                        navHostController.navigate(Routes.SettingScreen)
                    })

                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            if(showMenu){
                AddUserPopup(onDismiss = {showPopup = false}, onUserAdd = {newUser ->
                    homeBaseViewModel.addChat(newUser)
                }, baseViewModel = homeBaseViewModel)
            }

            LazyColumn{
                items(chatData){ chat ->
                    ChatDesign(chat, onClick = {
                        navHostController.navigate(Routes.ChatScreen.createRoutes(phoneNumber = chat.phoneNumber ?: "Ok"))

                    }, baseViewModel = homeBaseViewModel)
                }

            }
        }
    }
}

@Composable
fun AddUserPopup(onDismiss: () -> Unit, onUserAdd: (ChatDesignModel) -> Unit, baseViewModel: BaseViewModel){
    var phoneNumber by remember { mutableStateOf("") }
    var userFound by remember { mutableStateOf<ChatDesignModel?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        TextField(value = phoneNumber,
            onValueChange = {phoneNumber = it},
            label = {Text(text = "Enter phone number")},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
            )
        Row(){
            Button(onClick = {
                isSearching = true
                baseViewModel.searchUserByPhoneNumber(phoneNumber){
                    user ->
                    isSearching = false
                    if(user != null){
                        userFound = user
                    }else{
                        userFound = null
                    }
                }
            }, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.light_green))){
                Text(text = "Search")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onDismiss, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(colorResource(R.color.light_green))){
            Text(text = "Cancel")
        }
    }
    if(isSearching){
        Text(text = "Searching...", color = Color.Gray)
    }

    userFound?.let{
        Column {
            Text(text = "User found: ${it.name}")
            Button(onClick = {
              onUserAdd(it)
              onDismiss()

            }, colors = ButtonDefaults.buttonColors(colorResource(R.color.light_green))){
                Text(text = "Add to chat")
            }
        }
    }?:run{
        if(!isSearching){
            Text("No user found with this phone number")
        }
    }



}







//Column(modifier = Modifier.padding(it).background(color = Color.White)) {
//    Spacer(modifier = Modifier.height(8.dp))
//    Box(modifier = Modifier.fillMaxWidth()){
//        var isSearching by remember {mutableStateOf(false)}
//        var searchText by remember { mutableStateOf("") }
//        var showMenu by remember { mutableStateOf(false) }
//        if(isSearching){
//            TextField(value =  searchText, onValueChange = {
//                searchText = it
//            }, placeholder = {
//                Text(text = "Search", color = Color.Gray)
//            }, singleLine = true, modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp).fillMaxWidth(0.8f), colors = TextFieldDefaults.colors(
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                unfocusedContainerColor = Color.Transparent,
//                focusedContainerColor = Color.Transparent
//
//            ))
//        }else{
//            Text(text = "WhatsApp", fontSize = 28.sp, color = colorResource(id = R.color.light_green), modifier = Modifier.align(
//                Alignment.Center).padding(start = 12.dp), fontWeight = FontWeight.Bold)
//            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
//                IconButton(onClick = {}) {
//                    Icon(painter = painterResource(R.drawable.camera),
//                        contentDescription = null,
//                        modifier = Modifier.size(24.dp))
//
//                }
//                if(isSearching){
//                    IconButton(onClick = {
//                        isSearching = false
//                        searchText = ""
//                    }) {
//                        Icon(painter = painterResource(R.drawable.cross),
//                            contentDescription = null,
//                            modifier = Modifier.size(24.dp))
//                    }
//                }
//                else{
//                    IconButton(onClick = {
//                        isSearching = true
//                    }) {
//                        Icon(painter = painterResource(R.drawable.search),
//                            contentDescription = null,
//                            modifier = Modifier.size(24.dp))
//                    }
//
//                }
//                IconButton(onClick = {
//                    showMenu = !showMenu
//                }) {
//                    Icon(painter = painterResource(R.drawable.more),
//                        contentDescription = null,
//                        modifier = Modifier.size(24.dp))
//                    DropdownMenu(expanded = showMenu, onDismissRequest = {
//                        showMenu = false
//
//                    }, modifier = Modifier.background(color = Color.White)) {
//                        DropdownMenuItem(text = {Text(text = "New Group")}, onClick = {showMenu = false})
//                        DropdownMenuItem(text = {Text(text = "New Broadcast")}, onClick = {showMenu = false})
//                        DropdownMenuItem(text = {Text(text = "Linked Device")}, onClick = {showMenu = false})
//                        DropdownMenuItem(text = {Text(text = "Starred Messages")}, onClick = {showMenu = false})
//                        DropdownMenuItem(text = {Text(text = "Settings")}, onClick = {showMenu = false
//                            navHostController.navigate(Routes.SettingScreen)})
//                    }
//                }
//            }
//        }
//
//    }
//
//
//
//}