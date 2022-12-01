fun main() {
    fun part1(input: List<String>): Int {
        var maxCalories = 0
        var currentCalories = 0
        input.forEach {
            if (it != "")
                currentCalories += it.toIntOrNull() ?: 0
            else {
                if (currentCalories > maxCalories)
                    maxCalories = currentCalories
                currentCalories = 0
            }
        }

        return maxCalories
    }

    fun part2(input: List<String>): Int {
        val sumOfCalories = mutableListOf<Int>()

        var currentCalories = 0
        input.forEach {
            if (it != "")
                currentCalories += it.toIntOrNull() ?: 0
            else {
                sumOfCalories.add(currentCalories)
                currentCalories = 0
            }
        }

        sumOfCalories.sortDescending()

        return ((sumOfCalories.getOrNull(0) ?: 0)
                + (sumOfCalories.getOrNull(1) ?: 0)
                + (sumOfCalories.getOrNull(2) ?: 0))
    }

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
