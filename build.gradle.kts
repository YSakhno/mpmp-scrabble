/*
 * Solver for Matt Parker's Maths Puzzle (MPMP): Scrabble
 *
 * See this YouTube video for more info about the puzzle itself: https://youtu.be/JaXo_i3ktwM
 * Alternatively, the puzzle is described on this page: http://www.think-maths.co.uk/scrabble-puzzle
 *
 * Written in 2020 by Yuri Sakhno.
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.71"
    application

    eclipse
    idea
}

repositories {
    jcenter()
}

eclipse {
    classpath {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.3.71"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.0.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.0.2")
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
application {
    mainClassName = "io.ysakhno.mpmp.scrabble.AppKt"
}
