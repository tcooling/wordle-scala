package com.tcooling.wordle.model

/**
 * This enum represents every possible state in a Wordle game. The first state will always be Start and the final state
 * will always be Exit. The looping occurs in the following state:
 *   - [[CheckForWinOrLoss]] the next state will be [[Win]] or [[Lose]] or [[UserInputGuess]] (if guesses remain)
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
