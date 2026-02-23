package com.jeromedusanter.restorik.core.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

class PhotoStorageManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    suspend fun copyPhotoToInternalStorage(sourceUri: Uri): Uri? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return@withContext null

            val photosDir = File(context.filesDir, PhotoStorageConstants.PHOTOS_DIRECTORY)
            if (!photosDir.exists()) {
                photosDir.mkdirs()
            }

            val fileName = "${UUID.randomUUID()}.jpg"
            val destinationFile = File(photosDir, fileName)

            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.use { input ->
                    input.copyTo(out = outputStream)
                }
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                destinationFile
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deletePhoto(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            // For FileProvider URIs, we need to extract the filename and construct the actual path
            if (uri.authority == "${context.packageName}.fileprovider") {
                // Extract filename from the FileProvider URI path (e.g., /photos/uuid.jpg -> uuid.jpg)
                val fileName = uri.lastPathSegment
                if (fileName != null) {
                    val photosDir = File(context.filesDir, PhotoStorageConstants.PHOTOS_DIRECTORY)
                    val file = File(photosDir, fileName)
                    if (file.exists()) {
                        return@withContext file.delete()
                    }
                }
                false
            } else {
                // For other URIs (e.g., content:// from gallery), try the path-based approach
                val file = getFileFromUri(uri = uri)
                if (file != null && file.exists()) {
                    file.delete()
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isInternalStorageUri(uri: Uri): Boolean {
        return uri.authority == "${context.packageName}.fileprovider"
    }

    suspend fun downloadPhotoToDownloads(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext false

            val fileName = "Restorik_${System.currentTimeMillis()}.jpg"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above, use MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val imageUri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                if (imageUri != null) {
                    context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                        inputStream.use { input ->
                            input.copyTo(out = outputStream)
                        }
                    }
                    true
                } else {
                    false
                }
            } else {
                // For Android 9 and below, use legacy approach
                val downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }

                val destinationFile = File(downloadsDir, fileName)
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.use { input ->
                        input.copyTo(out = outputStream)
                    }
                }
                true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val path = uri.path
            if (path != null) {
                File(path)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
