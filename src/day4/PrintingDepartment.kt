package day4


import java.io.File
import kotlin.system.measureTimeMillis

private const val HARD_LEVEL = true

fun main() {
    val time = measureTimeMillis {
        val file = File("src/day4/input.txt")
        try {
            val content = file.readLines()
            val paperRollsGrid = mutableListOf<List<Cell>>()
            content.forEach { lineOfSymbols ->
                paperRollsGrid.add(lineOfSymbols.toGridLine())
            }

            var sumOfPaperRollsLadedResult = 0

            while (true) {
                val paperRollsGridForLadingProcess = paperRollsGrid.toList()
                var sumOfPaperRollsLaded = 0

                for (i in paperRollsGridForLadingProcess.indices) {
                    val (paperRollsGridLineAfterLading, paperRollsThatLaded) = startToLadingPaperRolls(
                        prevLine = paperRollsGridForLadingProcess.getOrNull(i - 1),
                        currentLine = paperRollsGridForLadingProcess[i],
                        nextLine = paperRollsGridForLadingProcess.getOrNull(i + 1)
                    )
                    sumOfPaperRollsLaded += paperRollsThatLaded
                    paperRollsGrid[i] = paperRollsGridLineAfterLading
                }

                if (sumOfPaperRollsLaded == 0) break
                sumOfPaperRollsLadedResult += sumOfPaperRollsLaded
                if (!HARD_LEVEL) break
            }

            println("Result: $sumOfPaperRollsLadedResult")
        } catch (e: Exception) {
            println("Error file: ${e.message}")
        }
    }
    println("Running ${time}ms")
}

private fun String.toGridLine(): List<Cell> {
    val cells = mutableListOf<Cell>()

    for (placeIndex in indices) {
        cells.add(
            Cell(
                current = this[placeIndex].isPaperRoll(),
                left = this.getOrNull(placeIndex - 1)?.isPaperRoll() ?: 0,
                right = this.getOrNull(placeIndex + 1)?.isPaperRoll() ?: 0,
            )
        )
    }
    return cells
}

private fun startToLadingPaperRolls(
    prevLine: List<Cell>? = null,
    currentLine: List<Cell>,
    nextLine: List<Cell>? = null,
    limit: Int = 4
): Pair<List<Cell>, Int> {

    var sumOfPaperRollsThatWillBeLading = 0
    val gridLineAfterLading = currentLine.toMutableList()

    currentLine.forEachIndexed { idx, place ->
        if (place.hasPaperRoll()) {
            val upperCell = prevLine?.get(idx) ?: Cell()
            val downCell = nextLine?.get(idx) ?: Cell()
            if ((place.computeJustNeighbours() + upperCell.computeWithYourSelf() + downCell.computeWithYourSelf()) < limit) {
                sumOfPaperRollsThatWillBeLading++
                gridLineAfterLading[idx] = gridLineAfterLading[idx].copy(current = 0)
                val left = gridLineAfterLading.getOrNull(idx - 1)
                left?.let {
                    gridLineAfterLading[idx - 1] = gridLineAfterLading[idx - 1].copy(right = 0)
                }
                val right = gridLineAfterLading.getOrNull(idx + 1)
                right?.let {
                    gridLineAfterLading[idx + 1] = gridLineAfterLading[idx + 1].copy(left = 0)
                }
            }
        }
    }
    return gridLineAfterLading to sumOfPaperRollsThatWillBeLading
}

private fun Cell.hasPaperRoll() = this.current == 1

private fun Char.isPaperRoll(): Int = if (this == '@') 1 else 0

data class Cell(
    val current: Int = 0,
    val left: Int = 0,
    val right: Int = 0
) {
    fun computeJustNeighbours() = left + right
    fun computeWithYourSelf() = current + left + right
}