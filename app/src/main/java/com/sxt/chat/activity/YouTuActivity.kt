package com.sxt.chat.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.steelkiwi.cropiwa.util.UriUtil
import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.dialog.AlertDialogBuilder
import com.sxt.chat.json.OCRObject
import com.sxt.chat.utils.glide.GlideRoundTransformer
import com.sxt.chat.youtu.OCRListener
import com.sxt.chat.youtu.OCRTask
import com.sxt.chat.youtu.SDKConfig

import java.io.File

/**
 * Created by 11837 on 2018/6/5.
 */

class YouTuActivity : HeaderActivity() {

    private val REQUEST_CODE_GALLERY = 100
    private val REQUEST_CODE_CAMARER = 101
    private val REQUEST_CROP_PHOTO = 102
    private val REQUEST_CODE_TAKE_PHOTO = 103
    private val MAX_LENGTH = 2 * 1024 * 1024
    private var result: TextView? = null
    private var rgCardType: RadioGroup? = null
    private var ocrTask: OCRTask? = null
    private val file: File? = null

    private val cardType: String
        get() {
            return when {
                rgCardType!!.checkedRadioButtonId == R.id.radio_card_type_2 -> SDKConfig.TYPE_CREDIT_CARDCOR
                rgCardType!!.checkedRadioButtonId == R.id.radio_card_type_3 -> SDKConfig.TYPE_FOOD_CARDCOR
                rgCardType!!.checkedRadioButtonId == R.id.radio_card_type_4 -> SDKConfig.TYPE_IMAGE_CARDCOR
                else -> SDKConfig.TYPE_IMAGE_CARDCOR
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youto)
        setTitle(R.string.ocr_scan)
        result = findViewById(R.id.result)
        rgCardType = findViewById(R.id.radio_group_card_type)
    }

    private fun startCardOCR(imgPath: String?) {
        val file = File(imgPath)
        if (file.exists()) {
            Glide.with(this).load(file)/*.override(320, 198)*/
                    .transform(CenterCrop(this), GlideRoundTransformer(this, 4))
                    /*.bitmapTransform(new GlideRoundTransformer(this, 8))*/
                    .into(findViewById<View>(R.id.img) as ImageView)

            if (ocrTask != null && !ocrTask!!.isCancelled) {
                ocrTask!!.cancel(true)
            }
            ocrTask = OCRTask(imgPath, cardType, 0, object : OCRListener {
                override fun onStart() {
                    showDialog()
                }

                override fun onFaied(e: Exception) {
                    dismiss()
                    runOnUiThread { result!!.text = e.message }
                }

                override fun onSuccess(ocrResult: OCRObject, result: String) {
                    dismiss()
                    this@YouTuActivity.result!!.text = result
                }
            })
            ocrTask!!.execute()
        }
    }

    private fun showDialog() {
        runOnUiThread { loading.show() }
    }

    fun dismiss() {
        runOnUiThread {
            if (loading != null && loading.isShowing) {
                loading.dismiss()
            }
        }
    }

    fun startGallery(view: View) {
        val b = checkPermission(REQUEST_CODE_GALLERY, Manifest.permission_group.STORAGE, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        if (b) {
            startGalleryApp()
        }
    }

    fun startCamera(view: View) {
        val b = checkPermission(REQUEST_CODE_CAMARER, Manifest.permission_group.CAMERA, arrayOf(Manifest.permission.CAMERA))
        if (b) {
            startCameraApp()
        }
    }

    private fun startCameraApp() {
        startActivityForResult(Intent(this, TakePhotoActivity::class.java), REQUEST_CODE_TAKE_PHOTO)

        //        调用系统相机拍照
        //        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //        file = new File(this.getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".png");
        //
        //        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        //        //添加权限
        //        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //        startActivityForResult(intent, REQUEST_CODE_CAMARER);
    }

    private fun startGalleryApp() {
        var intent = Intent()
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.action = Intent.ACTION_GET_CONTENT
        } else {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
        }

        intent = Intent.createChooser(intent, "选择图片")
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    private fun startCropActivity(file: File?) {
        //在手机相册中显示刚拍摄的图片
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)

        val intent = Intent(this, CropActivity::class.java)
        intent.putExtra(CropActivity.CROP_IMG_URI, contentUri)
        startActivityForResult(intent, REQUEST_CROP_PHOTO)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                if (data != null && data.data != null) {
                    val path = UriUtil.uri2Path(this, data.data)
                    if (path != null) {
                        val file = File(path)
                        if (file.exists()) {
                            if (file.length() > MAX_LENGTH) {
                                startCropActivity(file)
                            } else {
                                startCardOCR(path)
                            }
                        }
                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMARER) {
                startCropActivity(file)
            } else if (requestCode == REQUEST_CROP_PHOTO) {
                if (data != null) {
                    val uri = data.getParcelableExtra<Uri>(CropActivity.CROP_IMG_URI)
                    if (uri != null) {
                        startCardOCR(UriUtil.uri2Path(this, uri))
                    }
                }
            } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                if (data != null) {
                    val uri = data.getParcelableExtra<Uri>(CropActivity.CROP_IMG_URI)
                    if (uri != null) {
                        val file = File(uri.path)
                        if (file.length() > MAX_LENGTH) {
                            startCropActivity(file)
                        } else {
                            startCardOCR(file.path)
                        }
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onPermissionsAllowed(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_CAMARER -> startCameraApp()
            REQUEST_CODE_GALLERY -> startGalleryApp()
        }
    }

    override fun onPermissionsRefusedNever(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_CAMARER -> onPermissionRefuseNever(R.string.permission_request_CAMERA)
            REQUEST_CODE_GALLERY -> onPermissionRefuseNever(R.string.permission_request_READ_EXTERNAL_STORAGE)
        }
    }

    private fun onPermissionRefuseNever(stringRes: Int) {
        val appName = getString(R.string.app_name)
        val message = String.format(getString(stringRes), appName)
        val span = SpannableString(message)
        span.setSpan(TextAppearanceSpan(this, R.style.text_color_2_15_style), 0, message.indexOf(appName), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val start = message.indexOf(appName) + appName.length
        span.setSpan(TextAppearanceSpan(this, R.style.text_color_1_17_bold_style), message.indexOf(appName), start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(TextAppearanceSpan(this, R.style.text_color_2_15_style), start, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        showPermissionRefusedNeverDialog(span)
    }

    /**
     * 权限被彻底禁止后 , 弹框提醒用户去开启
     */
    private fun showPermissionRefusedNeverDialog(message: CharSequence) {
        AlertDialogBuilder(this)
                .setTitle(R.string.message_alert, true)
                .setMessage(message)
                .setLeftButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setRightButton(R.string.confirm) { dialog, _ ->
                    dialog.dismiss()
                    goToAppSettingsPage()
                }
                .setShowLine(true)
                .setCanceledOnTouchOutside(false)
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ocrTask != null && !ocrTask!!.isCancelled) {
            ocrTask!!.cancel(true)
        }
    }
}
