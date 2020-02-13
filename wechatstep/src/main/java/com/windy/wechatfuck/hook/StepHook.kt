package com.windy.wechatfuck.hook

import android.util.Log
import com.windy.wechatfuck.utils.ConfigHelper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

/**
 * Created by su on 2018/02/07.
 * 运动步数
 */
object StepHook {
    private var isFakeStep = false
    private var nStepMagnification: String = "10"
    fun hook(classLoader: ClassLoader?) {
        try { //4.4 nexus 通过
            val clazz = XposedHelpers.findClass("android.hardware.SystemSensorManager\$SensorEventQueue", classLoader)
            XposedBridge.hookAllMethods(clazz, "dispatchSensorEvent", object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    readConfig()
                    // Log.e("wechat-step", "dispatchSensorEvent")
                    if (isFakeStep) {
                        (param.args[1] as FloatArray)[0] = (param.args[1] as FloatArray)[0] * Integer.valueOf(nStepMagnification)
                    }
                    super.beforeHookedMethod(param)
                }
            })
        } catch (e: Error) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readConfig() {
        val config = ConfigHelper()
        isFakeStep = config.get("is_step_hack")?.toBoolean() ?: true
        nStepMagnification = config.get("step_magnification") ?: "5"
    }
}