package com.example.library

fun main() {
    val book1: Item = Book(1, "mybook1", "I", 20, true)
    val dvd1: Item = Disk(2, "diisk1", "DVD", true)
    val dvd2: Item = Disk(22, "diisk2", "СD", true)
    val dvd3: Item = Disk(23, "diisk3", "DVD", true)
    val news1: Item = Newspaper(31, "news1", 412, true)
    val news2: Item = Newspaper(32, "news2", 412, true)
    val news3: Item = Newspaper(33, "news3", 412, true)
    val book2: Item = Book(12, "mybook2", "I", 20, true)
    val book3: Item = Book(123, "mybook3", "I", 20, true)

    val itemList: MutableList<Item> = mutableListOf(
        book1, book2, book3, dvd1, dvd2, dvd3, news1, news2, news3
    )

    
    val manager = LibraryManager(itemList)

    while (true) {
        val welcome = """
                MENU
            1. Show books
            2. Show newspapers
            3. Show disks
        """.trimIndent()
        println(welcome)

        when (readlnOrNull()?.toIntOrNull()) {
            1 -> {
                println("   BOOKS:")
                manager.showItems(Book::class.java)
                manager.itemsFlow(Book::class.java)
            }

            2 -> {
                println("   NEWSPAPER:")
                manager.showItems(Newspaper::class.java)
                manager.itemsFlow(Newspaper::class.java)
            }

            3 -> {
                println("   DISKS:")
                manager.showItems(Disk::class.java)
                manager.itemsFlow(Disk::class.java)
            }

            else -> println("Invalid option. Please try again")
        }
    }
}

open class Item(
    protected val itemId: Int,
    protected val itemName: String,
    protected var isAvailable: Boolean
) {
    fun shortInfo() {
        println("$itemName available: ${if (isAvailable) "Yes" else "No"}")
    }

    open fun fullInfo() {
        println(
            """
            id: $itemId
            name: $itemName
            available: ${if (isAvailable) "Yes" else "No"}
        """.trimIndent()
        )
    }

    fun returnToLibrary() {
        if (!isAvailable) {
            isAvailable = true
            println("$itemName successfully returned")
        } else {
            println("$itemName cannot be returned")
        }
    }
}

class Book(
    bookId: Int,
    bookName: String,
    private val bookAuthor: String,
    private val bookPages: Int,
    isAvailable: Boolean
) : Item(bookId, bookName, isAvailable), Takeable, Readable {
    override fun fullInfo() {
        println("Book: $itemName ($bookPages pages) by $bookAuthor with ID: $itemId, available: ${if (isAvailable) "Yes" else "No"}")
    }

    override fun takeToHome() {
        if (isAvailable) {
            isAvailable = false
            println("Book $itemId has been taken home")
        } else {
            println("Book $itemId cannot be taken home")
        }
    }

    override fun readInHall() {
        if (isAvailable) {
            isAvailable = false
            println("Book $itemId has been taken to the reading hall")
        } else {
            println("Book $itemId cannot be taken to the reading hall")
        }
    }
}

class Newspaper(
    newspaperId: Int,
    newspaperName: String,
    private val newspaperNumber: Int,
    isAvailable: Boolean
) : Item(newspaperId, newspaperName, isAvailable), Readable {
    override fun fullInfo() {
        println("Release $newspaperNumber of newspaper $itemName with ID: $itemId, available: ${if (isAvailable) "Yes" else "No"}")
    }

    override fun readInHall() {
        if (isAvailable) {
            isAvailable = false
            println("Newspaper $itemId has been taken to the reading hall")
        } else {
            println("Newspaper $itemId cannot be taken to the reading hall")
        }
    }
}

class Disk(
    diskId: Int,
    diskName: String,
    private val diskType: String,
    isAvailable: Boolean
) : Item(diskId, diskName, isAvailable), Takeable {
    override fun fullInfo() {
        println("$diskType $itemName, available: ${if (isAvailable) "Yes" else "No"}")
    }

    override fun takeToHome() {
        if (isAvailable) {
            isAvailable = false
            println("Disk $itemId has been taken home")
        } else {
            println("Disk $itemId cannot be taken home")
        }
    }
}

interface Takeable {
    fun takeToHome()
}

interface Readable {
    fun readInHall()
}

class LibraryManager(private val itemList: MutableList<Item>) {
    private fun getIndex(): Int? {
        println("Enter item index:")
        return readlnOrNull()?.toIntOrNull()?.minus(1)
    }

    fun showItems(type: Class<out Item>) {
        val filteredList = itemList.filterIsInstance(type)
        filteredList.forEachIndexed { index, item ->
            print("${index + 1}. ")
            item.shortInfo()
        }
        println("${filteredList.size + 1} Return to main menu")
    }

    fun itemsFlow(type: Class<out Item>) {
        val index = getIndex()
        val filteredList = itemList.filterIsInstance(type)
        if (index == filteredList.size) {
            println("Returning to main menu")
            return
        }
        if (index == null || index !in filteredList.indices) {
            println("Invalid index. Returning to main menu")
            return
        }

        val item = filteredList[index]
        while (true) {
            println(
                """
                    OPTIONS
                1. Take to home
                2. Take to reading hall
                3. Show full info
                4. return to library
                5. Return to main menu
            """.trimIndent()
            )

            when (readlnOrNull()?.toIntOrNull()) {
                1 -> {
                    if (item is Takeable) {
                        item.takeToHome()
                    } else {
                        println("This item cannot be taken home")
                    }
                }

                2 -> {
                    if (item is Readable) {
                        item.readInHall()
                    } else {
                        println("This item cannot be read in the reading hall")
                    }
                }

                3 -> item.fullInfo()
                4 -> item.returnToLibrary()
                5 -> {
                    println("Returning to main menu")
                    return
                }

                else -> println("Invalid option. Please try again")
            }
        }
    }
}
