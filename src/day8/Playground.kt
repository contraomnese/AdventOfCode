package day8

import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

private const val CONNECT_MAX_COUNT = 10
private const val HARD_LEVEL = true

fun main() {
    val time = measureTimeMillis {
        val file = File("src/day8/input.txt")
        try {
            val junctionBoxes: List<JunctionBoxCoordinates> = file.readLines().map {
                val (x, y, z) = it.split(",").map { it.toInt() }
                Triple(x, y, z)
            }
            val minDistances = mutableListOf<List<MinDistance>>()
            junctionBoxes.forEachIndexed { index, triple ->
                minDistances.add(triple.computeMinDistances(index, junctionBoxes))
            }
            val sortedMinDistances = minDistances.flatten().sortedByDescending { it.second }.toMutableList()
            val firstConnect = sortedMinDistances.removeLast()
            val connects = mutableListOf(firstConnect.first.toList().toMutableSet())
            var connectsMaxCount = 1
            var lastConnects = firstConnect.first
            while (if (HARD_LEVEL) junctionBoxes.size != connects.first().size else connectsMaxCount < CONNECT_MAX_COUNT) {
                val minDistance = sortedMinDistances.removeLast()
                lastConnects = minDistance.first
                var connectionNotFound = true
                for (i in connects.indices) {
                    if (connects[i].contains(minDistance.first.first) && connects[i].contains(minDistance.first.second)) {
                        connectsMaxCount++
                        connectionNotFound = false
                        break
                    } else if (connects[i].contains(minDistance.first.first)) {
                        connects[i].add(minDistance.first.second)
                        val hasThisJunction =
                            connects.mapIndexed { index, ints -> if (ints.contains(minDistance.first.second)) index else null }
                                .filterNotNull().sortedDescending()
                        hasThisJunction.drop(1).forEach { idx ->
                            connects[hasThisJunction.first()].addAll(connects.removeAt(idx))
                        }
                        connectsMaxCount++
                        connectionNotFound = false
                        break
                    } else if (connects[i].contains(minDistance.first.second)) {
                        connects[i].add(minDistance.first.first)
                        val hasThisJunction =
                            connects.mapIndexed { index, ints -> if (ints.contains(minDistance.first.first)) index else null }
                                .filterNotNull().sortedDescending()
                        hasThisJunction.drop(1).forEach { idx ->
                            connects[hasThisJunction.first()].addAll(connects.removeAt(idx))
                        }
                        connectsMaxCount++
                        connectionNotFound = false
                        break
                    } else continue
                }
                if (connectionNotFound) {
                    connects.add(minDistance.first.toList().toMutableSet())
                    connectsMaxCount++
                }
            }
            val maxConnectionsJunctions = connects.map { it.size }.sortedDescending()
            val result =
                if (HARD_LEVEL) (junctionBoxes[lastConnects.first].first).times(junctionBoxes[lastConnects.second].first)
                else maxConnectionsJunctions.take(3).reduce { acc, num -> acc * num }
            println("Result: ${result}")

        } catch (e: Exception) {
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}

private typealias MinDistance = Pair<JunctionBoxIndexes, Distance>
private typealias JunctionBoxIndexes = Pair<Int, Int>
private typealias Distance = Double
private typealias JunctionBoxCoordinates = Triple<Int, Int, Int>

private fun JunctionBoxCoordinates.computeMinDistances(destinationIndex: Int, boxes: List<JunctionBoxCoordinates>): List<MinDistance> {
    val minDistances = mutableListOf<MinDistance>()
    boxes.forEachIndexed { pointIdx, triple ->
        if (pointIdx > destinationIndex) {
            minDistances.add((destinationIndex to pointIdx) to (this to triple).distance())
        }
    }
    return minDistances
}

private fun Pair<JunctionBoxCoordinates, JunctionBoxCoordinates>.distance(): Distance =
    sqrt(
        (this.second.first - this.first.first).toDouble().pow(2) +
                (this.second.second - this.first.second).toDouble().pow(2) +
                (this.second.third - this.first.third).toDouble().pow(2)
    )

// 33640 too low
// 3129120 too high
// 37200 too low
// 96672
