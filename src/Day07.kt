fun main() {
    val listOfSumIngredients = mutableListOf<Int>()

    abstract class FileSystemElement(val size: Int) {
        var parent: FileSystemElement? = null
        val children: MutableList<FileSystemElement> = mutableListOf()

        abstract fun calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize: Int): Int
    }

    class Root() : FileSystemElement(0) {
        override fun calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize: Int): Int {
            var sum = 0
            children.forEach {
                if (it.children.isNotEmpty()) //skip files
                    sum += it.calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize)
            }
            return sum
        }
    }

    class File(size: Int) : FileSystemElement(size) {
        override fun calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize: Int): Int = this.size
    }

    class Directory(size: Int) : FileSystemElement(size) {
        override fun calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize: Int): Int {
            var sum = 0

            children.forEach {
                sum += (it.calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize) *
                        (if (it.children.isNotEmpty()) 2 else 1)) // if current entity is directory, add twice!
            }
            return if (sum <= maxsize) sum else 0
        }
    }

    fun part1(input: List<String>): Int {
        val fsElements: MutableList<FileSystemElement> = mutableListOf()
        fsElements.add(Root())
        var currentElement: FileSystemElement = fsElements[0]
        var indentationIndex = 0
        var previousIndentationIndex = -1

        //remove first line - it is always root
        val inputWithoutFirstLine = input.drop(1)

        inputWithoutFirstLine.forEach {
            val size: Int = ("""([\d]+)""".toRegex().find(it)?.groupValues?.get(0)?.toInt()) ?: 0
            val nextElement = if (it.contains("dir")) Directory(size) else File(size)

            indentationIndex = it.indexOf("-")

            if (indentationIndex > previousIndentationIndex) {
                nextElement.parent = currentElement
                if (currentElement.children.size == 0)
                    currentElement.children.add(nextElement)

            } else if (indentationIndex == previousIndentationIndex) {
                nextElement.parent = currentElement.parent
                nextElement.parent?.children?.add(nextElement)
            } else {
                nextElement.parent = currentElement.parent!!.parent
                nextElement.parent?.children?.add(nextElement)
            }
            currentElement = nextElement

            previousIndentationIndex = indentationIndex
        }

        return fsElements[0].calculateSizeOfChildrenDirectoriesWithMaxSize(100000)
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
