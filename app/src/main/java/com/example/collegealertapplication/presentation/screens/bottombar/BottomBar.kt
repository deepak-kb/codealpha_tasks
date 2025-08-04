package com.example.collegealertapplication.presentation.screens.bottombar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegealertapplication.R
import com.example.collegealertapplication.data.BottomBarItem

@Composable
fun BottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomBarItem("Home", R.drawable.home_icon),
        BottomBarItem("Alert", R.drawable.alert_icon),
        BottomBarItem("Admin", R.drawable.event_icon),
        BottomBarItem("Profile", R.drawable.profile_icon)
    )

    NavigationBar(
        tonalElevation = 4.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Column(
                        modifier = Modifier.height(50.dp).width(50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                },
                alwaysShowLabel = false
            )
        }
    }
}
