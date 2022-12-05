import java.util.LinkedList
import java.util.Queue

fun main() {
    class Move(val howMuch: Int, val from: Int, val to: Int) {
        override fun toString(): String {
            return "move ${howMuch} crates from ${from} to ${to}"
        }
    }

    fun parseListOfMoves(input: List<String>): List<Move> {
        val regexPattern = """move ([\d]+) from ([\d]+) to ([\d]+)"""
        val regex = Regex(regexPattern)
        val listOfMoves = mutableListOf<Move>()

        input.forEach {
            val found = regex.find(it)
            val matches = found!!.groupValues
            val move = Move(matches[1].toInt(), matches[2].toInt(), matches[3].toInt())
            listOfMoves.add(move)
        }
        return listOfMoves
    }

    fun parseHeapsOfCrates(heap: List<String>, numerationString: String): List<MutableList<Char>> {
        //find all numbers in numeration line - then get indices of columns where number are in heap string
        var numbers =
            numerationString.mapIndexed { index, it -> if (!" []".toCharArray().contains(it)) index else null }
        numbers = numbers.filterNotNull()
        val heapQueues = mutableListOf<LinkedList<Char>>()

        numbers.forEachIndexed { index, column ->

            heapQueues.add(LinkedList<Char>())

            for (i in heap.size - 1 downTo 0) {
                val heapQueue = heapQueues[index]
                if (heap[i].length > column && heap[i].get(column) != ' ')
                    heapQueue.add(heap[i].get(column))
            }
        }

        return heapQueues
    }

    fun move(heap: List<MutableList<Char>>, targetMove: Move): List<MutableList<Char>> {

        for (i in 1..targetMove.howMuch) {
            heap[targetMove.to - 1].add(heap[targetMove.from - 1].removeLast())
        }
        return heap
    }

    fun moveWithoutReorganisation(heap: List<MutableList<Char>>, targetMove: Move): List<MutableList<Char>> {
        val lastIndex = heap[targetMove.from - 1].lastIndex
        val elementsToRemove: List<Char> =
            heap[targetMove.from - 1].filterIndexed { index, it -> index in lastIndex - (targetMove.howMuch - 1)..lastIndex }

        elementsToRemove.forEach {
            heap[targetMove.from - 1].removeLast()
        }
        heap[targetMove.to - 1].addAll(elementsToRemove)
        println(heap.toString())
        println(targetMove.toString())
        return heap
    }

    fun getSymbolsOfTopCrates(heap: List<List<Char>>): String {
        val sb = StringBuilder()
        for (i in 0 until heap.size) {
            if (heap[i].size > 0)
                sb.append(heap[i].last())
        }
        return sb.toString()
    }

    fun part1(input: List<String>): String {
        var beginningOfMoveListIndex = -1

        input.forEachIndexed() { index, it ->
            if (it.contains("move") && beginningOfMoveListIndex == -1) //not found yet
                beginningOfMoveListIndex = index
        }

        var heapContent = input.take(beginningOfMoveListIndex).dropLast(1)
        var numerationLine = heapContent.takeLast(1)[0]
        heapContent = heapContent.dropLast(1)
        var listOfMoves = input.drop(beginningOfMoveListIndex)

        var heap = parseHeapsOfCrates(heapContent, numerationLine)
        val moves = parseListOfMoves(listOfMoves)
        moves.forEach { move ->
            heap = move(heap, move)
        }

        return getSymbolsOfTopCrates(heap)
    }

    fun part2(input: List<String>): String {
        var beginningOfMoveListIndex = -1

        input.forEachIndexed() { index, it ->
            if (it.contains("move") && beginningOfMoveListIndex == -1) //not found yet
                beginningOfMoveListIndex = index
        }

        var heapContent = input.take(beginningOfMoveListIndex).dropLast(1)
        var numerationLine = heapContent.takeLast(1)[0]
        heapContent = heapContent.dropLast(1)
        var listOfMoves = input.drop(beginningOfMoveListIndex)

        var heap = parseHeapsOfCrates(heapContent, numerationLine)
        val moves = parseListOfMoves(listOfMoves)
        moves.forEach { move ->
            heap = moveWithoutReorganisation(heap, move)
        }

        return getSymbolsOfTopCrates(heap)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")

    val input = readInput("Day05")
    println(part1(input))

    check(part2(testInput) == "MCD")
    println(part2(input))
}
