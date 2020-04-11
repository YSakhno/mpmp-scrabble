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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.mutable.MutableLong
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val MAX_SCORE_POSSIBLE = 187

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

private suspend fun FlowCollector<Int>.generateScores(needToPick: Int, nextIndex: Int, score: Int) {
    if (needToPick > 0) {
        var lastUsedIdx = -1
        for (curIndex in nextIndex..ScrabbleTileSet.tiles.size - needToPick) {
            if (!ScrabbleTileSet.areTilesEquivalent(curIndex, lastUsedIdx)) {
                generateScores(needToPick - 1, curIndex + 1, score + ScrabbleTileSet.tiles[curIndex].points.points)
                lastUsedIdx = curIndex
            }
        }
    } else {
        emit(score)
    }
}

suspend fun generateUniqueHandsOfLength(targetHandLength: Int): Flow<Int> = flow {
    generateScores(targetHandLength, 0, 0)
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

@OptIn(ExperimentalTime::class)
suspend fun countAllUpTo(minHandLength: Int = 0, maxHandLength: Int = 12) {
    val table: MutableList<MutableList<Long>> = MutableList(MAX_SCORE_POSSIBLE + 1) { MutableList(101) { 0L } }

    println("Counting all hands of length:")
    for (length in minHandLength.coerceAtLeast(0)..maxHandLength.coerceAtMost(50)) {
        print("\t$length...")
        val (countsByScore, time) = measureTimedValue {
            generateUniqueHandsOfLength(length).countByTo(HashMap()) { it }
        }
        println("\tdone in $time")

        for (score in 0..MAX_SCORE_POSSIBLE) {
            if (countsByScore.containsKey(score)) {
                val count = countsByScore[score]?.value ?: 0L

                table[score][length] = count
                table[MAX_SCORE_POSSIBLE - score][100 - length] = count
            }
        }
    }

    println()
    print("score")
    (0..100).forEach { print(",$it") }
    println()

    table.forEachIndexed { score, row ->
        print("$score")
        row.forEach { print(",$it") }
        println()
    }

    print("Total:")
    (0..100).forEach { len -> table.asSequence().map { it[len] }.sum().also { print(",$it") } }
    println()
}

fun main() = runBlocking {
    countAllUpTo()
}
