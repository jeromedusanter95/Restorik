package com.jeromedusanter.restorik.core.data

/**
 * Constants for photo storage configuration.
 *
 * IMPORTANT: The [PHOTOS_DIRECTORY] value must be kept in sync with:
 * - app/src/main/res/xml/file_paths.xml
 *
 * If you change this value, you MUST update the XML file as well, or FileProvider
 * will fail to generate URIs for stored photos.
 */
object PhotoStorageConstants {
    /**
     * Directory name for storing photos in internal storage.
     */
    const val PHOTOS_DIRECTORY = "photos"
}
