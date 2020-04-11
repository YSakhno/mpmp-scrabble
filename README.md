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

Unlike the solution in the `master` branch, the solution presented in this branch, attempts to produce counts of
possible hands for each score, for each hand length.  Unfortunately, enumerating all possible hands of moderate to long
length (~20 or more) takes prohibitively long time even on top-notch modern personal computers.  Because of that, by
default the application only counts hands of at most length **12** (it also counts hands of length 100 down to 88 at the
same time, because of the 'duality' of counts).  This should take about a minute on a reasonably-fast computer.  You may
attempt to change the number to a higher length if you know what you're doing or feeling especially
brave/confident/unreasonable today.  The file [SPOILERS.md](SPOILERS.md) contains output of running the application of
hands of lengths up to **17**.
