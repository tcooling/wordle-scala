package com.tcooling.wordle.model

import cats.Show

enum UserInputError(val errorMessage: String) {
  case IncorrectLength(wordLength: Int) extends UserInputError(s"Guess length not equal to $wordLength")
  case NonLetterCharacter               extends UserInputError("Guess contained non letter character")
  case WordDoesNotExist                 extends UserInputError("Guess word does not exist")
}

object UserInputError {

  // TODO: make logic for reset + colour generic
  implicit val showUserInputError: Show[UserInputError] = Show.show { error =>
    Console.BLUE + error.errorMessage + Console.RESET
  }

}
