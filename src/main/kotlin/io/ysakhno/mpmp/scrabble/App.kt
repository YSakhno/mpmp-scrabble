/*
 * Solver for Matt Parker's Maths Puzzle (MPMP): Scrabble
 *
 * See this YouTube video for more info about the puzzle itself: https://youtu.be/JaXo_i3ktwM
 * Alternatively, the puzzle is described on this page: http://www.think-maths.co.uk/scrabble-puzzle
 *
 * Written in 2020 by Yuri Sakhno.
 */
package io.ysakhno.mpmp.scrabble

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

object TileSet {
    val tiles = Tile.values()
        .flatMapTo(mutableListOf()) { tile -> Iterable { iterator { repeat(tile.count) { yield(tile) } } } }
        .toList()

    override fun toString() = tiles.foldIndexed("") { idx, str, tile ->
        str + tile + (if ((idx + 1) % 10 == 0 && idx + 1 in tiles.indices) "\n" else "")
    }
}

fun main() {
    println("Have the following set of tiles:")
    println(TileSet)
}
