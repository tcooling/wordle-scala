package com.tcooling.wordle.parser

import cats.{Applicative, Monad, Parallel}
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import cats.implicits.*
import cats.effect.*
import cats.effect.std.Console
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
  def apply[F[_] : Applicative : Monad : Parallel](config: WordleConfig, fileReader: FileReader[F]): WordsParser[F] =
    new WordsParser[F] {
      override def parseWords(): F[Either[WordsParserError, NonEmptySet[String]]] = {
        // TODO: performance test if parTraverse is faster than not here
        val fileReaderR: Resource[F, List[String]] = fileReader.getLines(config.filename)
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

  // TODO: might need ReaderT here
  def observed[F[_] : Monad : Console](config: WordleConfig, delegate: WordsParser[F]): WordsParser[F] =
    new WordsParser[F] {
      override def parseWords(): F[Either[WordsParserError, NonEmptySet[String]]] =
        delegate.parseWords().evalTap {
          case Left(FileParseError) => Console[F].println("Error parsing words file.")
          case Left(InvalidWordsError) =>
            Console[F].println("Error parsing words (possibly word length or special characters).")
          case Left(EmptyFileError) => Console[F].println("Empty words file error")
          case Right(allWords) =>
            Console[F].println(
              s"Successfully parsed ${config.filename}, read ${allWords.length} words of length ${config.wordLength.value}")
        }

      // TODO: trying to recreate fs2 evalTap, must exist already?
      // TODO: move to util
      extension [F[_] : Monad, T](valueF: F[T]) {
        def evalTap(f: T => F[Unit]): F[T] = valueF.flatMap { value =>
          f(value).as(value)
        }
      }
    }

  // TODO: file reader in reader t?
  def live[F[_] : Applicative : Parallel : Console](config: WordleConfig, fileReader: FileReader[F]): WordsParser[F] = {
    val wordsParser = WordsParser.apply(config, fileReader)
    observed(config, wordsParser)
  }
}
