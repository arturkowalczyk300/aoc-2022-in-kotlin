import java.util.*

enum class DIRECTION {
    TOP_TO_BOTTOM,
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    BOTTOM_TO_TOP
}

fun printlnArray(arr: Array<BooleanArray>) {
    val string = Arrays.deepToString(arr)
        .replace("],", ",\n") //print every row in separate line
        .replace("false", "0")
        .replace("true", "1")
        .replace(" ", "")
    println("${string}\n")
}

fun changeValueOfMapArrayBorders(arr: Array<BooleanArray>, newValue: Boolean): Array<BooleanArray> {
    assert(arr.size >= 2)
    assert(arr.first().size >= 2)

    for (i in arr.indices) { //vertical borders
        arr[i][0] = newValue
        arr[i][arr[i].lastIndex] = newValue

        if (i == 0 || i == arr.lastIndex) {
            for (k in arr.first().indices)
                arr[i][k] = newValue
        }
    }

    return arr
}

fun main() {

    fun createMapOfVisibilityFromGivenVerticalSide(
        treesTopViewMap: Array<IntArray>,
        visibilityDirection: DIRECTION
    ): Array<BooleanArray> {

        val visibilityMap = Array(treesTopViewMap.size) { BooleanArray(treesTopViewMap.first().size) { false } }
        val columnIterationRange: IntProgression =
            when (visibilityDirection) {
                DIRECTION.TOP_TO_BOTTOM -> treesTopViewMap.first().indices
                else -> treesTopViewMap.first().indices.reversed() //bottom to top
            }

        for (columnIndex in columnIterationRange) {
            var maximum = -1

            val rowIterationRange: IntProgression = when (visibilityDirection) {
                DIRECTION.TOP_TO_BOTTOM -> treesTopViewMap.indices //columns
                else -> treesTopViewMap.indices.reversed() //bottom to top
            }

            for (rowIndex in rowIterationRange) {
                val itemInRow = treesTopViewMap[rowIndex][columnIndex]

                if (itemInRow > maximum) {
                    visibilityMap[rowIndex][columnIndex] = true
                    maximum = itemInRow
                }
            }
        }

        return visibilityMap
    }

    fun createMapOfVisibilityFromGivenHorizontalSide(
        treesTopViewMap: Array<IntArray>,
        visibilityDirection: DIRECTION
    ): Array<BooleanArray> {
        val visibilityMap = Array(treesTopViewMap.size) { BooleanArray(treesTopViewMap.first().size) { false } }
        val rowsIterationRange: IntProgression =
            when (visibilityDirection) {
                DIRECTION.LEFT_TO_RIGHT -> treesTopViewMap.indices //columns
                else -> treesTopViewMap.indices.reversed() //right to left
            }

        for (rowIndex in rowsIterationRange) {
            var maximum = -1

            val columnsIterationRange: IntProgression = when (visibilityDirection) {
                DIRECTION.LEFT_TO_RIGHT -> treesTopViewMap.first().indices //rows
                else -> treesTopViewMap.first().indices.reversed() //right to left
            }

            for (columnIndex in columnsIterationRange) {
                val itemInRow = treesTopViewMap[rowIndex][columnIndex]

                if (itemInRow > maximum) {
                    visibilityMap[rowIndex][columnIndex] = true
                    maximum = itemInRow
                }
            }
        }

        return visibilityMap
    }

    fun createMapOfVisibilityFromGivenSide(
        treesTopViewMap: Array<IntArray>,
        visibilityDirection: DIRECTION
    ): Array<BooleanArray> {
        return when (visibilityDirection) {
            DIRECTION.LEFT_TO_RIGHT, DIRECTION.RIGHT_TO_LEFT -> createMapOfVisibilityFromGivenHorizontalSide(
                treesTopViewMap,
                visibilityDirection
            )
            else -> createMapOfVisibilityFromGivenVerticalSide(treesTopViewMap, visibilityDirection)
        }
    }

    fun createMapOfVisibility(treesTopViewMap: Array<IntArray>): Array<BooleanArray> {
        var visibilityMap = Array(treesTopViewMap.size) { BooleanArray(treesTopViewMap.first().size) { false } }

        val mapFromLeftSide =
            createMapOfVisibilityFromGivenSide(treesTopViewMap, DIRECTION.LEFT_TO_RIGHT)
        val mapFromTopSide =
            createMapOfVisibilityFromGivenSide(treesTopViewMap, DIRECTION.TOP_TO_BOTTOM)
        val mapFromRightSide =
            createMapOfVisibilityFromGivenSide(treesTopViewMap, DIRECTION.RIGHT_TO_LEFT)
        val mapFromBottomSide =
            createMapOfVisibilityFromGivenSide(treesTopViewMap, DIRECTION.BOTTOM_TO_TOP)

        for (rowIndex in visibilityMap.indices) {
            for (columnIndex in visibilityMap.first().indices) {
                visibilityMap[rowIndex][columnIndex] =
                    (mapFromLeftSide[rowIndex][columnIndex] ||
                            mapFromTopSide[rowIndex][columnIndex] ||
                            mapFromRightSide[rowIndex][columnIndex] ||
                            mapFromBottomSide[rowIndex][columnIndex])
            }
        }

        visibilityMap = changeValueOfMapArrayBorders(visibilityMap, true)

        return visibilityMap
    }

    fun part1(input: List<String>): Int {
        val mapOfVisibility =
            createMapOfVisibility(input.map { it.toCharArray().map { char -> char.toString().toInt() }.toIntArray() }
                .toTypedArray())

        return mapOfVisibility.sumOf { row -> row.count { it } }
    }

    //part 2 functions
    fun calculateCountOfVisibleTreesInHorizontalDirection(
        treesTopViewMap: Array<IntArray>,
        direction: DIRECTION,
        homePotentialPlaceRow: Int,
        homePotentialPlaceColumn: Int
    ): Int {
        var currentHeight = treesTopViewMap[homePotentialPlaceRow][homePotentialPlaceColumn]
        var visibleTrees = 0

        val iterator: IntProgression = when (direction) {
            DIRECTION.LEFT_TO_RIGHT -> homePotentialPlaceColumn + 1 until treesTopViewMap[homePotentialPlaceRow].size
            else -> homePotentialPlaceColumn - 1 downTo 0//right to left
        }

        for (column in iterator) {
            visibleTrees++
            if (treesTopViewMap[homePotentialPlaceRow][column] >= currentHeight)
                break
        }

        return visibleTrees
    }


    fun calculateCountOfVisibleTreesInVerticalDirection(
        treesTopViewMap: Array<IntArray>,
        direction: DIRECTION,
        homePotentialPlaceRow: Int,
        homePotentialPlaceColumn: Int
    ): Int {
        var currentHeight = treesTopViewMap[homePotentialPlaceRow][homePotentialPlaceColumn]
        var visibleTrees = 0

        val iterator: IntProgression = when (direction) {
            DIRECTION.TOP_TO_BOTTOM -> homePotentialPlaceRow + 1 until treesTopViewMap.size
            else -> homePotentialPlaceRow - 1 downTo 0//bottom to top
        }

        for (row in iterator) {
            visibleTrees++
            if (treesTopViewMap[row][homePotentialPlaceColumn] >= currentHeight)
                break
        }

        return visibleTrees
    }

    fun part2(input: List<String>): Int {
        val topViewTreeMap = input.map {
            it.toCharArray().map { char -> char.toString().toInt() }.toIntArray()
        }.toTypedArray()

        var max = 0

        for (rowIndex in 0 until topViewTreeMap.size) {
            for (columnIndex in 0 until topViewTreeMap.first().size) {
                val leftToRight =
                    calculateCountOfVisibleTreesInHorizontalDirection(
                        topViewTreeMap,
                        DIRECTION.LEFT_TO_RIGHT,
                        rowIndex,
                        columnIndex
                    )
                val rightToLeft =
                    calculateCountOfVisibleTreesInHorizontalDirection(
                        topViewTreeMap,
                        DIRECTION.RIGHT_TO_LEFT,
                        rowIndex,
                        columnIndex
                    )
                val topToBottom =
                    calculateCountOfVisibleTreesInVerticalDirection(
                        topViewTreeMap,
                        DIRECTION.TOP_TO_BOTTOM,
                        rowIndex,
                        columnIndex
                    )
                val bottomToTop =
                    calculateCountOfVisibleTreesInVerticalDirection(
                        topViewTreeMap,
                        DIRECTION.BOTTOM_TO_TOP,
                        rowIndex,
                        columnIndex
                    )
                val score = leftToRight * rightToLeft * topToBottom * bottomToTop
                if (score > max) max = score
            }
        }
        return max
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)

    val input = readInput("Day08")
    println(part1(input))

    check(part2(testInput) == 8)
    println(part2(input))
}
