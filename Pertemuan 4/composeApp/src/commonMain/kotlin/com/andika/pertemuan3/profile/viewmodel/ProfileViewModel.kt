package com.andika.pertemuan3.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.andika.pertemuan3.profile.model.ProfileData
import com.andika.pertemuan3.profile.model.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun openEditProfile() {
        _uiState.update { currentState ->
            currentState.copy(
                editedProfile = currentState.profile,
                isEditMode = true,
            )
        }
    }

    fun cancelEditProfile() {
        _uiState.update { currentState ->
            currentState.copy(
                editedProfile = currentState.profile,
                isEditMode = false,
            )
        }
    }

    fun onEditedNameChange(newName: String) = updateEditedProfile { copy(nama = newName) }

    fun onEditedBioChange(newBio: String) = updateEditedProfile { copy(bio = newBio) }

    fun onEditedNimChange(newNim: String) = updateEditedProfile { copy(nim = newNim) }

    fun onEditedKelasChange(newKelas: String) = updateEditedProfile { copy(kelas = newKelas) }

    fun onEditedEmailChange(newEmail: String) = updateEditedProfile { copy(email = newEmail) }

    fun onEditedNomorTeleponChange(newNomor: String) = updateEditedProfile { copy(nomorTelepon = newNomor) }

    fun onEditedAlamatChange(newAlamat: String) = updateEditedProfile { copy(alamat = newAlamat) }

    fun saveProfile() {
        _uiState.update { currentState ->
            currentState.copy(
                profile = currentState.editedProfile,
                isEditMode = false,
            )
        }
    }

    fun onDarkModeChange(isEnabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isDarkMode = isEnabled)
        }
    }

    private fun updateEditedProfile(update: ProfileData.() -> ProfileData) {
        _uiState.update { currentState ->
            currentState.copy(editedProfile = currentState.editedProfile.update())
        }
    }
}
