package com.frc2036.comp

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam

import kotlin.random.Random

// Controller to manage api calls to run the tournament
@RestController
@RequestMapping(value = ["/api"])
class GameController {
    private val tournament = Tournament(1,"observe0","admin0", true)

    @RequestMapping(value = ["/board"], method = [RequestMethod.GET], produces = ["application/json"])
    @Synchronized
    fun getBoard(@RequestParam key: String): String {
        val boards = tournament.getBoardsForKey(key)
        if (boards.size == 1) {
            val board = boards[0]?.preparedFor(key)
            return if (board != null) {
                "{\"error\": null, \"board\": ${board.contents.map { column ->
                    column.map { tile ->
                        tile.type
                    }
                }
                }}"
            } else {
                "{\"error\": null, \"board\": null}"
            }
        }
        else {
            // send all boards in tournament to observer
            val boardStrings = mutableListOf<String>()
            for (board in boards) {
                if (board != null) {
                    val boardString = "${board.contents.map { column ->
                        column.map { tile ->
                            tile.type
                        }
                    }}"
                    boardStrings.add(boardString)
                }
            }
            return "{\"error\": null, \"boards\": $boardStrings}"
        }
    }

    @RequestMapping(value = ["/move_needed"], method = [RequestMethod.GET], produces = ["application/json"])
    @Synchronized
    fun moveNeeded(@RequestParam key: String): String {
        val needed = tournament.checkMoveNeeded(key)
        return "{\"error\": null, \"needed\": $needed}"
    }

    @RequestMapping(value = ["/set_name"], method = [RequestMethod.POST], produces = ["application/json"])
    @Synchronized
    fun setName(@RequestParam key: String, @RequestParam newName: String): String {
        val player = tournament.playerFromKey(key)
        if (player != null) {
            tournament.names[player] = newName
        }

        return "{\"error\": null}"
    }

    @RequestMapping(value = ["/move"], method = [RequestMethod.POST], produces = ["application/json"])
    @Synchronized
    fun makeMove(@RequestParam key: String, @RequestParam x: Int, @RequestParam y: Int): String {
        if (key !in tournament.keys) {
            return "{\"error\": \"must be a player to make a move\"}"
        }

        val board = tournament.getBoardsForKey(key)[0]
        var error: String? = "no active game"
        if (board != null) {
            error = board.putMove(key, x, y)
        }
        return "{\"error\": \"$error\"}"
    }
}