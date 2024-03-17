package com.tcooling.wordle.util

trait ShowWithColour[A] {
  def showWithColour(a: A): String

  extension [A : ShowWithColour](a: A) {
    def showWithColour: String = ShowWithColour[A].showWithColour(a)
  }
}

object ShowWithColour {

  def apply[A](using sh: ShowWithColour[A]): ShowWithColour[A] = sh

  /**
   * The console needs to be reset to prevent subsequent characters using the previously used colour.
   */
  def showWithColour[A](consoleColour: String, printF: A => String): ShowWithColour[A] =
    consoleColour + printF(_) + Console.RESET

  def showWithColour[A](consoleColourF: A => String, printF: A => String): ShowWithColour[A] = { a =>
    consoleColourF(a) + printF(a) + Console.RESET
  }

}
