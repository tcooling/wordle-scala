package com.tcooling.wordle.game

import com.tcooling.wordle.model.{boardRow, NumberOfGuesses, WordGuess, WordLength}

import scala.annotation.tailrec

object GameBoard {

  val StartSeparator: Char = '['
  val EndSeparator: Char   = ']'

  /**
   * Generate the game board and put each user guess on the board in the correct colour
   */
  def generateGameBoard(wordLength: WordLength,
                        numberOfGuesses: NumberOfGuesses,
                        previousGuesses: List[WordGuess]): List[String] = {
    val emptyRow = List.fill(wordLength.value)(s"$StartSeparator $EndSeparator")

    @tailrec
    def loop(previousGuesses: List[WordGuess], numberOfGuesses: Int, gameBoard: List[String]): List[String] =
      if (numberOfGuesses == 0) gameBoard
      else
        previousGuesses match {
          case head :: tail => loop(tail, numberOfGuesses - 1, gameBoard :+ head.boardRow)
          case Nil          => loop(Nil, numberOfGuesses - 1, gameBoard :+ emptyRow.mkString)
        }

    loop(previousGuesses, numberOfGuesses.value, gameBoard = Nil)
  }

}
