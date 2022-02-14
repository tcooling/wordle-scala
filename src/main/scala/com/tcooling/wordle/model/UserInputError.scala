package com.tcooling.wordle.model

import cats.Show

sealed abstract class UserInputError(val errorMessage: String)

object UserInputError {

  implicit val showUserInputError: Show[UserInputError] = Show.show { error =>
    Console.BLUE + error.errorMessage + Console.RESET
  }

  final case class IncorrectLength(wordLength: Int) extends UserInputError(s"Guess length not equal to $wordLength")
  final case object NonLetterCharacter extends UserInputError("Guess contained non letter character")
  final case object WordDoesNotExist extends UserInputError("Guess word does not exist")
}
