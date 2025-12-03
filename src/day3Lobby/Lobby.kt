package day3Lobby

import java.io.File
import kotlin.system.measureTimeMillis

private const val HARD_LEVEL = false

fun main() {
    val time = measureTimeMillis {
        val action = if (HARD_LEVEL) {  } else {  }
        val file = File("src/day3Lobby/input.txt")
        try {
            val content = file.readLines()
            var sumOfJoltage = 0L
            content.forEach {
                sumOfJoltage += it.takeMaxJoltage(2)
            }
            println("Result: $sumOfJoltage")
        } catch (e: Exception) {
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}


private fun String.takeMaxJoltage(maxCount: Int): Long {
    val maxJoltage = this.takeMaxNumber(maxCount - 1)
    println("Max joltage is $maxJoltage")
    return maxJoltage.toLong()
}

private fun String.takeMaxNumber(maxCount: Int): String {
    if (maxCount == 0) return this.toSet().max().toString()
    val maxNumber = this.dropLast(maxCount).toSet().max().toString()
    return maxNumber + this.substringAfter(maxNumber).takeMaxNumber(maxCount - 1)
}