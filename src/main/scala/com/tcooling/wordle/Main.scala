package com.tcooling.wordle

import cats.effect.*
import cats.effect.IO
import com.tcooling.wordle.game.Wordle
import com.tcooling.wordle.model.{Filename, NumberOfGuesses, WordLength, WordleConfig}

// TODO: use live methods - hexagonal architecture
object Main extends IOApp {

  private val config = WordleConfig(
    filename = Filename("wordadsadasds.txt"),
    wordLength = WordLength(5),
    numberOfGuesses = NumberOfGuesses(6)
  )

  override def run(args: List[String]): IO[ExitCode] = Wordle.live[IO](config).startGame

}
