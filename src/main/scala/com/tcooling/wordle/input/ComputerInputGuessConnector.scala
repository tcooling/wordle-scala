package com.tcooling.wordle.input

import cats.data.NonEmptySet
import com.tcooling.wordle.model.{WordGuess, WordleConfig}

final class ComputerInputGuessConnector(wordSet: NonEmptySet[String], wordleConfig: WordleConfig) extends GuessInputConnector {

  override def getUserInput(previousGuesses: List[WordGuess]): String = "TODO"

}
