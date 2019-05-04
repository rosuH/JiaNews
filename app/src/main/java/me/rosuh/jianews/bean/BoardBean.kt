package me.rosuh.jianews.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoardBean(
    val author: String,
    val content: String,
    val created: String,
    val id: Int,
    val tag: String,
    val title: String,
    val updated: String,
    val views: Int
): Parcelable