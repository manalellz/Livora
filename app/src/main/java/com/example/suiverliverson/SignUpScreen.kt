package com.example.suiverliverson

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.suiverliverson.ui.theme.LightGray
import com.example.suiverliverson.ui.theme.PinkGradientStart
import com.example.suiverliverson.ui.theme.PurpleAccent
import com.example.suiverliverson.ui.theme.White

@Composable
fun appFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF1D0E38),
    unfocusedContainerColor = Color(0xFF1D0E38),
    focusedBorderColor = PinkGradientStart,
    unfocusedBorderColor = Color(0xFF4C2A8A),
    focusedTextColor = White,
    unfocusedTextColor = White,
    cursorColor = PinkGradientStart
)

@Composable
fun SignUpScreen(store: AccountStore, onBack: () -> Unit, onSuccess: () -> Unit) {
    val s = LocalStrings.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val colors = appFieldColors()
    val shape = RoundedCornerShape(28.dp)

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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
        }

        // الفورم محاذي ف الوسط تماماً (Centered Vertically & Horizontally) 🎯
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                s.createAccount,
                color = White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(28.dp))

            // الاسم واللقب
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it; error = null },
                    modifier = Modifier.weight(1f).height(58.dp),
                    placeholder = { Text(s.lastName, color = LightGray, fontSize = 14.sp) },
                    singleLine = true,
                    shape = shape,
                    colors = colors
                )
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it; error = null },
                    modifier = Modifier.weight(1f).height(58.dp),
                    placeholder = { Text(s.firstName, color = LightGray, fontSize = 14.sp) },
                    singleLine = true,
                    shape = shape,
                    colors = colors
                )
            }

            Spacer(Modifier.height(16.dp))

            // العمر
            OutlinedTextField(
                value = age,
                onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) age = it; error = null },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                placeholder = { Text(s.age, color = LightGray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = PurpleAccent) },
                singleLine = true,
                shape = shape,
                colors = colors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(16.dp))

            // البريد / الهاتف
            OutlinedTextField(
                value = login,
                onValueChange = { login = it; error = null },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                placeholder = { Text(s.emailPhone, color = LightGray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = PurpleAccent) },
                singleLine = true,
                shape = shape,
                colors = colors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            // كلمة المرور
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = null },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                placeholder = { Text(s.password, color = LightGray, fontSize = 14.sp) },
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
                shape = shape,
                colors = colors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(16.dp))

            // تأكيد كلمة المرور
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it; error = null },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                placeholder = { Text(s.confirmPassword, color = LightGray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = PurpleAccent) },
                visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                shape = shape,
                colors = colors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = PinkGradientStart, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(28.dp))

            // زر إنشاء حساب
            Button(
                onClick = {
                    val ageInt = age.toIntOrNull()
                    error = when {
                        firstName.isBlank() || lastName.isBlank() || age.isBlank() ||
                                login.isBlank() || password.isBlank() || confirm.isBlank() ->
                            s.fillAllFields
                        ageInt == null || ageInt !in 18..70 -> s.invalidAge
                        password.length < 6 -> s.passwordShort
                        password != confirm -> s.passwordMismatch
                        store.exists(login) -> s.accountExists
                        else -> null
                    }
                    if (error == null && ageInt != null) {
                        val account = store.register(
                            firstName, lastName, ageInt, "B", login, password, null
                        )
                        store.setCurrent(account)
                        onSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = shape,
                colors = ButtonDefaults.buttonColors(containerColor = PinkGradientStart)
            ) {
                Text(s.createAccount, fontSize = 18.sp, color = White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(s.haveAccount, color = LightGray, fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onBack) {
                    Text(s.login, color = PinkGradientStart, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}
