package domain.service

import java.util.*


/**
 *
 *
 * @author Claudio E. de Oliveira<claudioe@ciandt.com>
 */
object LuckyNumberService {

    private val generator = Random()

    private fun newNumber(): Int = generator.nextInt(60) + 1

    fun luckyNumber(): List<Int> = (1..6).map { newNumber() }

}