package com.app.cryptotracker.crypto.presentation.app_info

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.cryptotracker.crypto.presentation.app_info.components.HeadingText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HeadingText("App Info")

            appInfo.forEach {
                Spacer(Modifier.height(8.dp))
                Text("⇒ $it")
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HeadingText("Developer info")

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Created by : ")
                    }
                    append("Yash Gamdha")
                },
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue
                        )
                    ) {
                        append("Github")
                    }
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW, Uri.parse("https://github.com/yash-gamdha")
                        )
                        context.startActivity(intent)
                    }
            )
        }
    }
}

private val appInfo = listOf(
    "This app simply fetches crypto currency info from CoinCap API.",
    "Live price of the crypto symbol is only available in Detail screen."
)