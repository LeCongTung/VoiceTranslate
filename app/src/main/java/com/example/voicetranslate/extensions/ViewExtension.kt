package com.example.voicetranslate.extensions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun showKeyboard(mEtSearch: EditText, context: Context) {
    mEtSearch.requestFocus()
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}
