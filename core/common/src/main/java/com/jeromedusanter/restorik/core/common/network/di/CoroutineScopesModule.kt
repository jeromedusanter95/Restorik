package com.jeromedusanter.restorik.core.common.network.di

import com.jeromedusanter.restorik.core.common.network.Dispatcher
import com.jeromedusanter.restorik.core.common.network.RestorikDispatchers.Default
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier for application-wide CoroutineScope.
 *
 * WHAT IS @ApplicationScope?
 * ===========================
 * This marks a CoroutineScope that lives for the entire lifetime of the app.
 * It's different from viewModelScope which is cancelled when ViewModel is cleared.
 *
 * WHEN TO USE:
 * ============
 * Use @ApplicationScope when you need operations that should:
 * - Survive configuration changes (screen rotation)
 * - Continue even after leaving a screen
 * - Live as long as the app process
 *
 * EXAMPLES:
 * - Syncing data in the background
 * - Monitoring network connectivity
 * - Managing app-wide caches
 * - Analytics/logging operations
 *
 * DON'T USE FOR:
 * - UI-related operations (use viewModelScope instead)
 * - Operations that should stop when user leaves a screen
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

/**
 * Hilt Module that provides an application-scoped CoroutineScope.
 *
 * WHAT DOES THIS PROVIDE?
 * ========================
 * A singleton CoroutineScope that:
 * 1. Lives for the entire app lifetime
 * 2. Uses Dispatchers.Default for CPU-bound work
 * 3. Uses SupervisorJob so one failure doesn't cancel everything
 *
 * WHY SupervisorJob?
 * ==================
 * SupervisorJob means if one child coroutine fails, others keep running.
 * Without it, if ANY coroutine in the scope throws an exception,
 * ALL other coroutines in that scope would be cancelled.
 *
 * Example:
 * scope.launch { doTask1() } // If this throws...
 * scope.launch { doTask2() } // ...this still continues with SupervisorJob
 *
 * WHY Dispatchers.Default?
 * =========================
 * The app scope is typically used for background processing, not UI work.
 * Default dispatcher is good for CPU-intensive background tasks.
 * If you need IO operations, you can still switch dispatchers:
 *
 * @Inject @ApplicationScope private val appScope: CoroutineScope
 * appScope.launch(Dispatchers.IO) { ... }
 *
 * EXAMPLE USAGE:
 * ==============
 * class DataSyncManager @Inject constructor(
 *     @ApplicationScope private val appScope: CoroutineScope,
 *     @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
 * ) {
 *     fun startPeriodicSync() {
 *         appScope.launch(ioDispatcher) {
 *             // This continues even if user navigates away
 *             syncDataFromServer()
 *         }
 *     }
 * }
 */
@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineScopesModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher(Default) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}
