package com.nativeknights.petzy.upload

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.nativeknights.domain.model.PetType
import com.nativeknights.petzy.viewmodel.UploadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
     navController : NavHostController,
     viewModel: UploadViewModel = hiltViewModel(),
     onUploadSuccess : () -> Unit
) {

    val context = LocalContext.current
    val selectedImage by viewModel.selectedImageUri.collectAsState()
    val caption by viewModel.caption.collectAsState()
    val hashtags by viewModel.hashtags.collectAsState()
    val petType by viewModel.petType.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Upload Pet Moment") })
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image Picker
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFECECEC))
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImage == null) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = "Add photo",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImage),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // PetType Selector
                PetTypeDropdown(
                    selectedType = petType,
                    onTypeSelected = { viewModel.onPetTypeSelected(it) }
                )

                Spacer(Modifier.height(16.dp))

                // Caption
                OutlinedTextField(
                    value = caption,
                    onValueChange = { viewModel.onCaptionChanged(it) },
                    label = { Text("Caption") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // Hashtags
                OutlinedTextField(
                    value = hashtags,
                    onValueChange = { viewModel.onHashtagsChanged(it) },
                    label = { Text("Hashtags (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (petType == null) {
                            Toast.makeText(context, "Please select a Pet Type", Toast.LENGTH_SHORT).show()
                        } else {
                            // Replace with real user data from datastore or viewmodel
                            viewModel.uploadPost()
                            Toast.makeText(context, "Uploaded Successfully!", Toast.LENGTH_SHORT).show()
                            onUploadSuccess()
                        }
                    },
                    enabled = !isUploading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isUploading) "Uploading..." else "Upload")
                }
            }
        }
    )
}

@Composable
fun PetTypeDropdown(
    selectedType: PetType?,
    onTypeSelected: (PetType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val petTypes = PetType.values().toList()

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedType?.name ?: "Select Pet Type")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            petTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}