package com.windy.wechatfuck

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import com.windy.wechatfuck.hook.StepHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


/**
 * Created by su on 2017/12/29.
 */
class Main : IXposedHookLoadPackage {

    companion object {
        const val WECHAT_PACKAGE = "com.tencent.mm"
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        // Log.e("wechat-step", "handleLoadPackage: " + lpparam.packageName)
        if (lpparam.appInfo == null ||
                lpparam.appInfo.flags and (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return
        }

        val packageName = lpparam.packageName
        if (BuildConfig.APPLICATION_ID == packageName) {
            XposedHelpers.findAndHookMethod("com.windy.wechatfuck.activity.MainActivity",
                    lpparam.classLoader,
                    "showModuleActiveInfo", Boolean::class.javaPrimitiveType,
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            param.args[0] = true
                            super.beforeHookedMethod(param)
                        }
                    })
        }

        if (WECHAT_PACKAGE == packageName) {
            try {
                XposedHelpers.findAndHookMethod(Application::class.java,
                        "attach",
                        Context::class.java,
                        object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun afterHookedMethod(param: MethodHookParam) {
                                super.afterHookedMethod(param)
                                val context = param.args[0] as Context
                                val appClassLoader = context.classLoader
                                StepHook.hook(appClassLoader)
                            }
                        })
            } catch (e: Throwable) {
                XposedBridge.log(e)
            }
        }
    }

}