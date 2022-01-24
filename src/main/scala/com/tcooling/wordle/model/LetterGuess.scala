package com.tcooling.wordle.model

/**
 * For a particular letter in the users guess, encapsulate the result of the guess
 * @param consoleColour the colour in which to print the guess letter when displaying the board
 */
sealed abstract class LetterGuess(val consoleColour: String) {
  val letter: Char
}

object LetterGuess {

  /**
   * The given letter is not present anywhere in the target word
   */
  final case class IncorrectGuess(letter: Char) extends LetterGuess(Console.RED)

  /**
   * The given letter is present in the target word but in a different position
   */
  final case class WrongPositionGuess(letter: Char) extends LetterGuess(Console.YELLOW)

  /**
   * The given letter is in the correct position in the target word
   */
  final case class CorrectGuess(letter: Char) extends LetterGuess(Console.GREEN)
}