package day2

import java.io.File
import kotlin.system.measureTimeMillis

private const val HARD_LEVEL = false

fun main() {
    val time = measureTimeMillis {
        val action = if (HARD_LEVEL) { id: String -> id.isInvalidId() } else { id: String -> id.isInvalidIdForTwiceRepeat() }
        val file = File("src/day2/input.txt")
        try {
            val content = file.readText()
            var sumOfInvalidIds = 0L
            content.split(",").forEach { ids ->
                val firstId = ids.substringBefore("-").toLong()
                val secondId = ids.substringAfter("-").toLong()
                (firstId..secondId).forEach { id ->
                    if (action.invoke(id.toString())) {
                        sumOfInvalidIds += id
                    }
                }
            }
            println("Result: $sumOfInvalidIds")
        } catch (e: Exception) {
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}

private fun String.isInvalidIdForTwiceRepeat(): Boolean {

    if (hasOddLength()) return false

    val firstPart = substring(0, (length / 2))
    val secondPart = substring(length / 2)
    return firstPart == secondPart
}

private fun String.isInvalidId(): Boolean {


    if (length < 2) return false

    val availableCombinations = mutableListOf<String>()
    val currentCombination = StringBuilder().append(this.first())

    return if (hasRepeatedCombination(currentCombination.toString())) true
    else {
        while (currentCombination.length <= length / 2) {
            val endCombination = drop(currentCombination.length).substringBefore(currentCombination.toString(), "")

            if (endCombination.isEmpty()) currentCombination.append(currentCombination.toString())
            else currentCombination.append(endCombination)

            if (currentCombination.length > length / 2) break
            availableCombinations.add(currentCombination.toString())
        }

        availableCombinations.any {hasRepeatedCombination(it)}
    }
}

private fun String.hasRepeatedCombination(combination: String): Boolean {
    return if (isEmpty()) true
    else if (this == combination) true
    else {
        if (startsWith(combination)) drop(combination.length).hasRepeatedCombination(combination)
        else false
    }
}

private fun String.hasOddLength() = length % 2 != 0

// Number of times
// 40850068543
// 40850068498
// 41849350257
// 41849350212
// 41019520383
// 39021058293
// 45283684555 - right

// ~580 ms