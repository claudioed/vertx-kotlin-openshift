package domain.service

import java.util.concurrent.atomic.AtomicInteger

object NumberCounter {

    val numbers = mutableMapOf<Int, AtomicInteger>()

    init {
        (1..60).forEach { numbers[it] = AtomicInteger() }
    }

    fun count(number: Int) = numbers[number]?.addAndGet(1)

}