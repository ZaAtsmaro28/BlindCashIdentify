package com.learn.blindcashidentify

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn.blindcashidentify.ui.theme.BlindCashIdentifyTheme

class FeedbackModeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlindCashIdentifyTheme {
                FeedbackModeScreen()
            }
        }
    }
}

@Composable
fun FeedbackModeScreen() {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                // Ini membantu TalkBack mengenali ini sebagai tampilan utama
                contentDescription = "Halaman pemilihan mode umpan balik"
            },
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pilih Mode Umpan Balik",
                color = Color.Yellow,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .semantics {
                        contentDescription = "Pilih Mode Umpan Balik"
                    }
            )

            Button(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .semantics {
                        contentDescription = "Tombol Umpan Balik Suara"
                    }
            ) {
                Text("Umpan Balik Suara", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .semantics {
                        contentDescription = "Tombol Umpan Balik Getar"
                    }
            ) {
                Text("Umpan Balik Getar", fontSize = 20.sp)
            }
        }
    }
}
