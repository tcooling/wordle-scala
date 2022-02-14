package com.tcooling.wordle.input

import cats.data.NonEmptySet
import com.tcooling.wordle.model.WordleConfig

final class ComputerInputGuessConnector(wordSet: NonEmptySet[String], wordleConfig: WordleConfig) extends GuessInputConnector {

  override def getUserInput(): String = "TODO"

}
