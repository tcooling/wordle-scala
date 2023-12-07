package com.tcooling.wordle.model

/**
 * This enum represents every possible state in a Wordle game. The first state will always be Start and the final state
 * will always be Exit.
 */
enum FSM {
  case Start, PrintHelp, PrintGameBoard, CheckForWinOrLoss, UserInputGuess, Win, Lose, Exit
}
