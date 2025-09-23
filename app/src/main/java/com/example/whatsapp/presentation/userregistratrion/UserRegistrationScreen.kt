package com.example.whatsapp.presentation.userregistratrion

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.whatsapp.R
import com.example.whatsapp.presentation.navigation.Routes
import com.example.whatsapp.presentation.viewmodel.AuthState
import com.example.whatsapp.presentation.viewmodel.PhoneAuthViewModel

@Composable
fun UserRegistrationScreen(navController: NavController, phoneAuthViewModel: PhoneAuthViewModel = hiltViewModel()){
    val authState by phoneAuthViewModel.authState.collectAsState()
    val context = LocalContext.current
    val activity = LocalActivity.current

    if (activity == null) {
        Toast.makeText(context, "No activity available", Toast.LENGTH_SHORT).show()
        return
    }

    var countryCode by remember { mutableStateOf("+1") }
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }


    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedCountry by remember {
        mutableStateOf("Vietnam")
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Enter your phone number", fontSize = 20.sp, color = colorResource(id = R.color.dark_green), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            Text(text = "whatsApp will need to verify your phone number")
            Spacer(modifier = Modifier.width(4.dp))

        }
        Text(text = "what's my number?", color = colorResource(id = R.color.dark_green))
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {expanded = true}, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(230.dp)){
                Text(text = selectedCountry, modifier = Modifier.align(Alignment.Center), fontSize = 16.sp, color = Color.Black)
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.align(
                    Alignment.CenterEnd), tint = colorResource(id = R.color.light_green))



            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 66.dp), thickness = 2.dp, color = colorResource(id = R.color.light_green)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = {expanded = false}, modifier = Modifier.fillMaxWidth()) {
            listOf("India", "USA", "Japan", "Korea").forEach {
                country ->
                DropdownMenuItem(text = {Text(text = country)}, onClick = {
                    selectedCountry = country
                    expanded = false
                })
            }
        }

        when(authState){
            is AuthState.Ideal, is AuthState.Loading, is AuthState.CodeSent -> {
                if(authState is AuthState.CodeSent){
                    verificationId = (authState as AuthState.CodeSent).verificationId
                }

                if(verificationId == null){
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        TextField(value = countryCode,
                            onValueChange = {countryCode = it},
                            modifier = Modifier.width(70.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = colorResource(id = R.color.light_green)

                            ))
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(value = phoneNumber,
                            onValueChange = {phoneNumber = it},
                            placeholder = {Text(text = "Phone number")},
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                                ))

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if(phoneNumber.isNotEmpty()){
                                val fullPhoneNumber = "$countryCode$phoneNumber"
                                phoneAuthViewModel.senderVerificationCode(fullPhoneNumber, activity)

                            }else{
                                Toast.makeText(context, "Please enter your phone number", Toast.LENGTH_SHORT).show()
                            }
                        }, shape = RoundedCornerShape(6.dp), colors = ButtonDefaults.buttonColors(
                            colorResource(id = R.color.dark_green)
                        )
                    ) {
                        Text(text = "Send OTP")
                    }
                    if(authState is AuthState.Loading){
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                }else{
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(text = "Enter the OTP sent to your phone number", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.dark_green))
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        value = otp,
                        onValueChange = {otp = it},
                        placeholder = {Text(text = "OTP")},
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )

                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if(otp.isNotEmpty() && verificationId != null){
                                phoneAuthViewModel.verifyCode(otp, context)
                            }else{
                                Toast.makeText(context, "Please enter the OTP", Toast.LENGTH_SHORT).show()
                            }
                        }, shape = RoundedCornerShape(6.dp), colors = ButtonDefaults.buttonColors(
                            colorResource(id = R.color.dark_green)
                        )
                    ) {
                        Text(text = "Verify OTP")
                    }
                    if(authState is AuthState.Loading){
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                }
            }
            is AuthState.Success -> {
                Log.d("PhoneAuth", "LoginSuccessful")
                phoneAuthViewModel.resetAuthState()
                navController.navigate(Routes.UserProfileScreen){
                    popUpTo<Routes.UserRegistrationScreen>{
                        inclusive = true
                    }
                }

            }

            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}