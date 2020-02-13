package com.windy.wechatfuck.activity

import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import butterknife.ButterKnife
import com.jaeger.library.StatusBarUtil
import com.windy.wechatfuck.R

/**
 * Created by su on 2018/1/7.
 */
open class BaseActivity : AppCompatActivity() {
    var toolbar: Toolbar? = null
        private set

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initStatusBar()
        initToolbar()
        ButterKnife.bind(this)
    }

    /**
     * 初始化状态栏颜色
     * 更多详情https://github.com/laobie/StatusBarUtil
     */
    protected fun initStatusBar() {
        if (isStatusBarTransparent) {
            StatusBarUtil.setTransparent(this@BaseActivity)
            return
        }
        StatusBarUtil.setColor(this,
                ContextCompat.getColor(this, statusBarColor), 0)
    }

    /**
     * 状态栏是否透明
     *
     * @return 默认否
     */
    protected val isStatusBarTransparent: Boolean
        get() = false

    /**
     * 状态栏默认颜色,不支持动态修改
     *
     * @return 状态栏颜色
     */
    @get:ColorRes
    protected val statusBarColor: Int
        get() = R.color.status_bar

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        if (null == toolbar) {
            return
        }
        var mActivityInfo: ActivityInfo? = null
        try {
            mActivityInfo = packageManager.getActivityInfo(this.componentName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        // 在 AndroidManifest 中添加 android:label 设置默认标题栏
        if (mActivityInfo != null && 0 != mActivityInfo.labelRes) {
            toolbar!!.setTitle(mActivityInfo.labelRes)
        } else {
            toolbar!!.title = ""
        }
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar!!.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * 显示状态栏
     */
    fun showToolbar() {
        toolbar!!.visibility = View.VISIBLE
    }

    /**
     * 隐藏状态栏
     */
    fun hideToolbar() {
        toolbar!!.visibility = View.GONE
    }

    private var mProgressDialog: ProgressDialog? = null
    /**
     * 显示 progress dialog
     *
     * @param cancelable 点击空白处是否可以关闭
     */
    fun showProgressDialog(cancelable: Boolean) {
        showProgressDialog("请稍候...", cancelable)
    }
    /**
     * 显示 progress dialog
     *
     * @param text
     * @param cancelable
     */
    /**
     * 显示 progress dialog
     */
    @JvmOverloads
    fun showProgressDialog(text: String? = "请稍候...", cancelable: Boolean = false) {
        if (null == mProgressDialog) {
            mProgressDialog = ProgressDialog(this)
        }
        mProgressDialog!!.setMessage(text)
        mProgressDialog!!.setCancelable(cancelable)
        mProgressDialog!!.show()
    }

    /**
     * 关闭 progress dialog
     */
    fun dismissProgressDialog() {
        if (null != mProgressDialog && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    /***
     * 双击退出
     *
     * @param text
     */
    protected fun exitBy2Click(text: String?) {
        val secondTime = System.currentTimeMillis()
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            firstTime = secondTime
        } else {
            finish()
        }
    }

    private var firstTime: Long = 0
}