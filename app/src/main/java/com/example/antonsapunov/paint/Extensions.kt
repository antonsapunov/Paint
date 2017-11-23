package com.example.antonsapunov.paint

import android.app.Activity
import android.support.annotation.IdRes
import android.util.Log
import android.view.View


    internal fun <T : View> Activity.bind(@IdRes id: Int): Lazy<T> {
        return lazy { findViewById<T>(id) }
    }

    internal fun log(any: Any?) = Log.d("log_tag", any.toString())
