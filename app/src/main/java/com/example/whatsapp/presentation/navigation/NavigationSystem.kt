package com.example.whatsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whatsapp.presentation.communityscreen.CommunityScreen
import com.example.whatsapp.presentation.homescreen.HomeScreen
import com.example.whatsapp.presentation.splashscreen.SplashScreen
import com.example.whatsapp.presentation.userregistratrion.UserRegistrationScreen
import com.example.whatsapp.presentation.welcomescreen.WelcomeScreen

@Composable
fun NavigationSystem(){
    val navController = rememberNavController()

    NavHost(startDestination = Routes.SplashScreen, navController = navController){
        composable<Routes.SplashScreen>{
            SplashScreen(navController)
        }
        composable<Routes.WelcomeScreen>{
            WelcomeScreen(navController)
        }
        composable<Routes.HomeScreen>{
            HomeScreen()
        }
        composable<Routes.UserRegistrationScreen>{
            UserRegistrationScreen(navController)
        }
        composable<Routes.CommunityScreen>{
            CommunityScreen()
        }

    }

}