package day7

import java.io.File
import kotlin.system.measureTimeMillis

private const val SPLITTER_CHAR = '^'

fun main() {
    val time = measureTimeMillis {
        val file = File("src/day7/input.txt")
        try {
            val content = file.readLines()
            val splitters = mutableListOf<List<Int>>()
            val startBeamIndexes = listOf(content.first().indexOf('S'))
            for (i in 2..<content.size) {
                if (i % 2 == 0) {
                    val splittersFormat =
                        content[i].mapIndexed { idx: Int, c: Char -> if (c == SPLITTER_CHAR) idx else 0 }.filter { it != 0 }
                    splitters.add(splittersFormat)
                } else continue
            }
            val sumOfSplit = splitters.runBeamHard(startBeamIndexes, node = Node(startBeamIndexes.first()))
            println("Result: $sumOfSplit")
        } catch (e: Exception) {
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}

private fun List<List<Int>>.runBeam(beamIndexes: List<Int>): Int {
    if (this.isEmpty()) return 0
    val newBeamIndexes = this[0].splitBeams(beamIndexes)
    println("Beams: ${newBeamIndexes.second}")
    return newBeamIndexes.first + this.drop(1).runBeam(newBeamIndexes.second)
}

private fun List<List<Int>>.runBeamHard(beamIndexes: List<Int>, node: Node): Int {
    if (this.size == 1) return node.visit()
    val newBeamIndexes = this[0].splitBeamsHard(beamIndexes, node)
    println("Beams: ${newBeamIndexes.second}")
    return this.drop(1).runBeamHard(newBeamIndexes.second, newBeamIndexes.first)
}

private fun List<Int>.splitBeams(beamIndexes: List<Int>): Pair<Int, List<Int>> {
    val newBeams = mutableSetOf<Int>()
    newBeams.addAll(beamIndexes)
    var splitCount = 0
    this.forEach {
        if (beamIndexes.contains(it)) {
            newBeams.remove(it)
            newBeams.addAll(it.splitBeam())
            splitCount++
        }
    }
    return splitCount to newBeams.toList()
}

private fun List<Int>.splitBeamsHard(beamIndexes: List<Int>, node: Node): Pair<Node, List<Int>> {
    val newBeams = mutableSetOf<Int>()
    newBeams.addAll(beamIndexes)
    this.forEach {
        if (beamIndexes.contains(it)) {
            newBeams.remove(it)
            newBeams.addAll(it.splitBeam())
        }
    }
    newBeams.forEach {
        node.insert(it)
    }

    return node to newBeams.toList()
}

private fun Int.splitBeam(): List<Int> {
    return listOf(this - 1, this + 1)
}

data class Node(val key: Int, var left: Node? = null, var right: Node? = null) {

    fun visit(): Int {
        val a = left?.visit() ?: 1
        val b = right?.visit() ?: 1
        return a + b
    }

    fun insert(value: Int) {

        if (value == this.key + 1) {
            if (this.right == null) {
                this.right = Node(value)
            } else {
                this.right!!.insert(value)
            }
        } else if (value == this.key - 1) {
            if (this.left == null) {
                this.left = Node(value)
            } else {
                this.left!!.insert(value)
            }
        }

        this.left?.insert(value)
        this.right?.insert(value)
    }
}

// 970 too low
// 1602