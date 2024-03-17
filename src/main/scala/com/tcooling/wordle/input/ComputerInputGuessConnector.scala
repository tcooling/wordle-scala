package com.tcooling.wordle.input

import com.tcooling.wordle.model.UserInputGuess
import cats.Applicative
import cats.effect.syntax.all.*
import cats.syntax.all.*

object ComputerInputGuessConnector {
  def apply[F[_] : Applicative](): GuessInputConnector[F] = new GuessInputConnector[F] {
    override def getUserInput: F[UserInputGuess] = UserInputGuess("TODO").pure
  }

}
