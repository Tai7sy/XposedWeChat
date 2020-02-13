package com.windy.wechatfuck.utils

import android.content.Context
import android.content.pm.PackageManager

/**
 * Created by su on 2019/8/27.
 */
object AppUtil {
    fun getVersionName(context: Context, pkgName: String?): String {
        try {
            val packageManager = context.packageManager
            val packInfo = packageManager.getPackageInfo(pkgName, 0)
            return packInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }
}