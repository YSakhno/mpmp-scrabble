/*
 * Solver for Matt Parker's Maths Puzzle (MPMP): Scrabble
 *
 * See this YouTube video for more info about the puzzle itself: https://youtu.be/JaXo_i3ktwM
 * Alternatively, the puzzle is described on this page: http://www.think-maths.co.uk/scrabble-puzzle
 *
 * Written in 2020 by Yuri Sakhno.
 */
package io.ysakhno.mpmp.scrabble

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

const val HAND_LENGTH = 7
const val HAND_SCORE = 46

inline class Points(val points: Int) : Comparable<Points> {
    override fun compareTo(other: Points) = this.points.compareTo(other.points)
    override fun toString() = "$points point${if (points != 1) "s" else ""}"
}

val Int.pt get() = Points(this)
val Int.pts get() = Points(this)

enum class Tile(val points: Points, val count: Int) {
    A(1.pt, 9), B(3.pts, 2), C(3.pts, 2), D(2.pts, 4), E(1.pt, 12), F(4.pts, 2), G(2.pts, 3), H(4.pts, 2),
    I(1.pt, 9), J(8.pts, 1), K(5.pts, 1), L(1.pt, 4), M(3.pts, 2), N(1.pt, 6), O(1.pt, 8), P(3.pts, 2),
    Q(10.pts, 1), R(1.pt, 6), S(1.pt, 4), T(1.pt, 6), U(1.pt, 4), V(4.pts, 2), W(4.pts, 2), X(8.pts, 1),
    Y(4.pts, 2), Z(10.pts, 1), BLANK(0.pts, 2);

    override fun toString() = if (this != BLANK) name else "_"
}

abstract class TileSet(val tiles: List<Tile>) {

    fun areTilesEquivalent(idx1: Int, idx2: Int) =
        (idx1 in tiles.indices && idx2 in tiles.indices && idx1 != idx2 && tiles[idx1] == tiles[idx2])

    override fun toString() = tiles.foldIndexed("") { idx, str, tile ->
        str + tile + (if ((idx + 1) % 10 == 0 && idx + 1 in tiles.indices) "\n" else "")
    }
}

object ScrabbleTileSet : TileSet(
    Tile.values().flatMapTo(mutableListOf()) { tile -> Iterable { iterator { repeat(tile.count) { yield(tile) } } } }
        .toList()
)

object AltTileSet : TileSet(listOf(Tile.A, Tile.A, Tile.A, Tile.B, Tile.B, Tile.C))

class Hand(private val choice: List<Tile> = listOf()) {
    val length = choice.size

    val score = choice.fold(0) { scr, tile -> scr + tile.points.points }

    operator fun get(idx: Int) = choice[idx]

    operator fun plus(tile: Tile) = Hand(choice + tile)
    override fun toString() = choice.map { "$it" }.joinToString(separator = "")

}

private suspend fun FlowCollector<Hand>.generateUniqueChoices(nextIndex: Int, accumulated: Hand) {
    val tileSet = ScrabbleTileSet

    if (accumulated.length != HAND_LENGTH) {
        var lastUsedIdx = -1
        for (curIndex in nextIndex until tileSet.tiles.size) {
            if (!tileSet.areTilesEquivalent(curIndex, lastUsedIdx)) {
                generateUniqueChoices(curIndex + 1, accumulated + tileSet.tiles[curIndex])
                lastUsedIdx = curIndex
            }
        }
    } else {
        emit(accumulated)
    }
}

suspend fun generateUniqueChoices(): Flow<Hand> = flow {
    generateUniqueChoices(0, Hand())
}

fun main() = runBlocking {
    println("List of all the hands of $HAND_LENGTH tiles to make exactly $HAND_SCORE points:")

    var num = 0

    generateUniqueChoices().filter { it.score == HAND_SCORE }.collect {
        num++
        println(it)
    }

    println("-------------")
    println("Number of [distinct] ways: $num")
}
