package com.andika.pertemuan3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.andika.pertemuan3.profile.model.ProfileData
import com.andika.pertemuan3.profile.viewmodel.ProfileViewModel
import org.jetbrains.compose.resources.painterResource
import pertemuan3.composeapp.generated.resources.Res
import pertemuan3.composeapp.generated.resources.foto_profil

@Composable
@Preview
fun App() {
    val viewModel = remember { ProfileViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    MaterialTheme(
        colorScheme = if (uiState.isDarkMode) {
            androidx.compose.material3.darkColorScheme()
        } else {
            androidx.compose.material3.lightColorScheme()
        },
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .safeContentPadding()
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                PageHeader(
                    title = if (uiState.isEditMode) "Edit Profile" else "My Profile",
                    isDarkMode = uiState.isDarkMode,
                    onDarkModeChange = viewModel::onDarkModeChange,
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.isEditMode) {
                    EditProfileScreen(
                        profile = uiState.editedProfile,
                        onNameChange = viewModel::onEditedNameChange,
                        onBioChange = viewModel::onEditedBioChange,
                        onNimChange = viewModel::onEditedNimChange,
                        onKelasChange = viewModel::onEditedKelasChange,
                        onEmailChange = viewModel::onEditedEmailChange,
                        onNomorTeleponChange = viewModel::onEditedNomorTeleponChange,
                        onAlamatChange = viewModel::onEditedAlamatChange,
                        onSaveClick = viewModel::saveProfile,
                        onCancelClick = viewModel::cancelEditProfile,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    ProfileScreen(
                        profile = uiState.profile,
                        onEditClick = viewModel::openEditProfile,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
fun PageHeader(
    title: String,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Dark",
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Switch(
                checked = isDarkMode,
                onCheckedChange = onDarkModeChange,
            )
        }
    }
}

@Composable
fun ProfileScreen(
    profile: ProfileData,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileCard {
            ProfileHeader(name = profile.nama)

            Spacer(modifier = Modifier.height(12.dp))
            ProfileBody(profile = profile)

            Spacer(modifier = Modifier.height(18.dp))
            PrimaryActionButton(
                text = "Edit Profil",
                onClick = onEditClick,
            )
        }
    }
}

@Composable
fun ProfileCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}

@Composable
fun ProfileHeader(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(232.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.foto_profil),
                contentDescription = "Foto profil",
                modifier = Modifier
                    .size(226.dp)
                    .clip(CircleShape),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ProfileBody(profile: ProfileData) {
    Text(
        text = profile.bio,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(16.dp))

    val details = listOf(
        "NIM" to profile.nim,
        "Kelas" to profile.kelas,
        "Email" to profile.email,
        "Nomor Telepon" to profile.nomorTelepon,
        "Alamat" to profile.alamat,
    )

    details.forEach { (label, value) ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.42f),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .weight(0.58f)
                    .padding(start = 8.dp),
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text)
    }
}