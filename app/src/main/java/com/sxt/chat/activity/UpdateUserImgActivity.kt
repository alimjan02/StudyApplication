package com.sxt.chat.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FetchUserInfoListener
import cn.bmob.v3.listener.UpdateListener
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.sxt.chat.App
import com.sxt.chat.R
import com.sxt.chat.ad.AdBannerActivity
import com.sxt.chat.db.User
import com.sxt.chat.dialog.AlertDialogBuilder
import com.sxt.chat.json.ResponseInfo
import com.sxt.chat.utils.Constants
import com.sxt.chat.utils.Prefs
import com.sxt.chat.utils.glide.GlideCircleTransformer
import com.sxt.chat.ws.BmobRequest

/**
 * Created by izhaohu on 2018/3/13.
 */

class UpdateUserImgActivity : AdBannerActivity(), View.OnClickListener {

    private var img: ImageView? = null
    private var bitmapUri: Uri? = null
    private val REQUEST_CHOOSE_PHOTO = 1000
    private val REQUEST_CROP_PHOTO = 1001
    private val CMD_UPLOAD_FILE = this.javaClass.name + "CMD_UPLOAD_FILE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user_img)
        img = findViewById(R.id.img)
        findViewById<View>(R.id.root).setOnClickListener(this)
        setTitle(R.string.header_img_update)
        updateHeadPortrait()
        val flag = Prefs.getInstance(this).getBoolean(Prefs.KEY_IS_SHOW_GOOGLE_AD, false)
        Log.e(TAG, "Google admob 显示状态 ：flag $flag")
        if (flag) {
            initGoogleAdBanner()
        } else {
            initTencentAdBanner2(Constants.BannerPosID_personal_profile)
        }
    }

    private fun updateHeadPortrait() {
        val user = BmobUser.getCurrentUser(User::class.java)
        if (user != null) {
            load(user.imgUri, if ("M" == BmobUser.getCurrentUser(User::class.java).gender) R.mipmap.men else R.mipmap.women)
        }
    }

    private fun updateUser(url: String) {
        val newUser = User()
        newUser.imgUri = url
        val bmobUser = BmobUser.getCurrentUser(User::class.java)
        newUser.update(bmobUser.objectId, object : UpdateListener() {
            override fun done(e: BmobException?) {
                if (e != null) {
                    loading.dismiss()
                    Toast("errorCode: " + e.errorCode + " , " + e.message)
                } else {
                    BmobUser.fetchUserJsonInfo(object : FetchUserInfoListener<String>() {
                        override fun done(s: String, e: BmobException?) {
                            loading.dismiss()
                            if (e == null) {
                                load(BmobUser.getCurrentUser(User::class.java).imgUri, if ("M" == BmobUser.getCurrentUser(User::class.java).gender) R.mipmap.men else R.mipmap.women)
                            } else {
                                Toast("errorCode: " + e.errorCode + " , " + e.message)
                            }
                        }
                    })
                }
            }
        })
    }

    private fun load(url: String, placeHolder: Int) {
        Glide.with(App.getCtx())
                .load(url)
                .error(placeHolder)
                .bitmapTransform(GlideCircleTransformer(App.getCtx()))
                //                .skipMemoryCache(true)//跳过内存
                //                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .signature(StringSignature(Prefs.getInstance(App.getCtx()).getString(Prefs.KEY_USER_HEADER_IMAGE_FLAG, "")))
                .into(img!!)
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
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CHOOSE_PHOTO) {
                startCropActivity(data!!.data)
            } else if (requestCode == REQUEST_CROP_PHOTO) {
                bitmapUri = data!!.getParcelableExtra(CropActivity.CROP_IMG_URI)
                if (bitmapUri != null) {
                    upload(bitmapUri!!)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun startCropActivity(uri: Uri?) {
        val intent = Intent(this, CropActivity::class.java)
        intent.putExtra(CropActivity.CROP_IMG_URI, uri)
        startActivityForResult(intent, REQUEST_CROP_PHOTO)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.root -> {
                val b = checkPermission(REQUEST_CHOOSE_PHOTO, Manifest.permission_group.STORAGE, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                if (b) {
                    startGalleryApp()
                }
            }
            else -> {
            }
        }
    }

    override fun onPermissionsAllowed(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults)
        startGalleryApp()
    }

    override fun onPermissionsRefusedNever(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults)
        if (REQUEST_CHOOSE_PHOTO == requestCode) {
            val appName = getString(R.string.app_name)
            val message = String.format(getString(R.string.permission_request_READ_EXTERNAL_STORAGE), appName)
            val span = SpannableString(message)
            span.setSpan(TextAppearanceSpan(this, R.style.text_color_2_15_style), 0, message.indexOf(appName), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val start = message.indexOf(appName) + appName.length
            span.setSpan(TextAppearanceSpan(this, R.style.text_color_1_17_bold_style), message.indexOf(appName), start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(TextAppearanceSpan(this, R.style.text_color_2_15_style), start, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            showPermissionRefusedNeverDialog(span)
        }
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

    private fun upload(bitmapUri: Uri) {
        loading.show()
        BmobRequest.getInstance(this).uploadFile(bitmapUri.path, CMD_UPLOAD_FILE)
    }

    override fun onMessage(resp: ResponseInfo) {
        if (ResponseInfo.OK == resp.code) {
            if (CMD_UPLOAD_FILE == resp.cmd) {
                Prefs.getInstance(this).putString(Prefs.KEY_USER_HEADER_IMAGE_FLAG, Prefs.getInstance(this).userId.toString() + "-" + System.currentTimeMillis())
                updateUser(resp.imgUrl)
            }
        } else {
            loading.dismiss()
            Toast(resp.error)
        }
    }

    override fun onResume() {
        super.onResume()
        updateHeadPortrait()
    }

    override fun onGoBack(view: View) {
        setResult(Activity.RESULT_OK)
        super.onGoBack(view)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}
