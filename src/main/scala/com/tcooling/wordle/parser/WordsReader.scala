package com.tcooling.wordle.parser

import cats.syntax.all.*
import cats.effect.IO
import cats.effect.Resource
import com.tcooling.wordle.model.Filename

import scala.io.{BufferedSource, Source}

object WordsReader extends FileReader {

  override def getLines(filename: Filename): Resource[IO, List[String]] = {
    val source: Resource[IO, BufferedSource] =
      Resource.make(IO.blocking(Source.fromResource(filename.value)))(_.close.pure)

    source.map(_.getLines().toList)
  }

}
