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
    private val tournament = Tournament(System.getenv()["SNAKE_OBSERVE_KEY"] ?: "observe0", System.getenv()["SNAKE_ADMIN_KEY"] ?: "admin0", System.getenv("SNAKE_NO_DEFAULT_KEYS") == null)

    @RequestMapping(value = ["/board"], method = [RequestMethod.GET], produces = ["application/json"])
    @Synchronized
    fun getBoard(@RequestParam key: String): String {
        val board = tournament.playerBoard[key]
        return if (board != null) {
            "{\"error\": null, \"board\": ${board.contents.map { column ->
                column.map { tile ->
                    tile.type
                }
            }
            }}"
        }
        else {
            "{\"error\":null, \"board\": null}"
        }
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
        val board = tournament.playerBoard[key]
        var error: String? = "no active game"
        if (board != null) {
            error = board.putMove(key, x, y)
        }
        return "\"error\": $error"
    }

}