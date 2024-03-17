package com.tcooling.wordle.model

import com.tcooling.wordle.util.ShowWithColour

enum UserInputError(val errorMessage: String) {
  case IncorrectLength(wordLength: Int) extends UserInputError(s"Guess length not equal to $wordLength")
  case NonLetterCharacter               extends UserInputError("Guess contained non letter character")
  case WordDoesNotExist                 extends UserInputError("Guess word does not exist")
}

object UserInputError {
  given ShowWithColour[UserInputError] = ShowWithColour.showWithColour(Console.BLUE, _.errorMessage)
}
