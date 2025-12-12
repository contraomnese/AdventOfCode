package day9

import java.io.File
import kotlin.math.abs
import kotlin.system.measureTimeMillis

private const val HARD_LEVEL = false

fun main() {
    val time = measureTimeMillis {
        val file = File("src/day9/input.txt")
        try {
            val redQuads = file.readLines().map { it.split(",").map { it.toInt() } }
            val result = if (HARD_LEVEL) drawGreenQuads() else computeMaxAreaOfRedQuads(redQuads)
            println("Result: $result")
        } catch (e: Exception) {
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}

private typealias RedQuads =  List<List<Int>>

private fun computeMaxAreaOfRedQuads(redQuads: RedQuads): Long {
    var maxArea = 0L
    for (i in 0 until redQuads.size) {
        for (j in i + 1 until redQuads.size) {
            val area = (abs(redQuads[i].first() - redQuads[j].first()) + 1) *
                    (abs(redQuads[i].last() - redQuads[j].last()) + 1).toLong()
            if (area > maxArea) {
                maxArea = area
            }
        }
    }
    return maxArea
}

private fun drawGreenQuads() {

}

// 288461520 too low
// 4469004075 too low
// 4755064176