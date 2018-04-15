package com.jonnyhsia.sample

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import androidx.content.edit
import com.jonnyhsia.core.ext.hideKeyboard
import com.jonnyhsia.core.ext.invokeKeyboard
import com.jonnyhsia.core.ext.like
import kotlinx.android.synthetic.main.activity_main.calendar
import kotlinx.android.synthetic.main.activity_main.inputText
import kotlinx.android.synthetic.main.activity_main.labelCalendar
import kotlinx.android.synthetic.main.activity_main.labelClick
import kotlinx.android.synthetic.main.activity_main.labelPreference
import kotlinx.android.synthetic.main.activity_main.labelSwitch
import kotlinx.android.synthetic.main.activity_main.layoutSubmit
import kotlinx.android.synthetic.main.activity_main.tvSubmit
import java.util.Date

class MainActivity : AppCompatActivity() {

    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // label switch
        labelSwitch.checkedChanges { _, isChecked ->
            labelCalendar.isEnabled = isChecked
            if (!isChecked && calendar.visibility == View.VISIBLE) {
                calendar.visibility = View.GONE
            }
        }

        // label calendar(toggle) & calendar
        val now = Date()
        calendar.apply {
            date = now.time
            visibility = View.GONE
        }
        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            labelCalendar.subLabel = "${year}年${month}月${dayOfMonth}日"
        }
        labelCalendar.apply {
            subLabel = now like "yyyy年MM月dd日"
            labelToggleChanges { _, isToggle ->
                calendar.visibility = if (isToggle) View.VISIBLE else View.GONE
            }
        }

        // label click
        labelClick.labelClicked {
            it.findViewById<TextView>(R.id.label_indicator).apply {
                text = "${++clickCount}"
                if (visibility == View.GONE) {
                    visibility = View.VISIBLE
                }
            }
        }

        // label with preference
        labelPreference.bindPreference("config", "text", "") { labelView, prefValue ->
            labelView.subLabel = prefValue
        }
        labelPreference.labelToggleChanges { labelView, isToggle ->
            if (isToggle) {
                inputText.requestFocus()
                invokeKeyboard()
            } else {
                hideKeyboard()
            }
            labelView.subLabel.let {
                inputText.setText(it)
                inputText.setSelection(it.length)
            }

            layoutSubmit.visibility = if (isToggle) View.VISIBLE else View.GONE
        }
        tvSubmit.setOnClickListener {
            getSharedPreferences("config", Context.MODE_PRIVATE).edit {
                putString("text", inputText.text.toString())
            }
            labelPreference.toggle()
        }
    }
}
