package com.tcooling.wordle

import com.tcooling.wordle.game.Wordle
import com.tcooling.wordle.input.UserInputGuessConnector
import com.tcooling.wordle.model.{Filename, NumberOfGuesses, WordLength, WordleConfig}
import com.tcooling.wordle.parser.WordsReader
import com.tcooling.wordle.util.RandomWord

object Main extends App {

  private val filename: Filename               = Filename.apply("words.txt")
  private val wordLength: WordLength           = WordLength.apply(5)
  private val numberOfGuesses: NumberOfGuesses = NumberOfGuesses.apply(6)

  private val config = WordleConfig(
    filename = filename,
    wordLength = wordLength,
    numberOfGuesses = numberOfGuesses
  )

  Wordle(config, WordsReader, RandomWord.chooseRandomWord, UserInputGuessConnector).startGame()

}
