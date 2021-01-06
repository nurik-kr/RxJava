package kg.nurik.rxjava.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.doAfterTextChanged(
    listener: (text: String) -> Unit
) {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            s?.let { listener.invoke(it.toString()) }
        }
    }
    addTextChangedListener(textWatcher)
}