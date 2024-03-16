package com.tcooling.wordle.model

opaque type UserInputGuess = String

object UserInputGuess {
  def apply(value: String): UserInputGuess                     = value
  extension (userInputGuess: UserInputGuess) def value: String = userInputGuess
}
