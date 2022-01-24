package com.tcooling.wordle.input

import com.tcooling.wordle.model.GameState

object UserInputGuessConnector extends GuessInputConnector {
  override def getUserInput(gameState: GameState): String = scala.io.StdIn.readLine()
}
