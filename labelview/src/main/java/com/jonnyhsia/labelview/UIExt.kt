package com.jonnyhsia.labelview

import android.content.SharedPreferences
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

internal fun View.dp2px(dp: Number): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
}

/**
 * 设置约束布局的布局参数
 * @param block 布局参数设置
 */
internal inline fun View.setConstraintParams(block: ConstraintLayout.LayoutParams.() -> Unit) {
    Log.d("setConstraintParams", "设置约束布局参数")
    val params = layoutParams as? ConstraintLayout.LayoutParams
            ?: throw IllegalStateException("LayoutParams 不正确")
    params.apply(block)
    layoutParams = params
}

/**
 * 根据 [View.isInEditMode] 设置图片加载的方式
 */
internal fun ImageView.loadImage(res: Int) {
    if (isInEditMode) {
        setImageResource(res)
    } else {
        Glide.with(this).load(res).into(this)
    }
}

internal fun <T> SharedPreferences.getValue(key: String, defaultValue: T): T {
    return when (defaultValue) {
        is Boolean -> getBoolean(key, defaultValue)
        is Int -> getInt(key, defaultValue)
        is Long -> getLong(key, defaultValue)
        is Float -> getFloat(key, defaultValue)
        is String -> getString(key, defaultValue)
        else -> throw IllegalStateException("不支持的 SharedPreference 类型")
    } as T
}