/*
 * Solver for Matt Parker's Maths Puzzle (MPMP): Scrabble
 *
 * See this YouTube video for more info about the puzzle itself: https://youtu.be/JaXo_i3ktwM
 * Alternatively, the puzzle is described on this page: http://www.think-maths.co.uk/scrabble-puzzle
 *
 * Written in 2020 by Yuri Sakhno.
 */
package io.ysakhno.mpmp.scrabble

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import oshi.SystemInfo
import java.util.concurrent.Executors
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.measureTime

const val MAX_SCORE_POSSIBLE = 187

private val numCpu = SystemInfo().hardware.processor.physicalProcessorCount
private val dispatcher =  Executors.newFixedThreadPool(numCpu).asCoroutineDispatcher()

@OptIn(ExperimentalTime::class)
private val appRunTimeMark = TimeSource.Monotonic.markNow()

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

abstract class TileSet(val tiles: Array<Tile>) {

    val points = tiles.map { it.points.points }.toIntArray()
    val indices = tiles.computeIndices()

    override fun toString() = tiles.foldIndexed("") { idx, str, tile ->
        str + tile + (if ((idx + 1) % 10 == 0 && idx + 1 in tiles.indices) "\n" else "")
    }

    private fun Array<Tile>.computeIndices(): IntArray {
        val indices = IntArray(size)
        var i = 0

        while (i < size) {
            var j = i+1

            while (j < size && this[i] == this[j]) j++

            indices.fill(j, i, j)
            i = j
        }

        return indices
    }
}

object ScrabbleTileSet : TileSet(
    Tile.values().flatMapTo(mutableListOf()) { tile -> Iterable { iterator { repeat(tile.count) { yield(tile) } } } }
        .toTypedArray()
)

private suspend fun FlowCollector<Int>.generateScores(needToPick: Int, nextIndex: Int, score: Int) {
    var curIndex = nextIndex

    while (curIndex <= ScrabbleTileSet.points.size - needToPick) {
        if (needToPick > 1) {
            generateScores(needToPick - 1, curIndex + 1, score + ScrabbleTileSet.points[curIndex])
        } else {
            emit(score + ScrabbleTileSet.points[curIndex])
        }
        curIndex = ScrabbleTileSet.indices[curIndex]
    }
}

suspend fun generateUniqueHandsOfLength(targetHandLength: Int): Flow<Int> = flow {
    if (targetHandLength > 0) {
        generateScores(targetHandLength, 0, 0)
    } else {
        emit(0)
    }
}

@OptIn(ExperimentalTime::class)
suspend fun countByScore(handLength: Int, countsByScore: LongArray) {
    val generator = generateUniqueHandsOfLength(handLength)
    val time = measureTime { generator.collect { countsByScore[it]++ } }

    println("Counted hands of length $handLength in $time (${appRunTimeMark.elapsedNow()} since app start)")
}

suspend fun countAllUpTo(minHandLength: Int = 0, maxHandLength: Int = 12) {
    val minLength = minHandLength.coerceAtLeast(0)
    val maxLength = maxHandLength.coerceAtMost(50)
    val allCounts = Array(maxLength + 1) { LongArray(MAX_SCORE_POSSIBLE + 1) }

    println("Counting all hands of lengths $minLength to $maxLength.  Please wait...")
    withContext(dispatcher) {
        for (length in minLength..maxLength) {
            launch {
                countByScore(length, allCounts[length])
            }
        }
    }
    dispatcher.close()

    val table: MutableList<MutableList<Long>> = MutableList(MAX_SCORE_POSSIBLE + 1) { MutableList(101) { 0L } }

    allCounts.forEachIndexed { length, countsByScore ->
        countsByScore.forEachIndexed { score, count ->
            if (count != 0L) {
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
