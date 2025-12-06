package day6

import java.io.File
import kotlin.system.measureTimeMillis

private const val HARD_LEVEL = false

fun main() {
    val time = measureTimeMillis {
        val file = File("src/day6/input.txt")
        try {
            val cashList = mutableListOf<Cash>()
            val content = file.readLines()
            cashList.handle(content[4])
            val result = if (HARD_LEVEL) cashList::computeHard else cashList::compute
            val sum = result(content[0], content[1], content[2], content[3]
            ).sum()
            println("Result: ${sum}")
        } catch (e: Exception) {
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}

data class Cash(val operation: Char, val capacity: Int = 0)

private fun MutableList<Cash>.handle(string: String): MutableList<Cash> {
    if (string.isEmpty()) return this
    var capacity = 0
    val dropOperation = string.drop(1)
    for (i in dropOperation.indices) {
        when (dropOperation[i]) {
            '+' -> break
            '*' -> break
            ' ' -> capacity++
            else -> throw IllegalArgumentException("Not support this char")
        }
    }
    this.add(Cash(operation = string.first(), capacity = capacity))
    return this.handle(dropOperation.substring(startIndex = capacity))
}

private fun MutableList<Cash>.compute(
    firstNumbers: String,
    secondNumbers: String,
    thirdNumbers: String,
    fourthNumbers: String
): List<Long> {
    val finals = mutableListOf<Long>()
    var capacity = 0
    this.forEach { cash ->
        val firstNumber = firstNumbers.drop(capacity).substring(0, cash.capacity).trim().toLong()
        val secondNumber = secondNumbers.drop(capacity).substring(0, cash.capacity).trim().toLong()
        val thirdNumber = thirdNumbers.drop(capacity).substring(0, cash.capacity).trim().toLong()
        val fourthNumber = fourthNumbers.drop(capacity).substring(0, cash.capacity).trim().toLong()
        val final = when(cash.operation) {
            '+' -> {
                print("$firstNumber + $secondNumber + $thirdNumber + $fourthNumber = ")
                firstNumber.plus(secondNumber).plus(thirdNumber).plus(fourthNumber)
            }
            '*' -> {
                print("$firstNumber * $secondNumber * $thirdNumber * $fourthNumber = ")
                firstNumber.times(secondNumber).times(thirdNumber).times(fourthNumber)
            }
            else -> throw IllegalArgumentException("Not support this char")
        }
        println(final)
        finals.add(final)
        capacity += cash.capacity + 1
    }
    return finals
}

private fun MutableList<Cash>.computeHard(
    firstNumbers: String,
    secondNumbers: String,
    thirdNumbers: String,
    fourthNumbers: String
): List<Long> {
    val finals = mutableListOf<Long>()
    var capacity = 0
    this.forEach { cash ->
        val firstNumber = firstNumbers.drop(capacity).substring(0, cash.capacity)
        val secondNumber = secondNumbers.drop(capacity).substring(0, cash.capacity)
        val thirdNumber = thirdNumbers.drop(capacity).substring(0, cash.capacity)
        val fourthNumber = fourthNumbers.drop(capacity).substring(0, cash.capacity)

        val reversedNumbers = mutableListOf<Long>()
        for (i in (cash.capacity - 1) downTo 0) {
            reversedNumbers.add((StringBuilder().append(firstNumber[i], secondNumber[i], thirdNumber[i], fourthNumber[i])).toString().trim().toLong())
        }

        val final = when(cash.operation) {
            '+' -> {
                reversedNumbers.forEachIndexed { idx, item ->
                    if (idx == reversedNumbers.size - 1) print("$item = ")
                    else print("$item + ")
                }
                reversedNumbers.sum()
            }
            '*' -> {
                reversedNumbers.forEachIndexed { idx, item ->
                    if (idx == reversedNumbers.size - 1) print("$item = ")
                    else print("$item * ")
                }
                reversedNumbers.fold(1L) { acc, item -> acc * item }
            }
            else -> throw IllegalArgumentException("Not support this char")
        }
        println(final)
        finals.add(final)
        capacity += cash.capacity + 1
    }
    return finals
}