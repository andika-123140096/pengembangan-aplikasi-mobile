package com.andika.pertemuan3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.andika.pertemuan3.profile.model.ProfileData

@Composable
fun EditProfileScreen(
    profile: ProfileData,
    onNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onNimChange: (String) -> Unit,
    onKelasChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onNomorTeleponChange: (String) -> Unit,
    onAlamatChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileCard {
            Text(
                text = "Edit Profil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInputField(label = "Nama", value = profile.nama, onValueChange = onNameChange)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInputField(label = "Bio", value = profile.bio, onValueChange = onBioChange, minLines = 3)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInputField(label = "NIM", value = profile.nim, onValueChange = onNimChange)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInputField(label = "Kelas", value = profile.kelas, onValueChange = onKelasChange)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInputField(label = "Email", value = profile.email, onValueChange = onEmailChange)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInputField(
                label = "Nomor Telepon",
                value = profile.nomorTelepon,
                onValueChange = onNomorTeleponChange,
            )
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInputField(label = "Alamat", value = profile.alamat, onValueChange = onAlamatChange, minLines = 2)

            Spacer(modifier = Modifier.height(16.dp))
            PrimaryActionButton(text = "Simpan", onClick = onSaveClick)

            Spacer(modifier = Modifier.height(8.dp))
            PrimaryActionButton(text = "Batal", onClick = onCancelClick)
        }
    }
}

@Composable
fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = minLines == 1,
        minLines = minLines,
    )
}
