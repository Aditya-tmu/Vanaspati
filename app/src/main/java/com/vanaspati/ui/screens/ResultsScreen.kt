package com.vanaspati.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vanaspati.ui.ResultsViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(onBack: () -> Unit, initialQuery: String? = null, vm: ResultsViewModel = viewModel()) {
    val loading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val plant by vm.plant.collectAsState()
    val details by vm.details.collectAsState()

    val context = LocalContext.current

    val galleryPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            vm.identifyPlant(bitmap)
        }
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            vm.identifyPlant(it)
        }
    }

    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            takePicture.launch()
        }
    }

    LaunchedEffect(initialQuery) {
        initialQuery?.takeIf { it.isNotBlank() }?.let { q ->
            vm.searchByName(q)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Results") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FloatingActionButton(onClick = { galleryPicker.launch("image/*") }) {
                        Icon(Icons.Filled.Image, contentDescription = "Pick Image")
                    }
                    FloatingActionButton(onClick = { cameraPermission.launch(Manifest.permission.CAMERA) }) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = "Capture")
                    }
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                plant?.let { p ->
                    Column {
                        p.image_url?.let { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = p.common_name,
                                modifier = Modifier.fillMaxWidth().height(200.dp)
                            )
                        }
                        Text(text = p.scientific_name, style = MaterialTheme.typography.titleMedium)
                        p.common_name?.let { commonName ->
                            Text(text = commonName, style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }

                details?.let {
                    Text(it)
                } ?: Text("Search from Home or use the camera to identify a plant.")
            }
        }
    }
}
