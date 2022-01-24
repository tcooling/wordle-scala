package com.tcooling.wordle.model

/**
 * Possible errors from parsing the words file
 */
sealed trait WordsParserError

object WordsParserError {
  final case object FileParseError extends WordsParserError
  final case object EmptyFileError extends WordsParserError
  final case object InvalidWordsError extends WordsParserError
}
