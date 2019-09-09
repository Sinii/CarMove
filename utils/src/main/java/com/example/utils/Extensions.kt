package com.example.utils

import android.app.Activity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment



infix fun <A, B, C> Pair<A, B>.toTriple(that: C): Triple<A, B, C> = Triple(this.first, this.second, that)

/**
 * Log extensions
 * */

fun String?.dLog(): String? {
    if (BuildConfig.DEBUG) {
        Log.d("ExampleLog ", this ?: "")
    }
    return this
}

fun String?.eLog() {
    if (BuildConfig.DEBUG) {
        Log.e("ExampleLog ", this ?: "")
    }
}

/**
 * View utils
 */
fun Fragment.hideKeyboard() {
    val activity = this.requireActivity()
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = activity.findViewById(android.R.id.content)
    }
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}