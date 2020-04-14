# Solver for Matt Parker's Maths Puzzle (MPMP): Scrabble

The original puzzle is (was) presented here: https://www.think-maths.co.uk/scrabble-puzzle

Corresponding YouTube video: https://youtu.be/JaXo_i3ktwM

## Running

You have to have at least Java (JDK) 8 installed (preferably to have JDK 11 or later version). You can download
one / find installation instructions here: https://adoptopenjdk.net/

Once JDK is properly installed, run the following command:

```
    $ ./gradlew run
```

(on Windows run the following command instead)

```
    gradlew.bat run
```

## What it does

By default (as is), it solves for the puzzle/case presented in the video (hand of 7 out of 100 standard Scrabble tiles,
hand score has to be _exactly_ 46) and outputs all such hands followed by the total number of hands.

If you wish, you can edit the main application file (`App.kt`) and change the constants at the top of the file to see
a solution for different input conditions.

## Alternative (extended) solution

The branch [`extended`](https://github.com/YSakhno/mpmp-scrabble/tree/extended) contains extended solver that finds
additional information (like the counts of hands that produce other scores than 46, or counts for hands of lengths other
than 7).  You may check the code in that branch out (and try to run it), or you can see the information produced by that
solver version [here](SPOILERS.md)
