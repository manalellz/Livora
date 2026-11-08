package com.example.suiverliverson

import android.content.Context
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.suiverliverson.ui.theme.AppSurface
import com.example.suiverliverson.ui.theme.BlueAccent
import com.example.suiverliverson.ui.theme.DarkGray
import com.example.suiverliverson.ui.theme.Gold
import com.example.suiverliverson.ui.theme.GreenAccent
import com.example.suiverliverson.ui.theme.LightGray
import com.example.suiverliverson.ui.theme.PinkGradientStart
import com.example.suiverliverson.ui.theme.PurpleAccent
import com.example.suiverliverson.ui.theme.Red
import com.example.suiverliverson.ui.theme.White
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun HomeScreen(
    deliveryManager: DeliveryManager,
    onNavigateToMap: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onOrderClick: (Order) -> Unit = {}
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val account = remember { AccountStore(context).current() }
    val imageUriString = account?.imageUri
    var online by remember { mutableStateOf(true) }

    var availableOrders by remember { mutableStateOf(generateRandomOrdersList()) }
    var incomingOrder by remember { mutableStateOf<Order?>(null) }
    var userCityName by remember { mutableStateOf("Skhirat") }

    // تحديد اسم المدينة الحقيقية للـ GPS
    LaunchedEffect(Unit) {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            @Suppress("MissingPermission")
            val lastKnown = try {
                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } catch (e: SecurityException) { null }
            if (lastKnown != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lastKnown.latitude, lastKnown.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea
                    if (!city.isNullOrEmpty()) {
                        userCityName = city
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(online) {
        if (online) {
            delay(2000L)
            if (incomingOrder == null) {
                incomingOrder = generateRandomIncomingOrder()
            }
        }
        while (online) {
            delay(15 * 60 * 1000L)
            if (incomingOrder == null) {
                incomingOrder = generateRandomIncomingOrder()
            }
        }
    }

    val userBitmap = remember(imageUriString) {
        if (!imageUriString.isNullOrEmpty()) {
            try {
                context.contentResolver.openInputStream(Uri.parse(imageUriString)).use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }

    // نافذة إشعار الطلب الجديد
    incomingOrder?.let { order ->
        Dialog(onDismissRequest = { incomingOrder = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Red)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = s.incomingOrder,
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomerAvatar(type = order.avatarType)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(order.customerName, color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(order.id, color = LightGray, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(GreenAccent)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("$${"%.2f".format(order.price)}", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkGray)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = s.orderCategory.format(order.orderType),
                            color = White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkGray)
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Red))
                            Spacer(Modifier.width(8.dp))
                            Text(s.fromPickup.format(order.pickup), color = LightGray, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GreenAccent))
                            Spacer(Modifier.width(8.dp))
                            Text(s.toDelivery.format(order.client), color = LightGray, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(s.distanceKmLabel.format(order.distanceKm), color = Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { incomingOrder = null },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGray),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(s.reject, color = White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                deliveryManager.acceptOrder(order)
                                incomingOrder = null
                                onNavigateToMap()
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(s.accept, color = White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // الشريط العلوي مع الأيقونات والمؤشرات 👑 🔔
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👑", fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AppSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.GridView, contentDescription = null, tint = White, modifier = Modifier.size(20.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { availableOrders = generateRandomOrdersList() }) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = LightGray, modifier = Modifier.size(22.dp))
                }
                Box {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = White, modifier = Modifier.size(26.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Red)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // كارد حالة السائق الفاخرة المطابقة للـ Screenshot 🟣
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(listOf(PurpleAccent, PinkGradientStart)),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    if (userBitmap != null) {
                        Image(
                            bitmap = userBitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .border(2.dp, PinkGradientStart, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.avatar),
                            contentDescription = null,
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .border(2.dp, PinkGradientStart, CircleShape)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (online) GreenAccent else LightGray)
                            .border(2.dp, AppSurface, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (online) s.youAreOnline + " 🟢" else s.youAreOffline,
                        color = White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (online) s.readyForOrders + " 🚀" else s.goOnlineToStart,
                        color = LightGray,
                        fontSize = 12.sp
                    )
                }

                Switch(
                    checked = online,
                    onCheckedChange = { online = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = BlueAccent,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = DarkGray
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // شبكة الإحصائيات الأربعة متساوية الارتفاع والمقاس تماماً 📊
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CustomStatCard(
                icon = Icons.Default.AccountBalanceWallet,
                iconBg = Color(0xFF1E88E5),
                value = "$${"%.2f".format(deliveryManager.totalEarnings)}",
                label = "📈 " + s.today,
                labelColor = GreenAccent,
                modifier = Modifier.weight(1f)
            )
            CustomStatCard(
                icon = Icons.Default.Security,
                iconBg = Color(0xFF1565C0),
                value = "${deliveryManager.completedTrips}",
                label = s.tripsCompleted,
                labelColor = LightGray,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CustomStatCard(
                icon = Icons.Default.Star,
                iconBg = Gold,
                value = "%.1f".format(deliveryManager.rating),
                showStars = true,
                label = s.rating,
                labelColor = LightGray,
                modifier = Modifier.weight(1f)
            )
            CustomStatCard(
                icon = Icons.Default.Place,
                iconBg = PinkGradientStart,
                value = userCityName,
                label = s.currentLocation,
                labelColor = LightGray,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(22.dp))

        // شريط العنوان مع الخط البنفسجي الجانبي 🟣
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(PinkGradientStart)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = s.newTripRequests,
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = { availableOrders = generateRandomOrdersList() }) {
                Text(s.seeAll + " ➔", color = PinkGradientStart, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(10.dp))

        // كروت طلبات الرحلات الجديدة 🛵
        availableOrders.forEach { order ->
            CustomRequestCard(
                order = order,
                onClick = { incomingOrder = order }
            )
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CustomStatCard(
    icon: ImageVector,
    iconBg: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    labelColor: Color = LightGray,
    showStars: Boolean = false
) {
    Card(
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconBg, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    color = White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                if (showStars) {
                    Row {
                        repeat(5) {
                            Text("⭐", fontSize = 9.sp)
                        }
                    }
                    Spacer(Modifier.height(1.dp))
                }
                Text(
                    text = label,
                    color = labelColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CustomerAvatar(type: AvatarType) {
    val (bgColor, emoji) = when (type) {
        AvatarType.WOMAN -> Pair(PinkGradientStart, "👩‍🦰")
        AvatarType.MAN -> Pair(BlueAccent, "👨")
        AvatarType.OLD_MAN -> Pair(Color(0xFF8D6E63), "👴")
        AvatarType.YOUNG -> Pair(GreenAccent, "👦")
    }

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(2.dp, White.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 26.sp)
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(GreenAccent)
                .border(1.5.dp, AppSurface, CircleShape)
                .align(Alignment.BottomEnd)
        )
    }
}

@Composable
private fun CustomRequestCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomerAvatar(type = order.avatarType)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(order.customerName, color = White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(order.pickup, color = LightGray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${order.distanceKm} km • 15 min", color = LightGray, fontSize = 11.sp)
                }
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = PinkGradientStart),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Info ➔", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
