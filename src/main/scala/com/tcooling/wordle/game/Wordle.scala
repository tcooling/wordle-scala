package com.tcooling.wordle.game

import cats.{Applicative, Monad, Parallel}
import cats.data.ReaderT
import cats.data.NonEmptySet
import cats.effect.kernel.Sync
import cats.implicits.*
import cats.effect.std.Console
import cats.effect.ExitCode
import com.tcooling.wordle.game.WordleFSM.State
import com.tcooling.wordle.model.FSM.{Exit, Start}
import com.tcooling.wordle.model.{FSM, TargetWord, WordGuess, WordleConfig, WordsParserError}
import com.tcooling.wordle.parser.{FileReader, WordsParser}
import com.tcooling.wordle.util.RandomWord

trait Wordle[F[_]] {
  def startGame: F[ExitCode]
}

// TODO: cancellable? e.g. user presses ctrl + c
object Wordle {
  def apply[F[_] : Applicative : Monad](
      config: WordleConfig,
      wordsParser: WordsParser[F],
      wordleFSM: WordleFSM[F],
      randomWord: RandomWord[F]
  ): Wordle[F] = new Wordle[F] {

    override def startGame: F[ExitCode] =
      wordsParser.parseWords().flatMap {
        case Left(_) => ExitCode.Error.pure
        case Right(words) =>
          randomWord
            .chooseRandomWord(words)
            .flatMap(gameLoop(words, _))
            .as(ExitCode.Success)
      }

    // TODO: handle ctrl + c from inside loop, currently program does not exit elegantly
    private def gameLoop(allWords: NonEmptySet[String], targetWord: TargetWord.Type): F[State] = {
      val wordleFSMNextState = wordleFSM.nextState(allWords, targetWord)

      def loop(state: FSM, guesses: List[WordGuess]): F[State] = state match {
        case Exit => (Exit -> guesses).pure
        case _    => wordleFSMNextState(state, guesses).flatMap(loop.tupled)
      }

      loop(state = Start, guesses = List.empty[WordGuess])
    }
  }

  def live[F[_] : Sync : Console : Parallel]: ReaderT[F, WordleConfig, Wordle[F]] =
    ReaderT { config =>
      val fileReader: FileReader[F] = FileReader.apply()
      val wordsParserF              = WordsParser.live(fileReader).apply(config)
      val wordleFSM: WordleFSM[F]   = WordleFSM.live(config)
      val randomWord: RandomWord[F] = RandomWord.apply()
      wordsParserF.map { wordsParser =>
        Wordle[F].apply(config, wordsParser, wordleFSM, randomWord)
      }
    }
}
