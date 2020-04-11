/*
 * Solver for Matt Parker's Maths Puzzle (MPMP): Scrabble
 *
 * See this YouTube video for more info about the puzzle itself: https://youtu.be/JaXo_i3ktwM
 * Alternatively, the puzzle is described on this page: http://www.think-maths.co.uk/scrabble-puzzle
 *
 * Written in 2020 by Yuri Sakhno.
 */
package io.ysakhno.mpmp.scrabble

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ScrabbleTileSetTest : ShouldSpec({

    "ScrabbleTileSet" {
        should("be") {
            "$ScrabbleTileSet" shouldBe """
                AAAAAAAAAB
                BCCDDDDEEE
                EEEEEEEEEF
                FGGGHHIIII
                IIIIIJKLLL
                LMMNNNNNNO
                OOOOOOOPPQ
                RRRRRRSSSS
                TTTTTTUUUU
                VVWWXYYZ__
            """.trimIndent()
        }

        should("have 187 points total") {
            ScrabbleTileSet.tiles.sumBy { it.points.points } shouldBe MAX_SCORE_POSSIBLE
        }
    }
})
