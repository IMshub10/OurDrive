package com.summer.ourdrive.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.github.shamil.Xid
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import java.io.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils {
    fun generateUUID(): String {
        return Xid.get().toHexString()
    }

    fun getIn2Format(value: Int): String {
        if (value > 99) {
            throw RuntimeException("Does not support more than 2 digits")
        }
        return if (value < 10) {
            "0$value"
        } else {
            value.toString()
        }
    }

    fun checkPermissions(activityContext: Context, vararg permissionStrings: String): Boolean {
        for (permission in permissionStrings) {
            if (ActivityCompat.checkSelfPermission(
                    activityContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    fun buttonUsingURI(id: Long): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MediaStore.Images.Media.getContentUri("external", id)
        } else {
            null
        }
    }

    fun getImageRealPathFromUri(fileUri: Uri, context: Context): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        MediaStore.Images.Media.getContentUri(fileUri.toString())
        val cursor = context.contentResolver.query(fileUri, proj, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            val string = it.getString(columnIndex)
            it.close()
            return string
        }
        return null
    }

    fun getImageIdFromFileName(name: String): Long? {
        val m: Matcher = Pattern.compile("\\d+").matcher(name)
        var id: Long? = null
        while (m.find()) {
            id = m.group().toLong()
        }
        return id
    }

    suspend fun saveFileToInternalStorage(
        context: Context,
        folderId: String,
        imageId: String,
        imageUri: Uri
    ) {
        val imageFileFromUri = FileUtil.from(context, imageUri)
        val compressedImageFile = Compressor.compress(context, imageFileFromUri) {
            quality(80)
        }

        val imagesDir = File(context.filesDir, "images")
        imagesDir.mkdir()
        val folderDir = File(imagesDir, folderId)
        folderDir.mkdir()
        val imageBitmap = BitmapFactory.decodeFile(compressedImageFile.path) ?: return
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(File(folderDir, "$imageId.png"))
            if (!imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                throw IOException("Not able to save bitmap")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fileOutputStream?.close()
        }
    }

    fun getBitmapFromInternalStorage(
        context: Activity,
        folderId: String,
        imageId: String
    ): Bitmap? {
        val imagesDir = File(File(File(context.filesDir, "images"), folderId), "$imageId.png")
        return if (imagesDir.exists()) {
            BitmapFactory.decodeFile(imagesDir.path)
        } else {
            null
        }
    }

    fun getUriFromInternalStorage(
        context: Activity,
        folderId: String,
        imageId: String
    ): Uri? {
        val imagesDir = File(File(File(context.filesDir, "images"), folderId), "$imageId.png")
        return if (imagesDir.exists()) {
            imagesDir.toUri()
        } else {
            null
        }
    }

    fun getBitmapForFolderNName(
        context: Activity,
        folderId: String
    ) {
        val files = context.filesDir.listFiles()
        val imagesFile = File("/data/user/0/com.summer.ourdrive/files/images/cc6db0jo65u4h73gu1ug")

        imagesFile.listFiles()?.forEach {
            Log.d("getBitmapForFolderNName", it.absolutePath)
            Log.d("getBitmapForFolderNName", it.path)
            Log.d("getBitmapForFolderNName", it.canonicalPath)
            Log.d("getBitmapForFolderNName", it.length().toString())
        }
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun getBitmapFormUri(ac: Activity, uri: Uri?): Bitmap? {
        var input: InputStream? = ac.contentResolver.openInputStream(uri!!)
        val onlyBoundsOptions = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true
        onlyBoundsOptions.inDither = true //optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input?.close()
        val originalWidth = onlyBoundsOptions.outWidth
        val originalHeight = onlyBoundsOptions.outHeight
        if (originalWidth == -1 || originalHeight == -1) return null
        //Image resolution is based on 480x800
        val hh = 800f //The height is set as 800f here
        val ww = 480f //Set the width here to 480f
        //Zoom ratio. Because it is a fixed scale, only one data of height or width is used for calculation
        var be = 1 //be=1 means no scaling
        if (originalWidth > originalHeight && originalWidth > ww) { //If the width is large, scale according to the fixed size of the width
            be = (originalWidth / ww).toInt()
        } else if (originalWidth < originalHeight && originalHeight > hh) { //If the height is high, scale according to the fixed size of the width
            be = (originalHeight / hh).toInt()
        }
        if (be <= 0) be = 1
        //Proportional compression
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = be //Set scaling
        bitmapOptions.inDither = true //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
        input = ac.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
        input?.close()
        return bitmap //Mass compression again
    }

    /**
     * Mass compression method
     *
     * @param image
     * @return
     */
    /*private fun compressImage(image: Bitmap?): Bitmap? {
        if (image == null) return null
        val baos = ByteArrayOutputStream();
        image.compress(
            Bitmap.CompressFormat.PNG,
            100,
            baos
        )//Quality compression method, here 100 means no compression, store the compressed data in the BIOS
        var options = 100
        while (baos.toByteArray().size / 1024 > 100) {  //Cycle to determine if the compressed image is greater than 100kb, greater than continue compression
            baos.reset();//Reset the BIOS to clear it
            //First parameter: picture format, second parameter: picture quality, 100 is the highest, 0 is the worst, third parameter: save the compressed data stream
            image.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                baos
            );//Here, the compression options are used to store the compressed data in the BIOS
            options -= 10;//10 less each time
        }
        val isBm =
            ByteArrayInputStream(baos.toByteArray())//Store the compressed data in ByteArrayInputStream
        val bitmap = BitmapFactory.decodeStream(
            isBm,
            null,
            null
        );//Generate image from ByteArrayInputStream data
        return bitmap
    }*/

    const val WORLD_TIME_CLOCK_API = "http://worldtimeapi.org/"
    const val ENCODING_HEADER_GZIP = "gzip, deflate"
}
