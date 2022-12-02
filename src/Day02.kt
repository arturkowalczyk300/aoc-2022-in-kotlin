enum class Result {
    LOOSE, WIN, DRAW
}

fun moveStringToMoveScore(move: String, firstMoveCharacter: Char): Int {
    return move.toCharArray().first().code - firstMoveCharacter.code + 1
}

fun stringToDesiredResult(desiredResultString: String): Result {

    return when (desiredResultString.toCharArray().first()) {
        'X' -> Result.LOOSE
        'Y' -> Result.DRAW
        else -> Result.WIN
    }
}

fun findMoveToAchieveDesiredResult(desiredResult: Result, elfMove: String): String {
    val elfMoveCode = moveStringToMoveScore(elfMove, 'A')

    var calculatedMoveCode: Int = 0

    if (desiredResult == Result.DRAW)
        calculatedMoveCode = elfMoveCode
    else {
        when (elfMoveCode) {
            1 -> calculatedMoveCode = if (desiredResult == Result.WIN) 2 else 3
            2 -> calculatedMoveCode = if (desiredResult == Result.WIN) 3 else 1
            3 -> calculatedMoveCode = if (desiredResult == Result.WIN) 1 else 2
        }
    }

    return when (calculatedMoveCode) {
        1 -> "X"
        2 -> "Y"
        else -> "Z"
    }
}


fun part1(rounds: List<String>): Int {
    var elfScore = 0
    var myScore = 0

    rounds.forEach {
        val moves = it.split(" ")

        val elfMoveValue = moveStringToMoveScore(moves[0], 'A')
        val myMoveValue = moveStringToMoveScore(moves[1], 'X')

        lateinit var myResult: Result

        if (elfMoveValue == myMoveValue) { //draw situation
            myResult = Result.DRAW
        } else {
            when (myMoveValue) {
                1 -> myResult = if (elfMoveValue == 2) Result.LOOSE else Result.WIN
                2 -> myResult = if (elfMoveValue == 3) Result.LOOSE else Result.WIN
                3 -> myResult = if (elfMoveValue == 1) Result.LOOSE else Result.WIN
            }
        }

        when (myResult) {
            Result.LOOSE -> elfScore += 6
            Result.DRAW -> {
                elfScore += 3
                myScore += 3
            }
            Result.WIN -> myScore += 6
        }
        elfScore += elfMoveValue
        myScore += myMoveValue
    }

    return myScore
}

fun part2(rounds: List<String>): Int {
    val listOfMoves = mutableListOf<String>()

    var elfScore = 0
    var myScore = 0

    rounds.forEach {
        val splittedString = it.split(" ")

        val desiredResult = stringToDesiredResult(splittedString[1])
        val myMove = findMoveToAchieveDesiredResult(desiredResult, splittedString[0])

        listOfMoves.add("${splittedString[0]} ${myMove}")

    }

    return part1(listOfMoves)
}


fun main() {
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    println(part1(input))

    val secondTestInput = readInput("Day02_test_second")
    check(part2(secondTestInput) == 12)

    println(part2(input))
}