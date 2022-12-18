//region data classes

enum class PROMPT_TYPE {
    COMMAND_CD_ROOT, //move to root
    COMMAND_CD_LOWER_LEVEL, //lower level, means deeper in file structure
    COMMAND_CD_UPPER_LEVEL, //upper level means returning closer to the root of file system
    COMMAND_LS
}

abstract class FileSystemElement(var size: Int, var nodeName: String, var parent: FileSystemElement?) {
    val children: MutableList<FileSystemElement> = mutableListOf()

    abstract fun calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize: Int): Int
}


class Root() : FileSystemElement(0, "/", null) {
    override fun calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize: Int): Int {
        var sum = 0
        children.forEach {
            if (it.children.isNotEmpty()) //skip files
            {
                sum += it.calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize)
//                println("keysum=$sum")
            }
        }
        return sum.also { println("root; return sum=$sum") }
    }
}

class File(size: Int, nodeName: String, parent: FileSystemElement?) : FileSystemElement(size, nodeName, parent) {
    override fun calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize: Int): Int =
        this.size.also { println("file; return sum=${this.size}") }
}

class Directory(size: Int, nodeName: String, parent: FileSystemElement?) : FileSystemElement(size, nodeName, parent) {
    override fun calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize: Int): Int {
        var totalSum = 0
        var sumOfFiles =0
        var sumOfChildrenDirectories = 0
        children.forEach {
            totalSum += (it.calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize) *
                    (if (it.children.isNotEmpty()) 2 else 1)) // if current entity is directory, add twice!
        }
        return (if (totalSum <= maxsize) totalSum else 0).also { println("directory; return sum=$it") }
    }


//OLD:    override fun calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize: Int): Int {
//        var totalSum = 0
//        var sumOfFiles =0
//        var sumOfChildrenDirectories = 0
//
//        children.forEach {
//            totalSum += (it.calculateSizeOfChildrenDirectoriesWithMaxSize(maxsize) *
//                    (if (it.children.isNotEmpty()) 2 else 1)) // if current entity is directory, add twice!
//        }
//        return (if (totalSum <= maxsize) totalSum else 0).also { println("directory; return sum=$it") }
//    }
}

class PromptCommand(
    val promptType: PROMPT_TYPE,
    var additionalInfo: List<String>? = null
) {
}

//endregion

fun main() {
    //region create list of commands

    fun createListOfCommands(input: List<String>): List<PromptCommand> {

        //1. convert list of strings to one string var
        val sb = StringBuilder()
        input.forEach {
            sb.append(it + "\n")
        }
        val str = sb.toString()

        //2. split prompt commands
        var promptLines = str.split("$").toMutableList()

        //2.1 remove empty elements
        promptLines.removeAll { it == "" }
        //2.2 remove spaces on beginning
        for (i in 0 until promptLines.size) {
            promptLines[i] = promptLines[i].removePrefix(" ")
        }


        //3. create list of prompts
        val listOfCommands = mutableListOf<PromptCommand>()
        promptLines.forEach {
            val cmd = it.substring(0, 2)

            if (it.substring(0, 2) == "ls") {
                var additionalInfo = it.lines().drop(1).dropLastWhile { line -> line == "" }
                listOfCommands.add(PromptCommand((PROMPT_TYPE.COMMAND_LS), additionalInfo))
            } else {
                val cdCommand = it.substring(0, 5)
                when (cdCommand) {
                    "cd /", "cd /\n" -> listOfCommands.add(PromptCommand((PROMPT_TYPE.COMMAND_CD_ROOT)))
                    "cd .." -> listOfCommands.add(PromptCommand((PROMPT_TYPE.COMMAND_CD_UPPER_LEVEL)))
                    else -> {
                        val obj = PromptCommand((PROMPT_TYPE.COMMAND_CD_LOWER_LEVEL))
                        obj.additionalInfo = listOf<String>(it.split(" ")[1].replace("\n", ""))
                        listOfCommands.add(obj)
                    }
                }
            }
        }

        return listOfCommands
    }

    //endregion

    //region file system checker (iterate and collect information about it)
    fun createFileStructureMap(listOfCommands: List<PromptCommand>): MutableList<FileSystemElement> {
        val fileSystemStructure: MutableList<FileSystemElement> = mutableListOf()

        //4. handle this list and create map of files
        var currentLevelOfIndent = 0
        var currentElement: FileSystemElement? = null

        listOfCommands.forEach { prompt ->
            when (prompt.promptType) {
                PROMPT_TYPE.COMMAND_CD_ROOT -> {
                    val el = Root()
                    fileSystemStructure.add(el)
                    currentElement = el
                }
                PROMPT_TYPE.COMMAND_CD_UPPER_LEVEL -> {
                    currentElement = currentElement!!.parent
                }
                PROMPT_TYPE.COMMAND_CD_LOWER_LEVEL -> {
                    val prevCurrentElement = currentElement
                    val name = prompt.additionalInfo!![0]
                    currentElement = currentElement!!.children.find { it.nodeName == name }
                    if (currentElement == null) {
                        val el = Directory(0, name, prevCurrentElement)
                        prevCurrentElement!!.children.add(el)
                        currentElement = el
                    }
                }
                PROMPT_TYPE.COMMAND_LS -> {
                    //println("doing listing in node with name=${currentElement!!.nodeName}")
                    prompt.additionalInfo!!.forEach { nodeString ->
                        if (!nodeString.contains("dir")) //node is a file
                        {
                            val grp = nodeString.split(" ")
                            val size = grp[0].toInt()
                            val name = grp[1]
                            //println("fullString=${nodeString}, size=${size}, name=${name}")
                            currentElement!!.children.add(File(size, name, currentElement))
                        } else {//directory
                            val name = nodeString.split(" ")[1]
                            currentElement!!.children.add(Directory(0, name, currentElement))
                        }
                    }
                }

            }

            if (prompt.promptType == PROMPT_TYPE.COMMAND_CD_LOWER_LEVEL)
                currentLevelOfIndent++
        }

        return fileSystemStructure
    }


    //endregion

    //region handle map of files
    /*this block of code needs input in format like:
    - / (dir)
    - a (dir)
    - e (dir)
    - i (file, size=584)
    - f (file, size=29116)
    - g (file, size=2557)
    - h.lst (file, size=62596)
    - b.txt (file, size=14848514)
    - c.dat (file, size=8504156)
    - d (dir)
    - j (file, size=4060174)
    - d.log (file, size=8033020)
    - d.ext (file, size=5626152)
    - k (file, size=7214296)*/


    fun handleMapOfFiles(input: List<String>): Int {
        val fsElements: MutableList<FileSystemElement> = mutableListOf()
        fsElements.add(Root())
        var currentElement: FileSystemElement = fsElements[0]
        var indentationIndex = 0
        var previousIndentationIndex = -1

        //remove first line - it is always root
        val inputWithoutFirstLine = input.drop(1)

        inputWithoutFirstLine.forEach {
            val size: Int = ("""([\d]+)""".toRegex().find(it)?.groupValues?.get(0)?.toInt()) ?: 0
            val nextElement = if (it.contains("dir")) Directory(size, "dir", null) else File(size, "file", null)

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
    //endregion

    fun part1(input: List<String>): Int {
        val commandsList = createListOfCommands(input)
        val fileStructure = createFileStructureMap(commandsList)
        val size = fileStructure[0].calculateSizeOfChildrenDirectoriesWithMaxSize(100000)
        return size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
   // val testInput = readInput("Day07_test")
   // val sz =  part1(testInput).also { println("sz=${it}") }
   // check(sz == 95437)

    val input = readInput("Day07")
    println(part1(input))
    //println(part2(input))
}
