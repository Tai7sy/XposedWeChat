package com.windy.wechatfuck.utils

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


class ConfigHelper {

    private var file: File? = null
    private var props: Properties

    constructor() {
        this.file = File(Environment.getExternalStorageDirectory().path, "wechat_step.ini")
        if (file != null && !file!!.exists()) {
            FileOutputStream(file).close()
        }
        props = Properties()
        props.clear()
        val fis = FileInputStream(file)
        props.load(fis)
        fis.close()
    }

    fun put(key: String?, value: String?) {
        Log.e("wechat-step", "ConfigHelper.put")
        val fos = FileOutputStream(file)
        props.setProperty(key, value)
        props.store(fos, "config")
        fos.close()
    }

    fun get(key: String): String? {
        return props.getProperty(key)
    }
}