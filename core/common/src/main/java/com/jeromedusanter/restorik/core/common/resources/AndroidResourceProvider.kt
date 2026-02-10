package com.jeromedusanter.restorik.core.common.resources

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidResourceProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ResourceProvider {

    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}
