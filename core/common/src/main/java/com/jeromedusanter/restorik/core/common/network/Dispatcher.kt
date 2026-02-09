package com.jeromedusanter.restorik.core.common.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val restorikDispatcher: RestorikDispatchers)

enum class RestorikDispatchers {
    Default,
    IO,
}