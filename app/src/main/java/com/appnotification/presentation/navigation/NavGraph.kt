package com.appnotification.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appnotification.domain.model.Event
import com.appnotification.presentation.ui.screen.EmailListScreen
import com.appnotification.presentation.ui.screen.EventDetailScreen

sealed class Screen(val route: String) {
    data object EmailList : Screen("email_list")
    data class EventDetail(val eventId: String = "{eventId}") : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.EmailList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.EmailList.route) {
            EmailListScreen(
                onEmailClick = { emailId ->
                    // Navigate to email detail or extract events
                    // For now, we'll just show the email list
                }
            )
        }

        composable(Screen.EventDetail("").route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            // In a real implementation, you'd fetch the event by ID
            // For now, this is a placeholder
        }
    }
}
