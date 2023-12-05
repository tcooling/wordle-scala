package com.tcooling.wordle.model

/**
 * This ADT represents every possible state in a Wordle game. The first state will always be Start and the final
 * state will always be Exit.
 */
sealed trait FSM

object FSM {
  case object Start extends FSM
  case object PrintHelp extends FSM
  case object PrintGameBoard extends FSM
  case object CheckForWinOrLoss extends FSM
  case object UserInputGuess extends FSM
  case object Win extends FSM
  case object Lose extends FSM
  case object Exit extends FSM
}
