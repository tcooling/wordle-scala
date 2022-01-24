package com.tcooling.wordle.parser

import scala.util.Try

/**
 * Implement for a particular type of file, e.g. words .txt file
 */
trait FileReader {
  def getLines(filename: String): Try[List[String]]
}
