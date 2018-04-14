package com.jonnyhsia.labelview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.SwitchCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.content.edit
import junit.framework.TestSuite.warning
import kotlin.properties.Delegates

@Suppress("MemberVisibilityCanBePrivate", "unused")
class LabelView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        setUpLabelViewWithAttrs(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setUpLabelViewWithAttrs(context, attrs)
    }

    constructor(context: Context,
                attrs: AttributeSet?,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUpLabelViewWithAttrs(context, attrs)
    }

    private val tvLabel: TextView by lazy {
        addLabelElement(TextView(context), R.id.label)
    }

    private val tvSubLabel: TextView by lazy {
        addLabelElement(TextView(context), R.id.sub_label)
    }

    private val switchCompat: SwitchCompat by lazy {
        addLabelElement(SwitchCompat(context), R.id.label_switch)
    }

    private val imgStart: ImageView by lazy {
        addLabelElement(ImageView(context), R.id.label_icon_start)
    }

    private val imgEnd: ImageView by lazy {
        addLabelElement(ImageView(context), R.id.label_icon_end)
    }

    private var indicator: View? = null

    /**
     * label view 的 id
     */
    private var root: Int by Delegates.notNull()

    private val dp: Int = dp2px(1).toInt()

    private var labelType: Int by Delegates.notNull()

    var label: CharSequence
        get() = tvLabel.text
        set(value) {
            tvLabel.text = value
        }

    var subLabel: CharSequence
        get() = tvSubLabel.text
        set(value) {
            tvSubLabel.text = value
        }

    var isChecked: Boolean
        get() = switchCompat.isChecked
        set(value) {
            switchCompat.isChecked = value
        }

    private fun setUpLabelViewWithAttrs(context: Context, attrs: AttributeSet?) {
        attrs ?: return

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelView)

        val label = typedArray.getString(R.styleable.LabelView_label)
        val labelColor = typedArray.getColor(R.styleable.LabelView_labelTextColor, Color.parseColor("#DE000000"))
        val labelTextSize = typedArray.getFloat(R.styleable.LabelView_labelTextSize, 15f)
        val labelIconPadding = typedArray.getDimensionPixelSize(R.styleable.LabelView_labelIconPadding, 2 * dp)
        val labelIconSize = typedArray.getDimensionPixelSize(R.styleable.LabelView_labelIconSize, 24 * dp)
        val labelTypefaceRes = typedArray.getResourceId(R.styleable.LabelView_labelFontFamily, -1)

        val subLabel = typedArray.getString(R.styleable.LabelView_subLabel)
        val subLabelTextSize = typedArray.getFloat(R.styleable.LabelView_subLabelTextSize, 13f)
        val subLabelColor = typedArray.getColor(R.styleable.LabelView_labelTextColor, Color.parseColor("#61000000"))
        val subLabelIconPadding = typedArray.getDimensionPixelSize(R.styleable.LabelView_subLabelIconPadding, (8 * dp))
        val subLabelTypefaceRes = typedArray.getResourceId(R.styleable.LabelView_subLabelFontFamily, -1)

        val iconStartRes = typedArray.getResourceId(R.styleable.LabelView_iconStart, -1)
        val iconEndRes = typedArray.getResourceId(R.styleable.LabelView_iconEnd, -1)
        val iconEndSize = typedArray.getDimensionPixelSize(R.styleable.LabelView_iconEndSize, 16 * dp)

        val switchChecked = typedArray.getBoolean(R.styleable.LabelView_checked, false)
        labelType = typedArray.getInt(R.styleable.LabelView_labelType, TYPE_NORMAL)

        typedArray.recycle()

        val typefaceLabel: Typeface? = if (labelTypefaceRes != -1) {
            ResourcesCompat.getFont(context, labelTypefaceRes)
        } else {
            null
        }

        val typefaceSubLabel: Typeface? = if (subLabelTypefaceRes != -1) {
            ResourcesCompat.getFont(context, subLabelTypefaceRes)
        } else {
            null
        }

        // 若外部没有设置 ID, 则内部生成
        if (id == -1) {
            id = View.generateViewId()
        }
        root = id

        //  设置 start icon
        val haveStartIcon = iconStartRes != -1
        if (haveStartIcon) {
            imgStart.apply {
                loadImage(iconStartRes)
            }.setConstraintParams {
                width = labelIconSize
                height = labelIconSize
                startToStart = root
                topToTop = root
                bottomToBottom = root
                marginStart = MARGIN_STANDARD * dp
            }
        }

        // 设置 primary label
        tvLabel.apply {
            text = label ?: ""
            textSize = labelTextSize
            setTextColor(labelColor)
            typefaceLabel?.let { typeface = it }
        }.setConstraintParams {
            width = 0
            topToTop = root
            bottomToBottom = root

            if (haveStartIcon) {
                startToEnd = imgStart.id
                marginStart = labelIconPadding
            } else {
                startToStart = root
                marginStart = MARGIN_STANDARD * dp
            }
        }

        // TYPE_SWITCH 的 LabelView 是不含 end icon 的
        val haveEndIcon = iconEndRes != -1 && labelType != TYPE_SWITCH
        if (haveEndIcon) {
            imgEnd.apply {
                loadImage(iconEndRes)
            }.setConstraintParams {
                width = iconEndSize
                height = iconEndSize

                topToTop = root
                bottomToBottom = root
                endToEnd = root
                marginEnd = MARGIN_STANDARD * dp
            }
        }

        when (labelType) {
            TYPE_NORMAL -> {
                tvSubLabel.apply {
                    text = subLabel
                    textSize = subLabelTextSize
                    typefaceSubLabel?.let { typeface = it }
                    setTextColor(subLabelColor)
                }.setConstraintParams {
                    topToTop = root
                    bottomToBottom = root

                    if (haveEndIcon) {
                        endToStart = imgEnd.id
                        marginEnd = subLabelIconPadding
                    } else {
                        endToEnd = root
                        marginEnd = MARGIN_STANDARD * dp
                    }
                }
            }
            TYPE_SWITCH -> {
                switchCompat.apply {
                    isChecked = switchChecked
                    isClickable = false
                }.setConstraintParams {
                    topToTop = root
                    bottomToBottom = root
                    endToEnd = root
                    marginEnd = (MARGIN_STANDARD - 2) * dp
                }
            }
        }
    }

    /**
     * 设置 LabelView 的点击监听
     */
    fun labelClicked(listener: (labelView: LabelView) -> Unit) {
        if (labelType == TYPE_SWITCH) {
            warning("Switch 类型使用 LabelView#checkedChanges() 设置监听, 否则 Switch Button 不会响应点击作出状态更改")
        }
        super.setOnClickListener {
            listener(this)
        }
    }

    /**
     * 只有设置了监听, Switch 类型的 label 才能响应点击作出状态更改
     */
    fun checkedChanges(listener: (labelView: LabelView, isChecked: Boolean) -> Unit) {
        super.setOnClickListener {
            val boolean = if (labelType == TYPE_SWITCH) {
                switchCompat.toggle()
                isChecked
            } else {
                false
            }
            listener(this, boolean)
        }
    }

    fun bindPreference(
            prefName: String,
            prefKey: String,
            defaultValue: Boolean = false,
            mode: Int = Context.MODE_PRIVATE,
            listener: (labelView: LabelView, isChecked: Boolean) -> Unit = { _, _ -> }
    ) {
        val pref = context.getSharedPreferences(prefName, mode)
        isChecked = pref.getBoolean(prefKey, defaultValue)
        checkedChanges { labelView, isChecked ->
            listener(labelView, isChecked)
            pref.edit {
                putBoolean(prefKey, isChecked)
            }
        }
    }

    @Deprecated(message = "使用 labelClicked 或 checkedChanges 替代, 即使强行也没有效果", level = DeprecationLevel.HIDDEN)
    override fun setOnClickListener(l: OnClickListener) {
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)


    }

    /**
     * 显示/隐藏 Indicator
     * @param visibility 显示/隐藏
     */
    fun showIndicator(visibility: Int) {
        if (indicator == null) {
            indicator = findViewById(R.id.label_indicator)
            if (indicator == null) {
                Log.e(TAG, "Indicator == null, 检查是否在 xml 中设置 Indicator!")
                return
            }
        }
        indicator?.visibility = visibility
    }

    /**
     * 添加 View 到 LabelView 中并设置 ID
     * @param view 需要添加的 View
     * @param id   View 的 ID
     */
    private fun <T : View> addLabelElement(view: T, id: Int): T {
        view.id = id
        addView(view)
        return view
    }

    companion object {
        const val TAG = "LabelView"
        /** LabelView 的类型: 普通, 开关 */
        const val TYPE_NORMAL = 0
        const val TYPE_SWITCH = 1

        /** 标准边距: 20 个单位 */
        const val MARGIN_STANDARD = 20
    }

}

