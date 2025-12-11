package day7

import java.io.File
import java.io.FileWriter
import kotlin.system.measureTimeMillis

private const val SPLITTER_CHAR = '^'
private const val BEAM_CHAR = '|'
private const val VOID_CHAR = '.'
private const val WIDTH_GRID = 140
private const val HARD_LEVEL = false

fun main() {
    val time = measureTimeMillis {
        val file = File("src/day7/input.txt")
        try {
            val content = file.readLines()
            val splitters = mutableListOf<List<Int>>()
            val startBeamIndex = listOf(content.first().indexOf('S'))
            val output = FileWriter("src/day7/output.txt", true)
            output.use { writer ->
                writer.write(content.first())
                writer.append("\n")
                for (i in 0..WIDTH_GRID) {
                    if (i == startBeamIndex.first()) writer.append(BEAM_CHAR)
                    else writer.append(VOID_CHAR)
                }
                writer.append("\n")
            }
            for (i in 2..<content.size) {
                if (i % 2 == 0) {
                    val splittersFormat =
                        content[i].mapIndexed { idx: Int, c: Char -> if (c == SPLITTER_CHAR) idx else 0 }.filter { it != 0 }
                    splitters.add(splittersFormat)
                } else continue
            }
            val sumOfSplit = splitters.runBeam(startBeamIndex)
            println("Result: $sumOfSplit")
            if (HARD_LEVEL) computeAllOutcomes(startBeamIndex.first())
        } catch (e: Exception) {
            throw e
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}

private typealias Emitter = Int
private typealias Splitters = List<Int>
private typealias Beams = Pair<Int, List<Int>>

private fun List<Splitters>.runBeam(emitters: List<Emitter>): Int {
    if (this.isEmpty()) return 0
    val beams = this[0].splitBeams(emitters)

    val output = FileWriter("src/day7/output.txt", true)

    output.use { writer ->
        for (i in 0..WIDTH_GRID) {
            if (this[0].contains(i)) writer.append(SPLITTER_CHAR)
            else if (beams.second.contains(i)) writer.append(BEAM_CHAR)
            else writer.append(VOID_CHAR)
        }
        writer.append("\n")
        for (i in 0..WIDTH_GRID) {
            if (this[0].contains(i)) writer.append(VOID_CHAR)
            else if (beams.second.contains(i)) writer.append(BEAM_CHAR)
            else writer.append(VOID_CHAR)
        }
        writer.append("\n")
    }
    return beams.first + this.drop(1).runBeam(beams.second)
}

private fun computeAllOutcomes(startBeamIndex: Int) {
    val file = File("src/day7/output.txt")
    val content = file.readLines().drop(1)
    var outcomes = mapOf(startBeamIndex to 1L)
    content.forEachIndexed { index, string ->
        if (index % 2 != 0) {
            val newOutcomes = mutableMapOf<Int, Long>()
            string.forEachIndexed { idx, char ->
                if (char == BEAM_CHAR) {
                    if (idx == 0) {
                        if (
                            string[idx + 1] == SPLITTER_CHAR && content[index - 1][idx + 1] == BEAM_CHAR
                        ) newOutcomes[idx] = outcomes.getValue(idx + 1)
                    } else if (idx == WIDTH_GRID) {
                        if (
                            string[idx - 1] == SPLITTER_CHAR && content[index - 1][idx - 1] == BEAM_CHAR
                        ) newOutcomes[idx] = outcomes.getValue(idx - 1)
                    } else {
                        if (
                            string[idx + 1] == SPLITTER_CHAR && content[index - 1][idx + 1] == BEAM_CHAR &&
                            string[idx - 1] == SPLITTER_CHAR && content[index - 1][idx - 1] == BEAM_CHAR &&
                            content[index - 1][idx] == BEAM_CHAR
                        ) newOutcomes[idx] = outcomes.getValue(idx - 1) + outcomes.getValue(idx) + outcomes.getValue(idx + 1)
                        else if (
                            string[idx + 1] == SPLITTER_CHAR && content[index - 1][idx + 1] == BEAM_CHAR &&
                            string[idx - 1] == SPLITTER_CHAR && content[index - 1][idx - 1] == BEAM_CHAR
                        ) newOutcomes[idx] = outcomes.getValue(idx - 1) + outcomes.getValue(idx + 1)
                        else if (
                            string[idx + 1] == SPLITTER_CHAR && content[index - 1][idx + 1] == BEAM_CHAR &&
                            content[index - 1][idx] == BEAM_CHAR
                        ) newOutcomes[idx] = outcomes.getValue(idx) + outcomes.getValue(idx + 1)
                        else if (
                            string[idx - 1] == SPLITTER_CHAR && content[index - 1][idx - 1] == BEAM_CHAR &&
                            content[index - 1][idx] == BEAM_CHAR
                        ) newOutcomes[idx] = outcomes.getValue(idx - 1) + outcomes.getValue(idx)
                        else if (
                            string[idx + 1] == SPLITTER_CHAR && content[index - 1][idx + 1] == BEAM_CHAR
                        ) newOutcomes[idx] = outcomes.getValue(idx + 1)
                        else if (
                            string[idx - 1] == SPLITTER_CHAR && content[index - 1][idx - 1] == BEAM_CHAR
                        ) newOutcomes[idx] = outcomes.getValue(idx - 1)
                        else if (
                            content[index - 1][idx] == BEAM_CHAR
                        ) newOutcomes[idx] = outcomes.getValue(idx)
                    }
                }
            }
            outcomes = newOutcomes.toMap()
        }
    }
    println("Result hard: ${outcomes.map { it.value }.sum()}")
}

private fun Splitters.splitBeams(emitters: List<Emitter>): Beams {
    val beams = mutableSetOf<Int>()
    beams.addAll(emitters)
    var splitBeamsCount = 0
    this.forEach {
        if (emitters.contains(it)) {
            beams.remove(it)
            beams.addAll(it.splitBeam())
            splitBeamsCount++
        }
    }
    return splitBeamsCount to beams.toList()
}

private fun Int.splitBeam(): List<Int> {
    return listOf(this - 1, this + 1)
}

// 970 too low
// 1602