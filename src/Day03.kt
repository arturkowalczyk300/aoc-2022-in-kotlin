fun main() {
    fun calculateItemTypePriority(itemType: Char): Int {
        val charCode = itemType.code
        if (charCode in ('a'.code..'z'.code))
            return charCode - 'a'.code + 1
        else
            return charCode - 'A'.code + 27
        return charCode
    }

    fun part1(input: List<String>): Int {

        var prioritiesOfMutualElementInEveryRucksack = 0

        input.forEach {
            val compartments = it.chunked(it.length / 2)

            //find mutual element - assuming concept, there is only one
            val prioritiesOfCompartmentOne = compartments[0].toCharArray().map { char ->
                calculateItemTypePriority(char)
            }

            val prioritiesOfCompartmentTwo = compartments[1].toCharArray().map { char ->
                calculateItemTypePriority(char)
            }

            val mutualElementPriority = prioritiesOfCompartmentOne.intersect(prioritiesOfCompartmentTwo)
            prioritiesOfMutualElementInEveryRucksack += mutualElementPriority.toList()[0]
        }

        return prioritiesOfMutualElementInEveryRucksack
    }

    fun part2(input: List<String>): Int {
        //divide elves to groups of 3
        val groupsOf3 = input.chunked(3)
        var sumOfBadgeCodes = 0

        //find mutual element in every group (badge)
        groupsOf3.forEach {rucksackContent->
            val rucksack1 = rucksackContent[0].toCharArray().map { calculateItemTypePriority(it) }
            val rucksack2 = rucksackContent[1].toCharArray().map { calculateItemTypePriority(it) }
            val rucksack3 = rucksackContent[2].toCharArray().map { calculateItemTypePriority(it) }

            val badgeCode = (rucksack1.intersect(rucksack2).intersect(rucksack3)).toList()[0]
            sumOfBadgeCodes += badgeCode
        }

        return sumOfBadgeCodes
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)

    val input = readInput("Day03")
    println(part1(input))

    val testInputSecond = readInput("Day03_test_second")
    check(part2(testInputSecond) == 70)

    println(part2(input))
}
