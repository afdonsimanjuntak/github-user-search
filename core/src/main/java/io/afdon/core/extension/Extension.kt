package io.afdon.core.extension

import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Job

fun Job?.cancelIfActive() {
    if (this?.isActive == true) cancel()
}

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext().applicationContext, message, Toast.LENGTH_SHORT).show()
}