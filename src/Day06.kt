fun main() {
    fun rotateCharArrayLeft(arr: CharArray): CharArray {
        for (i in 0 until arr.size - 1) {
            arr[i] = arr[i + 1]
        }
        return arr
    }

    fun findIndexEndSequenceOfNDifferentCharacters(input: String, N:Int): Int {
        if (input.length < N) return -1

        var sequence = CharArray(N)
        var setSequence: Set<Char> = sequence.toSet()

        input.forEachIndexed() { index, it ->
            if (index >= N) {
                setSequence = sequence.toSet()
                if (setSequence.size == N) //set can't contain the same elements!
                    return index
                else {
                    rotateCharArrayLeft(sequence)
                    sequence[sequence.lastIndex] = it
                }
            }
            else
                sequence[index] = it
        }

        return -1 //not found
    }

    fun part1(input: String): Int {
        val charactersProcessed = findIndexEndSequenceOfNDifferentCharacters(input, 4)
        return charactersProcessed
    }

    fun part2(input: String): Int {
        val charactersProcessed = findIndexEndSequenceOfNDifferentCharacters(input, 14)
        return charactersProcessed
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput[0]) == 7)

    val input = readInput("Day06")
    println(part1(input[0]))
    println(part2(input[0]))
}
