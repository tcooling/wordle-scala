package com.tcooling.wordle.parser

import com.tcooling.wordle.model.Filename
import scala.util.Try

/**
 * Implement for a particular type of file, e.g. words .txt file
 */
trait FileReader {
  def getLines(filename: Filename): Try[List[String]]
}
