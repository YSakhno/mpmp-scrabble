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
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.TreeMap

/**
 * Contains tests for the [Tile] class. These tests basically test that the initial conditions were imported correctly
 * from the original puzzle's description.
 *
 * @author Yuri Sakhno
 */
class TileTest : ShouldSpec({

    "Count of tiles" {
        should("be 100") {
            Tile.values().fold(0) { count, tile -> count + tile.count } shouldBe 100
        }
    }

    "Points and counts" {
        should("be correct") {
            val byPoints = Tile.values()
                .groupByTo(TreeMap()) { it.points }
                .map { "${it.key} - ${it.value.prettyPrint()}" }

            byPoints shouldHaveSize 8
            byPoints[0] shouldBe "0 points - _ (x2)"
            byPoints[1] shouldBe "1 point - A (x9), E (x12), I (x9), L (x4), N (x6), O (x8), R (x6), S (x4), T (x6), U (x4)"
            byPoints[2] shouldBe "2 points - D (x4), G (x3)"
            byPoints[3] shouldBe "3 points - B (x2), C (x2), M (x2), P (x2)"
            byPoints[4] shouldBe "4 points - F (x2), H (x2), V (x2), W (x2), Y (x2)"
            byPoints[5] shouldBe "5 points - K (x1)"
            byPoints[6] shouldBe "8 points - J (x1), X (x1)"
            byPoints[7] shouldBe "10 points - Q (x1), Z (x1)"
        }
    }

    "Order of tiles" {
        should("be alphabetical") {
            val tiles = Tile.values().map { "$it" }
            val letters = ('A'..'Z').map { "$it" }.fold(listOf<String>()) { lst, letter -> lst + letter } + "_"

            tiles shouldBe letters
        }
    }

    "Sum of points" {
        should("be 187") {
            Tile.values().sumBy { it.points.points * it.count } shouldBe MAX_SCORE_POSSIBLE
        }
    }
})

private fun List<Tile>.prettyPrint() = StringBuilder().also { builder ->
    this.forEach {
        if (builder.isNotEmpty()) builder.append(", ")
        builder.append("$it (x${it.count})")
    }
}.toString()
