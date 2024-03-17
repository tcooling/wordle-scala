package com.tcooling.wordle.model

import cats.Show
import cats.implicits.toShow
import com.tcooling.wordle.util.ShowWithColour

/**
 * For a particular letter in the users guess, encapsulate the result of the guess
 * @param consoleColour
 *   the colour in which to print the guess letter when displaying the board
 */
enum LetterGuess(val consoleColour: String) {
  val letter: Char

  /**
   * The given letter is not present anywhere in the target word
   */
  case Incorrect(letter: Char) extends LetterGuess(Console.RED)

  /**
   * The given letter is present in the target word but in a different position
   */
  case WrongPosition(letter: Char) extends LetterGuess(Console.YELLOW)

  /**
   * The given letter is in the correct position in the target word
   */
  case Correct(letter: Char) extends LetterGuess(Console.GREEN)
}

object LetterGuess {
  given ShowWithColour[LetterGuess] = ShowWithColour.showWithColour(_.consoleColour, _.letter.show)
}
