package com.sxt.chat.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.sxt.chat.App
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

class YouTuIdCardActivity : HeaderActivity(), View.OnClickListener {

    private var imgIdCard: ImageView? = null
    private var imgPlaceHolder: ImageView? = null
    private var statusTitle: TextView? = null
    private var resultTitle: TextView? = null
    private var resultValue: TextView? = null

    private var imgIdCard2: ImageView? = null
    private var imgPlaceHolder2: ImageView? = null
    private var statusTitle2: TextView? = null
    private var resultTitle2: TextView? = null
    private var resultValue2: TextView? = null

    private var next: TextView? = null
    private var SUCCESS: Boolean = false
    private var COMPLETE: Boolean = false
    private var UPLOAD_RE_TRY: Boolean = false
    private val REQUEST_CODE_GALLERY = 100
    private val REQUEST_CODE_CAMARER = 101
    private val REQUEST_CROP_PHOTO = 102
    private val REQUEST_CODE_TAKE_PHOTO = 103
    private var ocrTask: OCRTask? = null
    private var ocrResult: OCRObject? = null
    private var filePath: String? = null
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youto2)
        setTitle(R.string.ocr_scan_id_card)
        imgIdCard = findViewById(R.id.img_idcard)
        imgPlaceHolder = findViewById(R.id.img_placeHolder)
        statusTitle = findViewById(R.id.description)
        resultTitle = findViewById(R.id.result_title)
        resultValue = findViewById(R.id.result_value)

        imgIdCard2 = findViewById(R.id.img_idcard2)
        imgPlaceHolder2 = findViewById(R.id.img_placeHolder2)
        statusTitle2 = findViewById(R.id.description2)
        resultTitle2 = findViewById(R.id.result_title2)
        resultValue2 = findViewById(R.id.result_value2)

        next = findViewById(R.id.next)
        imgPlaceHolder!!.setOnClickListener(this)
        imgPlaceHolder2!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.img_placeHolder -> {
                startCamera(view)
                position = 0
            }

            R.id.img_placeHolder2 -> {
                startCamera(view)
                position = 1
            }
            R.id.next -> {
                if (UPLOAD_RE_TRY) {
                    startCardOCR(filePath)
                    return
                }
                if (COMPLETE) {
                    finish()
                    return
                }
                if (SUCCESS) {
                    if (ocrResult != null && !TextUtils.isEmpty(ocrResult!!.id) && !TextUtils.isEmpty(ocrResult!!.name)) {
                        loading.show()
                        //                        TJProtocol.getInstance(this).updateUserInfo(Prefs.getInstance(this).getInt(Prefs.KEY_USER_ID, 0),
                        //                                "", "", 0, ocrResult.getId(), ocrResult.getName(), CMD_UPDATE_USER_INFO);
                    }
                }
            }
        }
    }

    private fun startCardOCR(imgPath: String?) {
        val file = File(imgPath)
        if (file.exists()) {
            Glide.with(this).load(file)
                    .transform(CenterCrop(this), GlideRoundTransformer(this, 4))
                    /*.bitmapTransform(new GlideRoundTransformer(this, 8))*/
                    .error(R.mipmap.pic_ida)
                    .into(if (position == 0) imgIdCard else imgIdCard2)

            if (ocrTask != null && !ocrTask!!.isCancelled) {
                ocrTask!!.cancel(true)
            }
            ocrTask = OCRTask(imgPath, SDKConfig.TYPE_ID_CARD, position, object : OCRListener {
                override fun onStart() {
                    showDialog()
                }

                override fun onFaied(e: Exception) {
                    dismiss()
                    runOnUiThread {
                        val drawable = ContextCompat.getDrawable(App.getCtx(), R.mipmap.icon_failed)
                        drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                        if (position == 0) {
                            statusTitle!!.setCompoundDrawables(drawable, null, null, null)
                            statusTitle!!.text = "加载失败，重新上传"
                            //                            next.setText("重新上传");
                            //                            next.setEnabled(true);
                            resultTitle!!.text = ""
                            resultValue!!.text = ""
                            findViewById<View>(R.id.result_layout).visibility = View.INVISIBLE

                        } else if (position == 1) {

                            statusTitle2!!.setCompoundDrawables(drawable, null, null, null)
                            statusTitle2!!.text = "加载失败，重新上传"
                            resultTitle2!!.text = ""
                            resultValue2!!.text = ""
                            findViewById<View>(R.id.result_layout2).visibility = View.INVISIBLE

                        }
                        SUCCESS = false
                        COMPLETE = false
                        UPLOAD_RE_TRY = true
                        //                            next.setBackground(ContextCompat.getDrawable(App.getCtx(), R.drawable.btn_pressed_bg));
                    }
                }

                override fun onSuccess(ocrResult: OCRObject, result: String) {
                    dismiss()
                    this@YouTuIdCardActivity.ocrResult = ocrResult
                    if (OCRObject.SUCCESS == ocrResult.code) {

                        if (position == 0) {
                            if (!TextUtils.isEmpty(ocrResult.id) && !TextUtils.isEmpty(ocrResult.name)) {
                                val drawable = ContextCompat.getDrawable(App.getCtx(), R.mipmap.icon_complete)
                                drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                                statusTitle!!.setCompoundDrawables(drawable, null, null, null)
                                statusTitle!!.text = "识别成功"
                                resultTitle!!.text = "  姓名:" + ocrResult.name
                                resultValue!!.text = "身份证: " + ocrResult.id
                                findViewById<View>(R.id.result_layout).visibility = View.VISIBLE
                            } else {
                                failed()
                            }

                        } else if (position == 1) {
                            if (!TextUtils.isEmpty(ocrResult.authority) && !TextUtils.isEmpty(ocrResult.valid_date)) {
                                val drawable = ContextCompat.getDrawable(App.getCtx(), R.mipmap.icon_complete)
                                drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                                statusTitle2!!.setCompoundDrawables(drawable, null, null, null)
                                statusTitle2!!.text = "识别成功"
                                resultTitle2!!.text = "签发机关 : " + ocrResult.authority
                                resultValue2!!.text = "有效日期 : " + ocrResult.valid_date
                                findViewById<View>(R.id.result_layout2).visibility = View.VISIBLE
                            } else {
                                failed()
                            }
                        }
                        //                        next.setEnabled(true);
                        //                        next.setText(R.string.next);
                        //                        next.setBackground(ContextCompat.getDrawable(App.getCtx(), R.drawable.btn_pressed_bg));
                        SUCCESS = true
                        COMPLETE = false
                        UPLOAD_RE_TRY = false

                    } else {
                        failed()
                        SUCCESS = false
                        COMPLETE = false
                        UPLOAD_RE_TRY = false
                    }
                }

                private fun failed() {
                    val drawable = ContextCompat.getDrawable(App.getCtx(), R.mipmap.icon_failed)
                    drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    if (position == 0) {
                        statusTitle!!.setCompoundDrawables(drawable, null, null, null)
                        statusTitle!!.text = "识别失败，重新拍摄"
                        //                        next.setText(R.string.next);
                        //                        next.setEnabled(false);
                        //                        next.setBackground(ContextCompat.getDrawable(App.getCtx(), R.drawable.blue_gray_solid_round_25));
                        resultTitle!!.text = ""
                        resultValue!!.text = ""
                        findViewById<View>(R.id.result_layout).visibility = View.INVISIBLE

                    } else if (position == 1) {
                        statusTitle2!!.setCompoundDrawables(drawable, null, null, null)
                        statusTitle2!!.text = "识别失败，重新拍摄"
                        //                        next.setText(R.string.next);
                        //                        next.setEnabled(false);
                        //                        next.setBackground(ContextCompat.getDrawable(App.getCtx(), R.drawable.blue_gray_solid_round_25));
                        resultTitle2!!.text = ""
                        resultValue2!!.text = ""
                        findViewById<View>(R.id.result_layout2).visibility = View.INVISIBLE

                    }
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

    private fun startCropActivity(file: File) {
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
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (data != null) {
                val uri = data.getParcelableExtra<Uri>(CropActivity.CROP_IMG_URI)
                if (uri != null) {
                    val file = File(uri.path)
                    filePath = file.path
                    startCardOCR(filePath)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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
        super.onPermissionsRefused(requestCode, permissions, grantResults)
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
