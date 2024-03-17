package com.tcooling.wordle.parser

import cats.data.NonEmptySet
import com.tcooling.wordle.model.{UserInputError, UserInputGuess, WordLength}
import com.tcooling.wordle.model.UserInputError.{IncorrectLength, NonLetterCharacter, WordDoesNotExist}

object UserInputParser {

  /**
   * Validate that the user input is of the correct length, only contains valid characters and is a valid word.
   */
  def parseGuess(allWords: NonEmptySet[String],
                 userInput: UserInputGuess,
                 wordLength: WordLength): Either[UserInputError, UserInputGuess] =
    for {
      _     <- (userInput.value.length == wordLength.value).toEither(IncorrectLength(wordLength.value), userInput)
      guess <- WordRegex.validate(userInput.value).toEither(NonLetterCharacter, userInput)
      upperCaseGuess = UserInputGuess(guess.value.toUpperCase)
      validGuess <- allWords.contains(upperCaseGuess.value).toEither(WordDoesNotExist, upperCaseGuess)
    } yield validGuess

  extension [L, R](predicate: Boolean) {
    private def toEither(left: L, right: R): Either[L, R] = if (predicate) Right(right) else Left(left)
  }

}
