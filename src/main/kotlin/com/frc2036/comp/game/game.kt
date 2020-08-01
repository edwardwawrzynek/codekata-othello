package com.frc2036.comp

enum class TileType(val type: Int) {
    Empty(0),
    Player1(1),
    Player2(2)

}

class Board() {
    // empty
    var contents = Array(8) { Array(8) { TileType.Empty } }

    // central positions, ordered Player1, Player2, Player1, Player2
    private val centerFourPositions = listOf(Pair(3, 3), Pair(4, 3), Pair(4, 4), Pair(3, 4))
    var player1Key: String? = null
    var player2Key: String? = null

    init {
        resetBoard()
    }

    fun resetBoard() {
        // create blank board
        contents = Array(8) { Array(8) { TileType.Empty } }

        // place starting stones
        centerFourPositions.forEachIndexed { index, position ->
            val x = position.first
            val y = position.second
            if (index % 2 == 0) { // even index, Player1
                contents[x][y] = TileType.Player1
            }
            else {
                contents[x][y] = TileType.Player2
            }
        }
    }

    fun putMove(player: String, x: Int, y: Int): String? {
        // TODO: ensure incoming moves are legal
        // TODO: resolve move
        var type = TileType.Empty
        when (player) {
            player1Key -> type = TileType.Player1
            player2Key -> type = TileType.Player2
        }

        var error: String? = null

        if (contents[x][y] == TileType.Empty) contents[x][y] = type // TODO: handle it
        else error = "space is occupied"

        return error
    }

}

class Tournament(val observeKey: String, val adminKey: String, val defaultPlayerKeys: Boolean) {
    val keys = if (defaultPlayerKeys) mutableListOf("key0", "key1", "key2", "key3") else mutableListOf("", "", "", "")

    // names of each of the players
    val names = mutableListOf("Player 0", "Player 1", "Player 2", "Player 3")

    // all boards
    val boards = Array(3) { Board() }

    // maps players (by api key) to the board they are playing on
    val playerBoard = mutableMapOf<String, Board>()

    fun playerFromKey(key: String): Int? {
        if (key == "") return null

        val index = keys.indexOf(key)
        return if (index == -1) null else index
    }
}