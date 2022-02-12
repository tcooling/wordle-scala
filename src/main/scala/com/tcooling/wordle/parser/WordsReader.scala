package com.tcooling.wordle.parser

import scala.io.Source
import scala.util.Try

object WordsReader extends FileReader {

  override def getLines(filename: String): Try[List[String]] =
    Try {
      val bufferedSource = Source.fromResource(filename)
      val lines          = bufferedSource.getLines().toList
      bufferedSource.close()
      lines
    }

}
