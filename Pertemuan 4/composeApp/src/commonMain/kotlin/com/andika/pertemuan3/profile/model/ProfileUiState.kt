package com.andika.pertemuan3.profile.model

data class ProfileUiState(
    val profile: ProfileData = ProfileData(
        nama = "Andika Dinata",
        bio = "Hanya mahasiswa biasa.",
        nim = "123140096",
        kelas = "RB",
        email = "andika.123140096@student.itera.ac.id",
        nomorTelepon = "+62 812-3456-7890",
        alamat = "Jati Agung, Lampung Selatan, Lampung, Indonesia, Bumi",
    ),
    val editedProfile: ProfileData = profile,
    val isDarkMode: Boolean = false,
    val isEditMode: Boolean = false,
)
