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
import kotlinx.coroutines.flow.count

/**
 * Contains tests for some functions of the main app file (**App.kt**).
 *
 * @author Yuri Sakhno
 */
@ExperimentalCoroutinesApi
class AppKtTest : ShouldSpec({

    "Number of hands of length 7" {
        should("be correct for score 46") {
            generateUniqueHandsOfLength(7).count { it.score == 46 } shouldBe 138
        }
        should("be correct for score 48") {
            generateUniqueHandsOfLength(7).count { it.score == 48 } shouldBe 50
        }
    }

    listOf(
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
            generateUniqueHandsOfLength(handLength).count() shouldBe handCount
        }
    }
})