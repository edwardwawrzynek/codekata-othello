package com.frc2036.comp

enum class TileType(val type: Int) {
    Empty(0),
    Player1(1),
    Player2(2)

}

enum class Direction(val dx: Int, val dy: Int) {
    /** cardinal directions **/
    North(0, -1),
    South(0, 1),
    East(-1, 0),
    West(1, 0),

    /** orthogonal directions **/
    NorthEast(-1, -1),
    NorthWest(1, -1),
    SouthEast(-1, 1),
    SouthWest(1, -1);

    fun from(point: Pair<Int, Int>): Pair<Int, Int> {
        val x = point.first + this.dx
        val y = point.second + this.dy
        return Pair(x, y)

    }
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

    fun inbounds(value: Int): Boolean {
        return when {
            value >= 8 -> false
            value <= 0 -> false
            else -> true
        }
    }

    fun getTileAt(pos: Pair<Int, Int>): TileType {
        return contents[pos.first][pos.second]
    }

    fun getFlippedTiles(playerKey: String, x: Int, y: Int): List<Pair<Int, Int>> {
        val player = when (playerKey) {
            player1Key -> TileType.Player1
            player2Key -> TileType.Player2
            else -> null
        }
        val opponent = when (player) {
            TileType.Player1 -> TileType.Player2
            TileType.Player2 -> TileType.Player1
            else -> null
        }

        val flippedTiles = mutableListOf<Pair<Int, Int>>()

        if (player != null && opponent != null) {
            // find first friendly stone in each direction
            for (direction in Direction.values()) {
                var focus = Pair(x, y)
                var foundEndpoint = false
                val path = mutableListOf<Pair<Int, Int>>()
                do {
                    focus = direction.from(focus)
                    if (getTileAt(focus) == player) {
                        foundEndpoint = true
                        break
                    }
                    else {
                        path.add(focus)
                    }
                } while (inbounds(focus.first) && inbounds(focus.second) && getTileAt(focus) == opponent)

                if (foundEndpoint) flippedTiles.addAll(path)
            }
        }

        return flippedTiles.toList()
    }

    fun checkLegal(key: String, move: Pair<Int, Int>): Boolean {
        val flippedTiles = getFlippedTiles(key, move.first, move.second)
        return flippedTiles.isNotEmpty()
    }

    fun flipTiles(tiles: List<Pair<Int, Int>>) {
        for (tile in tiles) {
            val x = tile.first
            val y = tile.second
            when (contents[x][y]) {
                TileType.Player1 -> contents[x][y] = TileType.Player2
                TileType.Player2 -> contents[x][y] = TileType.Player1
                TileType.Empty -> println("Warning: tried to flip empty tile.")
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

        if (contents[x][y] == TileType.Empty) {
            flipTiles(getFlippedTiles(player, x, y))
            contents[x][y] = type
        }
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

    fun getBoardsForKey(key: String): List<Board?> {
        return if (key == observeKey) {
            boards.toList()
        }
        else {
            listOf(playerBoard[key])
        }
    }
}