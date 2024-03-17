package com.tcooling.wordle

import cats.effect._
import cats.effect.IO
import cats.effect.std.Console
import cats.implicits._
import cats.syntax.all._
import cats.Monad
import com.tcooling.wordle.game.Wordle
import com.tcooling.wordle.input.UserInputGuessConnector
import com.tcooling.wordle.model.{Filename, NumberOfGuesses, WordLength, WordleConfig}
import com.tcooling.wordle.parser.WordsReader
import com.tcooling.wordle.util.RandomWord

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

  override def run(args: List[String]): IO[ExitCode] = Wordle.live(config).startGame

}
