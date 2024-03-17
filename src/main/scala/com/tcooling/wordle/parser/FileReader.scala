package com.tcooling.wordle.parser

import cats.effect.Resource
import cats.effect.kernel.Sync
import com.tcooling.wordle.model.Filename

import scala.io.Source

trait FileReader[F[_]] {
  def getLines(filename: Filename.Type): Resource[F, List[String]]
}

object FileReader {
  def apply[F[_] : Sync](): FileReader[F] = new FileReader[F] {
    override def getLines(filename: Filename.Type): Resource[F, List[String]] =
      Resource
        .make(Sync[F].blocking(Source.fromResource(filename.value)))(source => Sync[F].delay(source.close()))
        .map(_.getLines().toList)
  }
}
