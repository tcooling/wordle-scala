package com.tcooling.wordle

import cats.effect._
import cats.effect.IO
import com.tcooling.wordle.game.Wordle
import com.tcooling.wordle.model.{Filename, NumberOfGuesses, WordLength, WordleConfig}

// TODO: use * for imports
// TODO: use live methods - hexagonal architecture
object Main extends IOApp {

  private val filename: Filename               = Filename.apply("words.txt")
  private val wordLength: WordLength           = WordLength.apply(5)
  private val numberOfGuesses: NumberOfGuesses = NumberOfGuesses.apply(6)

  private val config = WordleConfig(
    filename = filename,
    wordLength = wordLength,
    numberOfGuesses = numberOfGuesses
  )

  override def run(args: List[String]): IO[ExitCode] = Wordle.live[IO](config).startGame

}
