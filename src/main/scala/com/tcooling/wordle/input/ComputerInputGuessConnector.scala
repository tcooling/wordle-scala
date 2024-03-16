package com.tcooling.wordle.input

import com.tcooling.wordle.model.UserInputGuess
import cats.Applicative
import cats.effect.syntax.all.*
import cats.syntax.all.*

object ComputerInputGuessConnector {
  def apply[F[_] : Applicative](): GuessInputConnector[F] = new GuessInputConnector[F] {

    /**
     * When implementing, will need to pass the word set and config to this class
     */
    override def getUserInput: F[UserInputGuess] = UserInputGuess("TODO").pure
  }

}
