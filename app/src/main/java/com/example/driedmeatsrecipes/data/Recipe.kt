package com.example.driedmeatsrecipes.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    val dryingTime: String,
    val temperature: String,
    val images: List<String> = emptyList(), // Пазим пътищата към снимките
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable
