package day5

import java.io.File
import kotlin.system.measureTimeMillis

private const val HARD_LEVEL = true

fun main() {
    val time = measureTimeMillis {
        val file = File("src/day5/input.txt")
        val content = file.readLines()
        var sumOfFreshIds = 0L

        val rangeIds = content.drop(1).takeWhile { it.isNotEmpty() }

        val ids = if (HARD_LEVEL) emptyList<Long>() else content.takeLastWhile { it.isNotEmpty() }.map { it.toLong() }

        val (firstStart, firstEnd) = content.first().split('-').map { it.toLong() }

        val freshIngredientsRangeIds = mutableListOf(
            LongRange(firstStart, firstEnd)
        )

        rangeIds.forEach { line ->
            val (start, end) = line.split('-').map { it.toLong() }
            freshIngredientsRangeIds.addRange(start, end)
        }

        ids.forEach { id ->
            if (ids.isNotEmpty()) {
                for (rangeIdx in freshIngredientsRangeIds.indices) {
                    val range = freshIngredientsRangeIds[rangeIdx]
                    if (id in range.start..range.end) {
                        sumOfFreshIds++
                        break
                    }
                }
            }

        }
        println("Result: ${
            if (HARD_LEVEL) freshIngredientsRangeIds.sumOf { it.end - it.start + 1 }
            else sumOfFreshIds
        }")
    }
    println("Running ${time}ms")
}

private fun MutableList<LongRange>.addRange(start: Long, end: Long): MutableList<LongRange> {
    require(this.isNotEmpty())

    if (this.contains(LongRange(start, end))) return this

    val nextRange = this.firstOrNull { it.start >= start } ?: this.firstOrNull { it.end > start } ?: this.first()
    val indexOfNextRange = this.indexOf(nextRange)
    val indexOfPreviousRange = indexOfNextRange - 1

    if (indexOfPreviousRange == -1) {
        if (nextRange.include(start)) {
            when {
                nextRange.include(end) -> Unit
                nextRange.notInclude(end) -> {
                    val find = this.firstOrNull { it.end >= end }
                    val newRange = LongRange(start, find?.end ?: end)
                    this[indexOfNextRange] = newRange
                    this.removeAll {
                        it.end <= newRange.end && it.start != newRange.start
                    }
                }
            }
        } else if (nextRange.notInclude(start)) {
            when {
                nextRange.include(end) -> nextRange.expandLeftward(start)
                nextRange.notInclude(end) -> {
                    if (end < nextRange.end) this.add(0, LongRange(start, end))
                    else this.add(LongRange(start, end))
                }
            }
        }
        return this
    }

    val prevRange = this[indexOfPreviousRange]

    if (prevRange.notInclude(start)) {
        when {
            nextRange.include(end) -> this[indexOfNextRange] = nextRange.expandLeftward(start)
            nextRange.include(start) -> this[indexOfNextRange] = nextRange.expandRightward(end) // ADDED
            nextRange.end < end -> {
                val newNextRange = this.firstOrNull { it.end >= end }

                if (newNextRange?.include(end) ?: false) {
                    val newRange = LongRange(start, newNextRange?.end ?:end)
                    this[indexOfNextRange] = newRange
                    this.removeAll {
                        it.start > newRange.start && it.start < newRange.end
                    }
                } else {
                    val newRange = LongRange(start, end)
                    this.add(indexOfNextRange, newRange)
                    this.removeAll {
                        it.start > newRange.start && it.start < newRange.end
                    }
                }
            }

            nextRange.notInclude(end) -> this.add(indexOfNextRange, LongRange(start, end))
        }
    } else {
        when {
            prevRange.include(end) -> Unit
            nextRange.include(end) -> {
                this[indexOfPreviousRange] = LongRange(prevRange.start, nextRange.end)
                this.removeAt(indexOfNextRange)
            }

            nextRange.end < end -> {
                val newNextRange = this.firstOrNull { it.end >= end }

                if (newNextRange?.include(end) ?: false) {
                    val newRange = LongRange(prevRange.start, newNextRange?.end ?: end)
                    this[indexOfPreviousRange] = newRange
                    this.removeAll {
                        it.start > newRange.start && it.start < newRange.end
                    }
                } else {
                    val newRange = LongRange(prevRange.start, end)
                    this[indexOfPreviousRange] = newRange
                    this.removeAll {
                        it.start > newRange.start && it.start < newRange.end
                    }
                }
            }
            nextRange.notInclude(end) -> {
                this[indexOfPreviousRange] = prevRange.expandRightward(end)
            }
        }
    }

    return this
}

private fun LongRange.expandLeftward(start: Long): LongRange = LongRange(start = start, endInclusive = endInclusive)

private fun LongRange.expandRightward(end: Long): LongRange = LongRange(start = start, endInclusive = end)

private fun LongRange.include(number: Long): Boolean = number in this

private fun LongRange.notInclude(number: Long): Boolean = number !in this


private val LongRange.end: Long
    get() = this.endInclusive

// 445171204094120 too high
// 364417170202703 too high
// 353863745078671 right