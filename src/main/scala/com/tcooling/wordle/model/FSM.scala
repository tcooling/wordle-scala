package com.tcooling.wordle.model

/**
 * This ADT represents every possible state in a Wordle game. The first state will always be Start and the final
 * state will always be Exit.
 */
sealed trait FSM

object FSM {
  final case object Start extends FSM
  final case object PrintHelp extends FSM
  final case object PrintGameBoard extends FSM
  final case object CheckForWinOrLoss extends FSM
  final case object UserInputGuess extends FSM
  final case object Win extends FSM
  final case object Lose extends FSM
  final case object Exit extends FSM
}
