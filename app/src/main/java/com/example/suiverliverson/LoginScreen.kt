package com.example.suiverliverson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.suiverliverson.ui.theme.LightGray
import com.example.suiverliverson.ui.theme.PinkGradientStart
import com.example.suiverliverson.ui.theme.PurpleAccent
import com.example.suiverliverson.ui.theme.White

@Composable
fun LoginScreen(
    store: AccountStore,
    onLogin: () -> Unit,
    onCreateAccount: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val s = LocalStrings.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF1D0E38),
        unfocusedContainerColor = Color(0xFF1D0E38),
        focusedBorderColor = PinkGradientStart,
        unfocusedBorderColor = Color(0xFF4C2A8A),
        focusedTextColor = White,
        unfocusedTextColor = White,
        cursorColor = PinkGradientStart
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0E071E), Color(0xFF160A2D))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // الشعار العائم مع الدائرة المضيئة 🛵
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF221142))
                        .border(
                            2.5.dp,
                            Brush.linearGradient(listOf(PinkGradientStart, PurpleAccent)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(105.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("≡≡≡", color = PinkGradientStart, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = White)) { append("Liv") }
                            withStyle(SpanStyle(color = PinkGradientStart)) { append("ora") }
                        },
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                }

                Spacer(Modifier.height(8.dp))

                // العنوان الفرعي المعلم بخطوط جانبية ── Connexion livreur ──
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.width(40.dp), color = Color(0xFF4C2A8A))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = s.driverLogin,
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(10.dp))
                    HorizontalDivider(Modifier.width(40.dp), color = Color(0xFF4C2A8A))
                }
            }

            Spacer(Modifier.height(32.dp))

            // حقل البريد / الهاتف البيضاوي ✉️
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = null },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                placeholder = { Text(s.emailPhone, color = LightGray, fontSize = 15.sp) },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = PurpleAccent) },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            // حقل كلمة المرور البيضاوي 🔒
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = null },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                placeholder = { Text(s.password, color = LightGray, fontSize = 15.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = PurpleAccent) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = PurpleAccent
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            error?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = PinkGradientStart, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            // زر الدخول الوردي المضيء البيضاوي 🚀
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        error = s.fillAllFields
                    } else {
                        val account = store.authenticate(email, password)
                        if (account == null) {
                            error = s.invalidCredentials
                        } else {
                            store.setCurrent(account)
                            onLogin()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkGradientStart)
            ) {
                Text(s.login, fontSize = 18.sp, color = White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(10.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = White)
            }

            Spacer(Modifier.height(12.dp))

            // رابط نسيت كلمة المرور
            TextButton(onClick = onForgotPassword) {
                Text(
                    text = s.forgotPassword,
                    color = LightGray,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            // خط الفاصل بين الخيارات ── OU ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFF4C2A8A))
                Text(
                    s.or.uppercase(),
                    color = LightGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFF4C2A8A))
            }

            Spacer(Modifier.height(20.dp))

            // زر تواصل مع الدعم البيضاوي
            OutlinedButton(
                onClick = { },
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.height(48.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, PurpleAccent)
            ) {
                Icon(Icons.Default.Headset, null, tint = White)
                Spacer(Modifier.width(8.dp))
                Text(s.contactSupport, color = White, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))

            // رابط إنشاء حساب
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(s.noAccount, color = LightGray, fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onCreateAccount) {
                    Text(s.createAccount, color = PinkGradientStart, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // حاصد اختيار اللغة 🌐
            LanguageSelector()

            Spacer(Modifier.height(40.dp))
        }
    }
}
