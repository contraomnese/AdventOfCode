package day10

import java.io.File
import kotlin.system.measureTimeMillis

private const val HARD_LEVEL = false

fun main() {
    val time = measureTimeMillis {

        val file = File("src/day10/input.txt")
        val lights = file.readLines()
        var result = 0

        try {
            lights.forEach { lightData ->
                result += lightData.computeActions()
            }
            println("Result: $result")
        } catch (e: Exception) {
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}

private typealias LightData = String
private typealias LightSet = Set<Int>

private fun LightData.computeActions(): Int {
    val parts = this.split(' ').toMutableList()
    parts.removeLast()
    val lights = parts.removeFirst().toLights()

    val buttonActions = parts.map { it.toAction() }
    var currentStates: Set<LightSet> = setOf(emptySet())
    val visited: MutableSet<LightSet> = mutableSetOf(emptySet())
    var pressCount = 0

    while (true) {
        if (currentStates.isEmpty()) {
            throw IllegalStateException("Solution not found")
        }

        pressCount++
        val nextStates = mutableSetOf<LightSet>()

        for (currentState in currentStates) {
            for (action in buttonActions) {
                val newState = currentState symDiff action

                if (newState == lights) {
                    return pressCount
                }

                if (visited.add(newState)) {
                    nextStates.add(newState)
                }
            }
        }

        currentStates = nextStates
    }
}

private fun String.toLights(): Set<Int> {
    return this.removeSurrounding("[", "]")
        .mapIndexed { idx, char -> if (char == '#') idx else null }
        .filterNotNull()
        .toSet()
}

private fun String.toAction(): Set<Int> {
    return Regex("\\d+")
        .findAll(this)
        .map { it.value.toInt() }
        .toSet()
}

infix fun <T> Set<T>.symDiff(other: Set<T>): Set<T> {
    return (this union other) subtract (this intersect other)
}

// 523 too low
// 536 too low