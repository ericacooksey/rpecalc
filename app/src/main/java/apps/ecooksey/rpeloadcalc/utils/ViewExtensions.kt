package apps.ecooksey.rpeloadcalc.utils

import android.app.Activity
import android.databinding.BindingAdapter
import android.os.Build
import android.support.design.widget.TextInputLayout
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

fun Activity.hideSoftKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    // Find the currently focused view, so we can grab the correct window token from it.
    var view = this.currentFocus
    // If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view!!.windowToken, 0)
}

@BindingAdapter("app:errorText")
fun TextInputLayout.setErrorMsg(errorMsg: String?) {
    this.error = errorMsg
}

@BindingAdapter("app:linkedText")
fun TextView.setLinkedText(htmlText: String?) {
    // TODO open in chrome tab
    this.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlText)
    }
    this.movementMethod = LinkMovementMethod.getInstance()
}