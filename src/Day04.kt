fun main() {
    fun part1(input: List<String>): Int {
        //divide lines to ranges per elf
        var countRangeFullyContainsAnotherRange = 0

        input.forEach { line ->
            val ranges = line.split(',')
            val rangeStringElf1 = ranges[0].split('-')
            val rangeStringElf2 = ranges[1].split('-')
            val rangeElf1 = IntRange(rangeStringElf1[0].toInt(), rangeStringElf1[1].toInt())
            val rangeElf2 = IntRange(rangeStringElf2[0].toInt(), rangeStringElf2[1].toInt())

            if (rangeElf1.toList().containsAll(rangeElf2.toList())
                || rangeElf2.toList().containsAll(rangeElf1.toList())
            )
                countRangeFullyContainsAnotherRange++
        }
        return countRangeFullyContainsAnotherRange
    }

    fun part2(input: List<String>): Int {
        //divide lines to ranges per elf
        var countRangeContainsPartOfAnotherRange = 0

        input.forEach { line ->
            val ranges = line.split(',')
            val rangeStringElf1 = ranges[0].split('-')
            val rangeStringElf2 = ranges[1].split('-')
            val rangeElf1 = IntRange(rangeStringElf1[0].toInt(), rangeStringElf1[1].toInt())
            val rangeElf2 = IntRange(rangeStringElf2[0].toInt(), rangeStringElf2[1].toInt())

            if (rangeElf1.toList().intersect(rangeElf2.toList()).isNotEmpty())
                countRangeContainsPartOfAnotherRange++
        }
        return countRangeContainsPartOfAnotherRange
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)

    val input = readInput("Day04")
    println(part1(input))

    //second part
    check(part2(testInput) == 4)
    println(part2(input))
}
