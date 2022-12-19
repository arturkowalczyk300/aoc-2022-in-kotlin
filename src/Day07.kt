//region data classes

enum class PROMPT_TYPE {
    COMMAND_CD_ROOT, //move to root
    COMMAND_CD_LOWER_LEVEL, //lower level, means deeper in file structure
    COMMAND_CD_UPPER_LEVEL, //upper level means returning closer to the root of file system
    COMMAND_LS
}

abstract class FileSystemElement(var size: Int, var nodeName: String, var parent: FileSystemElement?) {
    protected val children: MutableList<FileSystemElement> = mutableListOf()

    fun calculateSize(): Int {
        var sum = 0
        children.forEach {
            sum += it.calculateSize()
        }
        if (children.isEmpty())
            sum += this.size

        this.size = sum

        return sum
    }

    fun getChildrenReadOnly(): List<FileSystemElement> = children.toList()

    fun addFile(file: File) {
        children.add(file)
        size += file.size
    }

    fun addDirectory(dir: Directory) {
        children.add(dir)
    }

    fun getFlatten(): List<FileSystemElement> {
        val mutableList = mutableListOf<FileSystemElement>()
        children.forEach {
            if (it is Directory)
                mutableList.addAll(it.getFlatten())
            mutableList.add(it)
        }
        return mutableList.toList()
    }
}


class Root() : FileSystemElement(0, "/", null) {

}

class File(size: Int, nodeName: String, parent: FileSystemElement?) : FileSystemElement(size, nodeName, parent) {

}

class Directory(size: Int, nodeName: String, parent: FileSystemElement?) : FileSystemElement(size, nodeName, parent) {

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
                    currentElement = currentElement!!.getChildrenReadOnly().find { it.nodeName == name }
                    if (currentElement == null) {
                        val el = Directory(0, name, prevCurrentElement)
                        prevCurrentElement!!.addDirectory(el)
                        currentElement = el
                    }
                    currentLevelOfIndent++
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
                            currentElement!!.addFile(File(size, name, currentElement))
                        } else {//directory
                            val name = nodeString.split(" ")[1]
                            currentElement!!.addDirectory(Directory(0, name, currentElement))
                        }
                    }
                }

            }
        }

        return fileSystemStructure
    }

    fun findSizeOfDirectoryWhichDeletionWillGiveMoreSpace(dirList: List<Directory>, neededSpace: Int): Int {
        val found = dirList.find {
            it.size >= neededSpace
        }
        return found!!.size
    }
    //endregion

    fun part1(input: List<String>): Int {
        val commandsList = createListOfCommands(input)
        val fileStructure = createFileStructureMap(commandsList)

        val flatten = fileStructure[0].getFlatten()

        fileStructure[0].calculateSize()
        var size = flatten.sumOf {
            if (it is Directory && it.size < 100000) it.size
            else 0
        }

        return size
    }

    fun part2(input: List<String>): Int {
        val commandsList = createListOfCommands(input)
        val fileStructure = createFileStructureMap(commandsList)

        val flatten = fileStructure[0].getFlatten()
        fileStructure[0].calculateSize()

        //get list of dirs, sorted
        val directories = flatten.map {
            if (it is Directory)
                it
            else null
        }.filterNotNull()
            .toMutableList().apply {
                add(Directory(fileStructure[0].size, fileStructure[0].nodeName, null))//add / directory
            }
            .sortedBy {
                it.size
            }

        val totalSize = 70000000
        val currentlyUsedSpace = fileStructure[0].size //currently used space
        val freeSpace = totalSize - currentlyUsedSpace
        val neededSpace = 30000000 - freeSpace

        return findSizeOfDirectoryWhichDeletionWillGiveMoreSpace(directories, neededSpace)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)

    val input = readInput("Day07")
    println(part1(input))

    check(part2(testInput) == 24933642)
    println(part2(input))
}
