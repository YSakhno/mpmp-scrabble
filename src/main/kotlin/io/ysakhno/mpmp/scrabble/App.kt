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
import org.apache.commons.lang3.mutable.MutableLong
import java.util.TreeMap

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

class Hand(private val choice: List<Tile> = emptyList()) {

    val score = choice.fold(0) { scr, tile -> scr + tile.points.points }

    operator fun get(idx: Int) = choice[idx]

    operator fun plus(tile: Tile) = Hand(choice + tile)
    override fun toString() = choice.joinToString(separator = "") { "$it" }
}

private suspend fun FlowCollector<Hand>.generateUniqueChoices(needToPick: Int, nextIndex: Int, accumulated: Hand) {
    if (needToPick > 0) {
        var lastUsedIdx = -1
        for (curIndex in nextIndex until ScrabbleTileSet.tiles.size) {
            if (!ScrabbleTileSet.areTilesEquivalent(curIndex, lastUsedIdx)) {
                generateUniqueChoices(needToPick - 1, curIndex + 1, accumulated + ScrabbleTileSet.tiles[curIndex])
                lastUsedIdx = curIndex
            }
        }
    } else {
        emit(accumulated)
    }
}

suspend fun generateUniqueHandsOfLength(targetHandLength: Int): Flow<Hand> = flow {
    generateUniqueChoices(targetHandLength, 0, Hand())
}

suspend inline fun <T, K, M : MutableMap<in K, MutableLong>> Flow<T>.countByTo(
    destination: M,
    crossinline keySelector: suspend (T) -> K
) = collect { value ->
    val key = keySelector(value)
    val count = destination[key]

    if (count != null) {
        count.increment()
    } else {
        destination.put(key, MutableLong(1L))
    }
}.let { destination }

suspend fun countWaysForCertainScore(handLength: Int = HAND_LENGTH, targetScore: Int = HAND_SCORE) {
    println("List of all the hands of $handLength tiles to make exactly $targetScore points:")

    var num = 0

    generateUniqueHandsOfLength(handLength).filter { it.score == targetScore }.collect {
        num++
        println(it)
    }

    println("-------------")
    println("Number of [distinct] ways: $num")
}

suspend fun countByScore(handLength: Int = HAND_LENGTH) {
    println("List of all counts, by each score:")

    var totalNum = 0L
    val countsByScore = generateUniqueHandsOfLength(handLength).countByTo(TreeMap()) { it.score }

    countsByScore.forEach { (score, num) ->
        totalNum += num.value
        println("${Points(score)} -> $num ways")
    }

    println("-------------")
    println("Total number of [distinct] ways: $totalNum")
}

fun main() = runBlocking {
    countWaysForCertainScore()
    println()
    countByScore()
}
