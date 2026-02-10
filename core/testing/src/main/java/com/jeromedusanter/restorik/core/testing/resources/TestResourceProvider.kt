package com.jeromedusanter.restorik.core.testing.resources

import androidx.annotation.StringRes
import com.jeromedusanter.restorik.core.common.resources.ResourceProvider

class TestResourceProvider : ResourceProvider {

    override fun getString(@StringRes resId: Int): String {
        return "Test String"
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        // Format the arguments into the test string for better test clarity
        return if (formatArgs.isEmpty()) {
            "Test String"
        } else {
            "Test String: ${formatArgs.joinToString(", ")}"
        }
    }
}
