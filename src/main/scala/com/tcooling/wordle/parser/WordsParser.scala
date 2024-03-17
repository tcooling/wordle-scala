package com.tcooling.wordle.parser

import cats.{Applicative, Monad}
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import cats.implicits.*
import cats.effect.*
import cats.syntax.all.*
import cats.effect.{IO, Resource}
import cats.data.{EitherT, NonEmptyList, NonEmptySet}
import com.tcooling.wordle.model.WordleConfig
import com.tcooling.wordle.model.{Filename, WordLength, WordsParserError}
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}

trait WordsParser[F[_]] {
  def parseWords(): F[Either[WordsParserError, NonEmptySet[String]]]
}

object WordsParser {
  def apply[F[_] : Applicative : Monad : Async](fileReader: FileReader[F], config: WordleConfig): WordsParser[F] =
    new WordsParser[F] {
      override def parseWords(): F[Either[WordsParserError, NonEmptySet[String]]] = {

        val fileReaderR: Resource[F, List[String]] = fileReader.getLines(config.filename)

        // TODO: performance test if parTraverse is faster than not here
        val xyz = {
          for {
            lines         <- EitherT(fileReaderR.use(_.pure).attempt.map(_.leftMap(_ => FileParseError)))
            nonEmptyLines <- EitherT.fromEither(NonEmptyList.fromList(lines).map(_.toNes).toRight(EmptyFileError))
            validatedLines <- EitherT(
              nonEmptyLines.toSortedSet.toList
                .parTraverse(parseLine)
                .map(maybeValidLines =>
                  NonEmptyList.fromList(maybeValidLines.flatten).map(_.toNes).toRight(InvalidWordsError)))
          } yield validatedLines
        }.value
      }

      private def parseLine(line: String): F[Option[String]] = {
        val validatedLine =
          if (line.length == config.wordLength.value && WordRegex
              .validate[Boolean](_, ifMatchesRegex = true, ifDoesNotMatchRegex = false)) {
            Some(line.toUpperCase)
          } else {
            None
          }
        validatedLine.pure
      }
    }
}
