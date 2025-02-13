package com.tcooling.wordle.parser

import cats.data.NonEmptySet
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
      _     <- Either.cond(userInput.value.length == wordLength.value, userInput, IncorrectLength(wordLength.value))
      guess <- Either.cond(WordRegex.validate(userInput.value), userInput, NonLetterCharacter)
      upperCaseGuess = UserInputGuess(guess.value.toUpperCase)
      validGuess <- Either.cond(allWords.contains(upperCaseGuess.value), upperCaseGuess, WordDoesNotExist)
    } yield validGuess

}
