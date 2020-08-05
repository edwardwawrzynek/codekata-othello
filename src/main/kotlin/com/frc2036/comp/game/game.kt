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
    SouthWest(1, 1);

    fun from(point: Pair<Int, Int>): Pair<Int, Int> {
        val x = point.first + this.dx
        val y = point.second + this.dy
        return Pair(x, y)

    }
}

data class WLTRecord(var wins: Int, var losses: Int, var ties: Int)

class Board() {
    // empty
    var contents = Array(8) { Array(8) { TileType.Empty } }

    // central positions, ordered Player1, Player2, Player1, Player2
    private val centerFourPositions = listOf(Pair(3, 3), Pair(4, 3), Pair(4, 4), Pair(3, 4))
    var player1Key: String? = null
    var player2Key: String? = null

    var lastPlayedPlayer: String? = null

    var ended = false
    var winner: String? = null
    var loser: String? = null

    init {
        resetBoard()
    }

    // load a game by assigning player keys
    fun loadGame(game: Pair<String, String>) {
        player1Key = game.first
        player2Key = game.second
    }

    // reset the board so that it is ready for another game
    fun resetBoard() {
        // clear player information
        player1Key = null
        player2Key = null

        lastPlayedPlayer = null

        ended = false
        winner = null
        loser = null

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

    // check if a value is in the boundaries of the board (8x8)
    fun inbounds(value: Int): Boolean {
        return (value in 0 until 8)
    }

    // get the tile at position pos
    fun getTileAt(pos: Pair<Int, Int>): TileType {
        return contents[pos.first][pos.second]
    }

    // determine flipped tiles in all 8 directions given move (x, y) by player playerKey
    fun getFlippedTiles(playerKey: String, x: Int, y: Int): List<Pair<Int, Int>> {
        // determine the tile types of the player and opponent
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
            // find stones between this stone and next friendly stone in each direction
            for (direction in Direction.values()) {
                var first = true
                var focus = Pair(x, y)
                var foundEnd = false
                val path = mutableListOf<Pair<Int, Int>>()

                // find path
                while (getTileAt(focus) == opponent || first) {
                    first = false

                    // change focus in the direction
                    focus = direction.from(focus)

                    // ensure that the focus is on the board
                    if (!inbounds(focus.first) || !inbounds(focus.second)) break

                    // if it's an endpoint, end the path. If it's an opponent piece, add it
                    if (getTileAt(focus) == player) {
                        foundEnd = true
                        break
                    }
                    else if (getTileAt(focus) == opponent) {
                        path.add(focus.copy())
                    }
                }

                // if the path ended, add it to flippedTiles
                if (foundEnd) flippedTiles.addAll(path)
            }
        }

        return flippedTiles.toList()
    }

    // check if a move is legal
    fun checkLegal(key: String, move: Pair<Int, Int>): Boolean {
        if (getTileAt(move) != TileType.Empty) return false
        val flippedTiles = getFlippedTiles(key, move.first, move.second)
        return flippedTiles.isNotEmpty()
    }

    // get all moves that are legal
    fun getLegalMoves(playerKey: String): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()

        // if the player exists, get their legal moves
        if (playerKey == player1Key || playerKey == player2Key) {
            contents.forEachIndexed { x, col ->
                col.forEachIndexed { y, _ ->
                    val move = Pair(x, y)
                    if (checkLegal(playerKey, move)) moves.add(move)
                }
            }
        }

        return moves.toList()
    }

    // check if a player must forfeit their turn because they have no legal moves
    fun checkMustForfeit(playerKey: String): Boolean = getLegalMoves(playerKey).isEmpty()

    // determine whose turn it is
    fun getNextPlayerKey(): String? {
        var nextPlayer: String? = null

        if (player1Key != null && player2Key != null) {
            var player1MustForfeit = checkMustForfeit(player1Key!!)
            var player2MustForfeit = checkMustForfeit(player2Key!!)

            if (player1MustForfeit && player2MustForfeit) {
                endGame()
            }
            else if (player1MustForfeit && lastPlayedPlayer == player2Key) {
                nextPlayer = player2Key
            }
            else if (player2MustForfeit && lastPlayedPlayer == player1Key) {
                nextPlayer = player1Key
            }
            else if (lastPlayedPlayer == player1Key) {
                nextPlayer = player2Key
            }
            else if (lastPlayedPlayer == null || lastPlayedPlayer == player2Key) {
                nextPlayer = player1Key
            }
        }

        return nextPlayer
    }

    // change contents to flip tiles
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

    // put a move on the board by player at (x, y) and resolve effects
    fun putMove(player: String, x: Int, y: Int): String? {
        // TODO: ensure incoming moves are legal
        var type = TileType.Empty
        when (player) {
            player1Key -> type = TileType.Player1
            player2Key -> type = TileType.Player2
        }

        var error: String? = null

        if (contents[x][y] == TileType.Empty) {
            flipTiles(getFlippedTiles(player, x, y))
            contents[x][y] = type
            lastPlayedPlayer = player
        }
        else error = "space is occupied"

        return error
    }

    // end the game because both players must forfeit their turn
    fun endGame() {
        ended = true

        // tally results
        var p1Score = 0
        var p2Score = 0
        contents.forEachIndexed { _, col ->
            col.forEachIndexed { _, cell ->
                when (cell) {
                    TileType.Player1 -> ++p1Score
                    TileType.Player2 -> ++p2Score
                }
            }
        }

        winner = when {
            p1Score > p2Score -> player1Key
            p2Score > p1Score -> player2Key
            else -> null
        }
        loser = when (winner) {
            player2Key -> player1Key
            player1Key -> player2Key
            else -> null
        }

    }

    // create a new board that is prepared for player key
    fun preparedFor(key: String): Board {
        val newBoard = withReversedAxes()
        newBoard.loadGame(player1Key!! to player2Key!!)

        return newBoard
    }

    // reverse the axes to fix JSON serialization
    fun withReversedAxes(): Board {
        val newBoard = Board()
        newBoard.loadGame(player1Key!! to player2Key!!)
        contents.forEachIndexed { x, col ->
            col.forEachIndexed { y, cell ->
                newBoard.contents[y][x] = cell
            }
        }

        return newBoard
    }

}

class Tournament(val rounds: Int, val observeKey: String, val adminKey: String, val defaultPlayerKeys: Boolean) {
    val keys = if (defaultPlayerKeys) mutableListOf("key0", "key1", "key2", "key3") else mutableListOf("", "", "", "")

    // names of each of the players
    val names = mutableListOf("Player 0", "Player 1", "Player 2", "Player 3")

    // win/loss/tie records
    val records = mutableMapOf<String, WLTRecord>()

    // all boards
    val boards = Array(3) { Board() }

    // maps players (by api key) to the board they are playing on
    val playerBoard = mutableMapOf<String, Board?>()

    /* Game schedule represented by Pairs of API keys. In a round, each
    player plays each other twice times, going first once. Number of games
    equals P(number of AIs, 2) * rounds. With 4 players, this means each
    round consists of 12 games.
    I also may have done my math wrong and am just stupid.
    */
    val schedule = mutableListOf<Pair<String, String>>()
    val startedGames = mutableListOf<Pair<String, String>>()
    val completedGames = mutableListOf<Pair<String, String>>()

    init {
        // build records map from keys
        for (p in names zip names.map { WLTRecord(0, 0, 0) }) {
            records[p.first] = p.second
        }

        // build game schedule
        for (player in keys) {
            for (other in keys) {
                if (player == other) continue
                val matchUp = Pair(player, other)
                for (i in 0 until rounds) schedule.add(matchUp)
            }
        }
        schedule.shuffle()

        // initialize boards to null
        for (key in keys) playerBoard[key] = null
    }

    // check if a player is in a game
    fun playerIsIdle(key: String) = playerBoard[key] == null

    // assign games to boards when possible
    fun updateActiveGames() {
        for (board in boards) {
            // give this board to the next game
            for (game in schedule) {
                /* skip this game if the game has already been played/started,
                or if the players are active. */
                if (game in startedGames) continue
                if (!playerIsIdle(game.first) || !playerIsIdle(game.second)) continue

                board.loadGame(game)
                startedGames.add(Pair(board.player1Key!!, board.player2Key!!))
                playerBoard[game.first] = board
                playerBoard[game.second] = board
                break
            }
        }
    }

    // return if player `key` needs to submit a move
    fun checkMoveNeeded(key: String): Boolean {
        updateActiveGames()

        if (playerBoard[key] != null) {
            val board = playerBoard[key]!!
            val nextPlayerKey = board.getNextPlayerKey()

            if (key == nextPlayerKey) {
                return true
            } else if (board.ended) { // Note: this block isn't complete and has bugs
                // TODO: double check non-null assertions
                // TODO: separate this into 2 methods
                if (board.winner != null && board.loser != null) {
                    ++records[board.winner!!]!!.wins
                    ++records[board.loser!!]!!.losses
                } else {
                    ++records[board.player1Key!!]!!.ties
                    ++records[board.player2Key!!]!!.ties
                }

                completedGames.add(Pair(board.player1Key!!, board.player2Key!!))
                board.resetBoard()
            }
        }
        return false
    }

    // get the index of the player with key `key`
    fun playerFromKey(key: String): Int? {
        if (key == "") return null

        val index = keys.indexOf(key)
        return if (index == -1) null else index
    }

    // get the active board(s) for `key`.
    fun getBoardsForKey(key: String): List<Board?> {
        return if (key == observeKey) { // return all if request is from observer
            boards.toList()
        }
        else {
            listOf(playerBoard[key]) // otherwise, return the games the player is in
        }
    }
}