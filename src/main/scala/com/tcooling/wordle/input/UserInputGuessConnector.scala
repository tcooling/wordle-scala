package com.tcooling.wordle.input

import com.tcooling.wordle.model.UserInputGuess
import cats.Monad
import cats.effect.std.Console
import cats.effect.syntax.all.*
import cats.syntax.all.*

object UserInputGuessConnector {
  def apply[F[_] : Console : Monad](): GuessInputConnector[F] = new GuessInputConnector[F] {
    // TODO: do I need a Sync[F].blocking?
    override def getUserInput: F[UserInputGuess] = Console[F].readLine.map(UserInputGuess.apply)
  }
}
