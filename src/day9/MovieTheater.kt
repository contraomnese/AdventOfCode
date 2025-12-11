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
            var maxArea = 0L
            for (i in 0 until redQuads.size) {
                for (j in i + 1 until redQuads.size) {
                    val newArea = (abs(redQuads[i].first() - redQuads[j].first()) + 1) *
                            (abs(redQuads[i].last() - redQuads[j].last()) + 1).toLong()
                    if (newArea > maxArea) {
                        maxArea = newArea
                    }
                }
            }
            println("Result: ${maxArea}")
        } catch (e: Exception) {
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}

// 288461520 too low
// 4469004075 too low
// 4755064176