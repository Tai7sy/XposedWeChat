package com.windy.wechatfuck.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.windy.wechatfuck.BuildConfig
import com.windy.wechatfuck.R
import com.windy.wechatfuck.utils.ConfigHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar?.navigationIcon = null
        showModuleActiveInfo(false)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            showPermissionDialog()
        } else {
            initView()
        }
    }


    private fun initView() {
        val config = ConfigHelper()
        isHideIcon.isChecked = config.get("is_hide_icon")?.toBoolean() ?: false
        isStepHack.isChecked = config.get("is_step_hack")?.toBoolean() ?: false
        stepMagnification.setText(config.get("step_magnification") ?: "5")

        isHideIcon.setOnCheckedChangeListener { _, isChecked ->
            hideLauncherIcon(isChecked)
            config.put("is_hide_icon", isChecked.toString())
        }
        isStepHack.setOnCheckedChangeListener { _, isChecked ->
            stepMagnification?.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            config.put("is_step_hack", isChecked.toString())
        }
        stepMagnification.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                config.put("step_magnification", s.toString())
            }
        })
    }

    private fun showPermissionDialog() {
        val alertDialog = AlertDialog.Builder(this)
                .setMessage("需要存储卡读写权限来保存应用配置")
                .setNegativeButton("不用了") { _, _ -> finish() }
                .setPositiveButton("好") { _, _ ->
                    ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            EXTERNAL_STORAGE_REQUEST_CODE)
                }
                .setCancelable(false)
                .create()
        alertDialog.show()
    }

    private fun hideLauncherIcon(isHide: Boolean) {
        val packageManager = this.packageManager
        val hide = if (isHide) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        packageManager.setComponentEnabledSetting(aliasComponentName, hide, PackageManager.DONT_KILL_APP)
    }

    private val aliasComponentName: ComponentName
        get() = ComponentName(this@MainActivity, "com.windy.wechatfuck.activity.MainActivity_Alias")

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_about) {
            showInfo()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun showInfo() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_about_content, null)
        val mTvVersionName = view.findViewById<TextView>(R.id.tv_version_name)
        val mTvInfo = view.findViewById<TextView>(R.id.tv_info)
        mTvVersionName.text = getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME
        mTvInfo.text = ("Thanks for wechatstep")
        val alertDialog = AlertDialog.Builder(this)
                .setTitle("关于")
                .setView(view)
                .create()
        alertDialog.show()
    }

    /**
     * 模块激活信息
     *
     * @param isModuleActive
     */
    @Suppress("SameParameterValue")
    private fun showModuleActiveInfo(isModuleActive: Boolean) {
        if (!isModuleActive) {
            Toast.makeText(this, "模块未激活", Toast.LENGTH_SHORT).show()
        } else {
            showInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initView()
        } else {
            finish()
        }
    }

    companion object {
        private const val EXTERNAL_STORAGE_REQUEST_CODE = 999
    }
}