package com.jonnyhsia.labelview

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