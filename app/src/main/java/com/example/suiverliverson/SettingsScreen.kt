package com.example.suiverliverson

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.suiverliverson.ui.theme.GreenAccent
import com.example.suiverliverson.ui.theme.LightGray
import com.example.suiverliverson.ui.theme.PinkGradientStart
import com.example.suiverliverson.ui.theme.PurpleAccent
import com.example.suiverliverson.ui.theme.White

@Composable
fun SettingsScreen(store: AccountStore, onBack: () -> Unit) {
    val s = LocalStrings.current
    val account = store.current()
    val currentLang = LocalLang.current
    val setLang = LocalSetLang.current

    var editFirstName by remember { mutableStateOf(account?.firstName ?: "") }
    var editLastName by remember { mutableStateOf(account?.lastName ?: "") }
    var editPhone by remember { mutableStateOf(account?.login ?: "") }
    var enableNotifs by remember { mutableStateOf(true) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF1D0E38),
        unfocusedContainerColor = Color(0xFF1D0E38),
        focusedBorderColor = PinkGradientStart,
        unfocusedBorderColor = Color(0xFF4C2A8A),
        focusedTextColor = White,
        unfocusedTextColor = White,
        cursorColor = PinkGradientStart
    )
    val pillShape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0E071E), Color(0xFF160A2D))
                )
            )
    ) {
        // زر العودة الفوق ف اليسار ⬅️
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 16.dp, start = 12.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = White)
        }

        // كارد الإعدادات المحاذي والمتساوي ف الوسط 🎯
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(30.dp))

            Text(
                text = s.settings,
                color = White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0E33))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // الاسم
                    OutlinedTextField(
                        value = editFirstName,
                        onValueChange = { editFirstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        label = { Text(s.firstName, color = LightGray, fontSize = 14.sp) },
                        colors = fieldColors,
                        shape = pillShape,
                        singleLine = true
                    )

                    Spacer(Modifier.height(14.dp))

                    // اللقب
                    OutlinedTextField(
                        value = editLastName,
                        onValueChange = { editLastName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        label = { Text(s.lastName, color = LightGray, fontSize = 14.sp) },
                        colors = fieldColors,
                        shape = pillShape,
                        singleLine = true
                    )

                    Spacer(Modifier.height(14.dp))

                    // رقم الهاتف / البريد
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        label = { Text(s.emailPhone, color = LightGray, fontSize = 14.sp) },
                        colors = fieldColors,
                        shape = pillShape,
                        singleLine = true
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(s.language, color = LightGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("en" to "English", "fr" to "Français", "ar" to "العربية").forEach { (code, name) ->
                            Box(
                                modifier = Modifier
                                    .clip(pillShape)
                                    .background(if (currentLang == code) PinkGradientStart else Color(0xFF281845))
                                    .clickable { setLang(code) }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                            ) {
                                Text(name, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(s.enableNotifications, color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = enableNotifs,
                            onCheckedChange = { enableNotifs = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = White,
                                checkedTrackColor = GreenAccent,
                                uncheckedThumbColor = White,
                                uncheckedTrackColor = Color(0xFF281845)
                            )
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (account != null) {
                                val updated = account.copy(
                                    firstName = editFirstName,
                                    lastName = editLastName,
                                    login = editPhone
                                )
                                store.updateAccount(updated)
                            }
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkGradientStart),
                        shape = pillShape
                    ) {
                        Text(s.save, color = White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}
