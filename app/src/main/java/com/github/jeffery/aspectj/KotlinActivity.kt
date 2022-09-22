package com.github.jeffery.aspectj

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * @author mxlei
 * @date   2022/9/22
 */
class KotlinActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_test).setOnClickListener(this)
    }

    private fun test(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    @Override
    override fun onClick(v: View?) {
        test("kotlin hello")
    }
}