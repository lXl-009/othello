package com.lxl.othello.plus

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.lxl.othello.plus.update.InAppUpdate

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.main_new_game).setOnClickListener { LocalGameActivity.navigateFrom(this@MainActivity) }
        findViewById<TextView>(R.id.main_version).setText(BuildConfig.VERSION_CODE.toString())
    }

    private val mInAppUpdate by lazy {
        InAppUpdate(this)
    }

    override fun onResume() {
        super.onResume()
        mInAppUpdate.onResume()
    }
}