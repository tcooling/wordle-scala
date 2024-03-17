package com.tcooling.wordle.input

import com.tcooling.wordle.model.UserInputGuess
import cats.Monad
import cats.effect.kernel.Sync
import cats.effect.std.Console
import cats.syntax.all.*

object UserInputGuessConnector {
  def apply[F[_] : Console : Monad : Sync](): GuessInputConnector[F] = new GuessInputConnector[F] {
    override def getUserInput: F[UserInputGuess.Type] =
      Sync[F]
        .defer(Console[F].readLine)
        .map(UserInputGuess(_))
  }
}
