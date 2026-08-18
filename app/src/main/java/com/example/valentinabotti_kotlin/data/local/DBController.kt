package com.example.valentinabotti_kotlin.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity//identifica la classe image come una tabella di DB
data class Image(
    @PrimaryKey val imageMid: Int, // Chiave primaria
    val base64: String,            // Rappresentazione base64 dell'immagine
    val version: Int               // Versione dell'immagine
)

@Entity
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Dao
interface ImageDao { //questa interfaccia contiene la query per interagire con il DB

    @Insert(onConflict = OnConflictStrategy.REPLACE)//se un'immagine con la stessa key esiste viene sovrascitta
    suspend fun insertImage(image: Image)//inserisce un'immagine nel DB

    @Query("SELECT * FROM Image")
    suspend fun getAllImages(): List<Image>

    @Query("SELECT base64 FROM Image WHERE imageMid = :mid")
    suspend fun getBase64ByMid(mid: Int): String?
}

@Dao
interface WordDAO { //questa interfaccia contiene la query per interagire con il DB

    @Insert(onConflict = OnConflictStrategy.REPLACE)//se un'immagine con la stessa key esiste viene sovrascitta
    suspend fun insertWord(word: Word)//inserisce un'immagine nel DB

    @Query("SELECT * FROM Word")
    suspend fun getAllWord(): List<Word>

    @Query("SELECT name FROM Word WHERE id = :id_input")
    suspend fun getWordById(id_input: Int): String?
}

@Database(entities = [Image::class, Word::class], version = 2)//classe che rappresenta il DB, con una tabella Image e la versione del DB usata
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao//consente dall'esyterno l'accesso al DAO
    abstract fun wordDAO(): WordDAO
}

object DBController {
    // Singleton del database
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "image-database"
            )
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}