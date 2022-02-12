package com.tcooling.wordle.input

import cats.data.NonEmptySet
import com.tcooling.wordle.model.{GameState, WordleConfig}

final class ComputerInputGuessConnector(wordSet: NonEmptySet[String], wordleConfig: WordleConfig) extends GuessInputConnector {

  override def getUserInput(gameState: GameState): String = "TODO"

}
