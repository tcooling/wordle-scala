package com.tcooling.wordle.model

final case class WordleConfig(
    filename: Filename,
    wordLength: WordLength,
    numberOfGuesses: NumberOfGuesses
)

opaque type Filename = String

object Filename {
  def apply(value: String): Filename               = value
  extension (filename: Filename) def value: String = filename
}

opaque type WordLength = Int

object WordLength {
  def apply(value: Int): WordLength                 = value
  extension (wordLength: WordLength) def value: Int = wordLength
}

opaque type NumberOfGuesses = Int

object NumberOfGuesses {
  def apply(value: Int): NumberOfGuesses                      = value
  extension (numberOfGuesses: NumberOfGuesses) def value: Int = numberOfGuesses
}
