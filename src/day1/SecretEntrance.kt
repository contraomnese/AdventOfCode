package day1

import java.io.File

fun main() {

    val file = File("src/day1/input.txt")
    val dial = Dial(50, true)
    println(dial.position)
    try {
        val content = file.readLines()
        content.forEach {
            it.action().invoke(dial)
        }
        println(dial.getSecretCode())
    } catch (e: Exception) {
        println("Error file: ${e.message}")
    }
}

class Dial(private val startPosition: Int, private val method0x434C49434B: Boolean = false) {
    
    private var code = 0

    private var _position = startPosition
        set(value) {
            require(value in MIN..MAX)
            if (value == 0) code++
            field = value
        }

    init {
        println("The dial starts by pointing at $position")
    }
    
    val position: Int
        get() = _position

    operator fun plus(turns: Int) {
        print("The dial is rotated R$turns to point at ")
        _position = (_position + turns.normalize()).normalize()
        println(if (position == 0) "[$position]." else "$position.")
    }

    operator fun minus(turns: Int) {
        print("The dial is rotated L$turns to point at ")
        _position = (_position - turns.normalize()).normalize()
        println(if (position == 0) "[$position]." else "$position.")
    }

    fun getSecretCode() = code

    private fun Int.normalize(): Int =

        when {
            madeFullCircleOfTheDial() -> 0
            madeLargerThenFullTurnedRight() -> {
                if (method0x434C49434B) code += (this / (MAX + 1)).coerceAtLeast(0)
                (this % (MAX + 1))
            }
            madeLargerThenFullTurnedLeft() -> {
                if (position != 0 && method0x434C49434B) code++
                (MAX + 1) + this
            }
            else -> this
        }

    private fun Int.madeLargerThenFullTurnedLeft() = this < MIN

    private fun Int.madeLargerThenFullTurnedRight() = this > (MAX + 1)

    private fun Int.madeFullCircleOfTheDial() = this == (MAX + 1)

    companion object {
        const val MIN = 0
        const val MAX = 99
    }
}

fun String.action(): (Dial) -> Unit =
    if (this.startsWith("R")) { dial: Dial -> dial + this.drop(1).toInt() }
    else { dial: Dial -> dial - this.drop(1).toInt() }

