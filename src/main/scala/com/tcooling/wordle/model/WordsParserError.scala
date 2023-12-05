package com.tcooling.wordle.model

/**
 * Possible errors from parsing the words file
 */
sealed trait WordsParserError

object WordsParserError {
  case object FileParseError extends WordsParserError
  case object EmptyFileError extends WordsParserError
  case object InvalidWordsError extends WordsParserError
}
