package com.tcooling.wordle.game

import com.tcooling.wordle.model.{boardRow, WordGuess}

import scala.annotation.tailrec

object GameBoard {

  val startSeparator: Char = '['
  val endSeparator: Char   = ']'

  /**
   * Generate the game board and put each user guess on the board in the correct colour
   */
  def generateGameBoard(wordLength: Int, numberOfGuesses: Int, previousGuesses: List[WordGuess]): List[String] = {
    val emptyRow = List.fill(wordLength)(s"$startSeparator $endSeparator")

    @tailrec
    def loop(previousGuesses: List[WordGuess], numberOfGuesses: Int, gameBoard: List[String]): List[String] =
      if (numberOfGuesses == 0) gameBoard
      else
        previousGuesses match {
          case head :: tail => loop(tail, numberOfGuesses - 1, gameBoard :+ head.boardRow)
          case Nil          => loop(Nil, numberOfGuesses - 1, gameBoard :+ emptyRow.mkString)
        }

    loop(previousGuesses, numberOfGuesses, gameBoard = Nil)
  }

}
