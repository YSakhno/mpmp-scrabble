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
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Contains tests for some functions of the main app file (**App.kt**).
 *
 * @author Yuri Sakhno
 */
@ExperimentalCoroutinesApi
class AppKtTest : ShouldSpec({

    "Number of hands of length 7" {
        listOf(
            row(46, 138),
            row(48, 50)
        ).map { (score, count) ->
            should("be correct for score $score") {
                val countsByScore = LongArray(MAX_SCORE_POSSIBLE + 1) { 0L }

                generateUniqueHandsOfLength(7, countsByScore)
                countsByScore[score] shouldBe count
            }
        }
    }

    "Number of hands of length 0" {
        should("be correct for score 0") {
            val countsByScore = LongArray(MAX_SCORE_POSSIBLE + 1) { 0L }

            generateUniqueHandsOfLength(0, countsByScore)
            countsByScore[0] shouldBe 1
        }
    }

    listOf(
        row(0, 1),
        row(1, 27),
        row(2, 373),
        row(3, 3509),
        row(4, 25254),
        row(5, 148150),
        row(6, 737311),
        row(7, 3199724),
        row(8, 12353822)
    ).map { (handLength, handCount) ->
        "Number of hands of length $handLength should be correct" {
            val countsByScore = LongArray(MAX_SCORE_POSSIBLE + 1) { 0L }

            generateUniqueHandsOfLength(handLength, countsByScore)
            countsByScore.sum() shouldBe handCount
        }
    }
})
