package com.tcooling.wordle.model

/**
 * This enum represents every possible state in a Wordle game. The first state will always be Start and the final state
 * will always be Exit.
 */
enum FSM {
  case Start
  case PrintHelp
  case PrintGameBoard
  case CheckForWinOrLoss
  case UserInputGuess
  case Win
  case Lose
  case Exit
}
