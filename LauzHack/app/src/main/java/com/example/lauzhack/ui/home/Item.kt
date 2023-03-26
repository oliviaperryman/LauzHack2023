package com.example.lauzhack.ui.home

import android.location.Location
import android.net.Uri

data class Item(val name: String, val location: String, val tags: MutableList<String>, val uri: Uri)