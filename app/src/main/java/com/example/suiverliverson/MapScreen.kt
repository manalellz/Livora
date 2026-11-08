package com.example.suiverliverson

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.suiverliverson.ui.theme.AppSurface
import com.example.suiverliverson.ui.theme.Blue
import com.example.suiverliverson.ui.theme.Green
import com.example.suiverliverson.ui.theme.Red
import com.example.suiverliverson.ui.theme.White
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberTileOverlayState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

// جالب طبقات الخرائط الحقيقية المفتوحة OpenStreetMap (OSM)
class OpenStreetMapTileProvider : TileProvider {
    override fun getTile(x: Int, y: Int, zoom: Int): Tile? {
        val urlString = "https://tile.openstreetmap.org/$zoom/$x/$y.png"
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "SuiverliversonApp/1.0 (Android)")
            connection.connectTimeout = 4000
            connection.readTimeout = 4000
            connection.connect()

            if (connection.responseCode == 200) {
                val input: InputStream = connection.inputStream
                val buffer = ByteArrayOutputStream()
                val data = ByteArray(4096)
                var count: Int
                while (input.read(data, 0, data.size).also { count = it } != -1) {
                    buffer.write(data, 0, count)
                }
                buffer.flush()
                val imageBytes = buffer.toByteArray()
                Tile(256, 256, imageBytes)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

// دالة جلب مسار الطرقات والشوراع الحقيقي (OSRM Turn-by-Turn Routing)
suspend fun fetchRoadRoute(start: LatLng, way: LatLng, end: LatLng): List<LatLng> {
    return withContext(Dispatchers.IO) {
        try {
            val urlString = "https://router.project-osrm.org/route/v1/driving/${start.longitude},${start.latitude};${way.longitude},${way.latitude};${end.longitude},${end.latitude}?overview=full&geometries=geojson"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("User-Agent", "SuiverliversonApp/1.0")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            if (conn.responseCode == 200) {
                val text = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(text)
                val routes = json.optJSONArray("routes")
                if (routes != null && routes.length() > 0) {
                    val coords = routes.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates")
                    val result = mutableListOf<LatLng>()
                    for (i in 0 until coords.length()) {
                        val c = coords.getJSONArray(i)
                        result.add(LatLng(c.getDouble(1), c.getDouble(0)))
                    }
                    if (result.isNotEmpty()) return@withContext result
                }
            }
            listOf(start, way, end)
        } catch (e: Exception) {
            listOf(start, way, end)
        }
    }
}

val darkMapStyleJson = """
[
  {"elementType": "geometry", "stylers": [{"color": "#1d1d2b"}]},
  {"elementType": "labels.text.fill", "stylers": [{"color": "#e0e0e0"}]},
  {"elementType": "labels.text.stroke", "stylers": [{"color": "#1a1a2b"}]},
  {"featureType": "administrative.locality", "elementType": "labels.text.fill", "stylers": [{"color": "#ff9800"}]},
  {"featureType": "poi", "elementType": "labels.text.fill", "stylers": [{"color": "#00bcd4"}]},
  {"featureType": "poi.park", "elementType": "geometry", "stylers": [{"color": "#263c3f"}]},
  {"featureType": "road", "elementType": "geometry", "stylers": [{"color": "#383852"}]},
  {"featureType": "road", "elementType": "geometry.stroke", "stylers": [{"color": "#212130"}]},
  {"featureType": "road", "elementType": "labels.text.fill", "stylers": [{"color": "#ffffff"}]},
  {"featureType": "road", "elementType": "labels.text.stroke", "stylers": [{"color": "#000000"}, {"weight": 3}]},
  {"featureType": "road.highway", "elementType": "geometry", "stylers": [{"color": "#ff5252"}]},
  {"featureType": "road.highway", "elementType": "labels.text.fill", "stylers": [{"color": "#ffffff"}]},
  {"featureType": "water", "elementType": "geometry", "stylers": [{"color": "#0d0d17"}]}
]
""".trimIndent()

fun getMotorcycleIcon(context: Context): BitmapDescriptor? {
    return try {
        MapsInitializer.initialize(context)
        val bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.parseColor("#007BFF")
            isAntiAlias = true
        }
        canvas.drawCircle(40f, 40f, 36f, paint)
        val textPaint = Paint().apply {
            textSize = 42f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("🛵", 40f, 54f, textPaint)
        BitmapDescriptorFactory.fromBitmap(bitmap)
    } catch (e: Exception) {
        null
    }
}

fun getCompactMarkerIcon(context: Context, emoji: String, colorHex: String): BitmapDescriptor? {
    return try {
        MapsInitializer.initialize(context)
        val bitmap = Bitmap.createBitmap(60, 60, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.parseColor(colorHex)
            isAntiAlias = true
        }
        canvas.drawCircle(30f, 30f, 26f, paint)
        val textPaint = Paint().apply {
            textSize = 28f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(emoji, 30f, 40f, textPaint)
        BitmapDescriptorFactory.fromBitmap(bitmap)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun MapScreen(deliveryManager: DeliveryManager) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val activeOrder = deliveryManager.currentActiveOrder

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    var userGpsPos by remember { mutableStateOf<LatLng?>(null) }

    DisposableEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

            fun updateBestLocation() {
                try {
                    val providers = locationManager?.getProviders(true) ?: emptyList()
                    var bestLocation: Location? = null
                    for (provider in providers) {
                        val loc = locationManager?.getLastKnownLocation(provider) ?: continue
                        if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                            bestLocation = loc
                        }
                    }
                    if (bestLocation != null) {
                        userGpsPos = LatLng(bestLocation.latitude, bestLocation.longitude)
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }

            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    userGpsPos = LatLng(location.latitude, location.longitude)
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }

            try {
                updateBestLocation()
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, listener)
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 1f, listener)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

            onDispose {
                try {
                    locationManager?.removeUpdates(listener)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        } else {
            onDispose { }
        }
    }

    val driverStartPos = userGpsPos ?: LatLng(33.8524, -7.0315)
    val pickupPos = LatLng(driverStartPos.latitude + 0.005, driverStartPos.longitude + 0.005)
    val deliveryPos = LatLng(driverStartPos.latitude + 0.012, driverStartPos.longitude + 0.012)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverStartPos, 15f)
    }

    LaunchedEffect(userGpsPos) {
        userGpsPos?.let { pos ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(pos, 16f)
            )
        }
    }

    // جلب نقاط مسار الطرقات الحقيقية والمنعطفات الشارعية
    var routePoints by remember { mutableStateOf<List<LatLng>>(listOf(driverStartPos, pickupPos, deliveryPos)) }

    LaunchedEffect(driverStartPos) {
        routePoints = fetchRoadRoute(driverStartPos, pickupPos, deliveryPos)
    }

    var enableOsmLayer by remember { mutableStateOf(true) }
    var currentMapTypeIndex by remember { mutableStateOf(0) }
    val mapTypes = listOf(MapType.NORMAL, MapType.HYBRID, MapType.SATELLITE)

    val currentMapType = mapTypes[currentMapTypeIndex]
    val osmTileProvider = remember { OpenStreetMapTileProvider() }

    val mapProperties = remember(hasLocationPermission, currentMapType, enableOsmLayer) {
        MapProperties(
            isMyLocationEnabled = hasLocationPermission,
            mapType = currentMapType,
            mapStyleOptions = if (!enableOsmLayer && currentMapType == MapType.NORMAL) {
                try { MapStyleOptions(darkMapStyleJson) } catch (e: Exception) { null }
            } else null
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false
        )
    }

    var progress by remember { mutableFloatStateOf(0.0f) }
    var isDeliveredShowDialog by remember { mutableStateOf(false) }

    val motorcycleIcon = remember(context) { getMotorcycleIcon(context) }
    val pickupMarkerIcon = remember(context) { getCompactMarkerIcon(context, "🏪", "#E91E63") }
    val deliveryMarkerIcon = remember(context) { getCompactMarkerIcon(context, "🏠", "#28A745") }

    val currentBikePos = remember(progress, routePoints) {
        if (routePoints.isEmpty()) driverStartPos
        else {
            val index = (progress * (routePoints.size - 1)).toInt().coerceIn(0, routePoints.size - 1)
            routePoints[index]
        }
    }

    LaunchedEffect(deliveryManager.deliveryStage) {
        if (deliveryManager.deliveryStage != DeliveryStage.IDLE) {
            while (progress < 1.0f) {
                delay(2200L)
                progress += 0.02f

                if (progress >= 0.5f && deliveryManager.deliveryStage == DeliveryStage.PICKUP_EN_ROUTE) {
                    deliveryManager.completePickup()
                    Toast.makeText(context, s.orderStarted, Toast.LENGTH_SHORT).show()
                }

                if (progress >= 1.0f && deliveryManager.deliveryStage == DeliveryStage.ORDER_PICKED_UP) {
                    isDeliveredShowDialog = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings
        ) {
            if (enableOsmLayer) {
                TileOverlay(
                    state = rememberTileOverlayState(),
                    tileProvider = osmTileProvider
                )
            }

            // خط المسار الأزرق يتبع الطرقات والمنعطفات الشارعية الحقيقية
            Polyline(
                points = routePoints,
                color = Blue,
                width = 12f
            )

            if (pickupMarkerIcon != null) {
                Marker(
                    state = MarkerState(position = pickupPos),
                    icon = pickupMarkerIcon,
                    title = activeOrder?.pickup ?: s.pickup
                )
            } else {
                Marker(
                    state = MarkerState(position = pickupPos),
                    title = activeOrder?.pickup ?: s.pickup
                )
            }

            if (deliveryMarkerIcon != null) {
                Marker(
                    state = MarkerState(position = deliveryPos),
                    icon = deliveryMarkerIcon,
                    title = activeOrder?.client ?: s.delivery
                )
            } else {
                Marker(
                    state = MarkerState(position = deliveryPos),
                    title = activeOrder?.client ?: s.delivery
                )
            }

            if (motorcycleIcon != null) {
                Marker(
                    state = MarkerState(position = currentBikePos),
                    icon = motorcycleIcon,
                    title = s.motorcycle
                )
            } else {
                Marker(
                    state = MarkerState(position = currentBikePos),
                    title = s.motorcycle
                )
            }
        }

        // أزرار التحكم الفوق ف الخريطة
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    val target = userGpsPos ?: driverStartPos
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(target, 16f))
                },
                containerColor = AppSurface,
                contentColor = Red
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "GPS")
            }

            Spacer(Modifier.height(10.dp))

            FloatingActionButton(
                onClick = {
                    enableOsmLayer = !enableOsmLayer
                    val msg = if (enableOsmLayer) "OpenStreetMap Activé 🗺️ (جميع الطرقات والأزقة)" else "Google Maps Mode"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                containerColor = if (enableOsmLayer) Red else AppSurface,
                contentColor = White
            ) {
                Icon(Icons.Default.Map, contentDescription = "OSM Layer")
            }

            Spacer(Modifier.height(10.dp))

            FloatingActionButton(
                onClick = {
                    currentMapTypeIndex = (currentMapTypeIndex + 1) % mapTypes.size
                },
                containerColor = AppSurface,
                contentColor = White
            ) {
                Icon(Icons.Default.Layers, contentDescription = "Map Layers")
            }
        }

        activeOrder?.let { order ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 20.dp, end = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.customerName,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Green)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$${"%.2f".format(order.price)}",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if (isDeliveredShowDialog) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 30.dp, start = 20.dp, end = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Green)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = White)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = s.orderDeliveredConfirm,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            deliveryManager.completeDelivery()
                            isDeliveredShowDialog = false
                            progress = 0.0f
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = White),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(s.confirmer, color = Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}