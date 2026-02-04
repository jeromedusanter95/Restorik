package com.jeromedusanter.restorik.feature.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Combines 9 flows into a single flow.
 * Standard combine() only supports up to 5 parameters.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> combine(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    transform: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Flow<R> = combine(
    combine(flow1, flow2, flow3, flow4, flow5) { t1, t2, t3, t4, t5 ->
        CombineTuple5(t1, t2, t3, t4, t5)
    },
    combine(flow6, flow7, flow8, flow9) { t6, t7, t8, t9 ->
        CombineTuple4(t6, t7, t8, t9)
    }
) { tuple5, tuple4 ->
    transform(
        tuple5.t1,
        tuple5.t2,
        tuple5.t3,
        tuple5.t4,
        tuple5.t5,
        tuple4.t1,
        tuple4.t2,
        tuple4.t3,
        tuple4.t4
    )
}

private data class CombineTuple5<T1, T2, T3, T4, T5>(
    val t1: T1,
    val t2: T2,
    val t3: T3,
    val t4: T4,
    val t5: T5
)

private data class CombineTuple4<T1, T2, T3, T4>(
    val t1: T1,
    val t2: T2,
    val t3: T3,
    val t4: T4
)
