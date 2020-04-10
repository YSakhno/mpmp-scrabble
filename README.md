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

The version of the solution presented in this branch, it solves for the puzzle/case presented in the video (hand of 7
out of 100 standard Scrabble tiles, exact hand score has to be _exactly_ 46) and outputs all such hands followed by the
total number of hands, just like the solution from the `master` branch.  In addition to that, it then counts _all_ hands
of length 7, and breaks down the counts by the scores that the hands amount to, counting for each score how many
distinct hands are possible. In the end, it outputs a total number of distinct hands of length 7.

If you wish, you can edit the main application file (`App.kt`) and change the constants at the top of the file to see
a solution for different input conditions.

Alternatively, you can see the excerpts from the output [here](SPOILERS.md)
