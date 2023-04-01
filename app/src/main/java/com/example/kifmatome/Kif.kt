package com.example.kifmatome

import android.widget.ImageView

data class Kif(
    var icon: ImageView? = null,
    var title: String? = null,
    var tournament: String? = null,
    var date: String? = null,
    var sente: String? = null,
    var gote: String? = null
)
