package com.tcooling.wordle.parser

import cats.data.NonEmptySet
import com.tcooling.wordle.util.Syntax.toEither
import com.tcooling.wordle.model.{UserInputError, UserInputGuess, WordLength}
import com.tcooling.wordle.model.UserInputError.{IncorrectLength, NonLetterCharacter, WordDoesNotExist}

object UserInputParser {

  /**
   * Validate that the user input is of the correct length, only contains valid characters and is a valid word.
   */
  def parseGuess(
      allWords: NonEmptySet[String],
      userInput: UserInputGuess.Type,
      wordLength: WordLength.Type
  ): Either[UserInputError, UserInputGuess.Type] =
    for {
      _     <- (userInput.value.length == wordLength.value).toEither(IncorrectLength(wordLength.value), userInput)
      guess <- WordRegex.validate(userInput.value).toEither(NonLetterCharacter, userInput)
      upperCaseGuess = UserInputGuess(guess.value.toUpperCase)
      validGuess <- allWords.contains(upperCaseGuess.value).toEither(WordDoesNotExist, upperCaseGuess)
    } yield validGuess

}
