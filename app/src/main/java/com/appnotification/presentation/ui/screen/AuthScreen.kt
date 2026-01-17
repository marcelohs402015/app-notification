package com.appnotification.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

@Composable
fun AuthScreen(
    credential: GoogleAccountCredential,
    onAuthSuccess: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            // TODO: Implement Google Sign-In
            // This requires additional setup with Google Sign-In API
            onAuthSuccess()
        }) {
            Text("Sign In with Google")
        }
    }
}
