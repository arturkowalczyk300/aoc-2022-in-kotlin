import java.util.Queue

fun main() {
    class Move(val howMuch: Int, val from: Int, to: Int)

    fun parseListOfMoves(input: String): List<Move> {
        //TODO: notImplemented()
    }

    fun parseHeapsOfCrates(input: String): List<Queue<Char>> {

    }

    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 1)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
