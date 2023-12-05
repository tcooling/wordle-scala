//package com.tcooling.wordle.game
//
//import cats.data.{NonEmptyList, NonEmptySet}
//import com.tcooling.wordle.input.GuessInputConnector
//import com.tcooling.wordle.model.FSM.*
//import com.tcooling.wordle.model.{FSM, WordGuess, WordleConfig}
//import org.scalamock.scalatest.MockFactory
//import org.scalatest.wordspec.AnyWordSpecLike
//import org.scalatest.Matchers
//
//final class WordleFSMTest extends AnyWordSpecLike with Matchers with MockFactory {
//
//  private val wordleConfig: WordleConfig = WordleConfig(
//    filename = "words.txt",
//    wordLength = 5,
//    numberOfGuesses = 6
//  )
//
//  private val word1:          String              = "HELLO"
//  private val word2:          String              = "CLICK"
//  private val word3:          String              = "HORSE"
//  private val word4:          String              = "GREAT"
//  private val word5:          String              = "ELDER"
//  private val word6:          String              = "CLEAN"
//  private val targetWord:     String              = "VAGUE"
//  private val missingWord:    String              = "ABCDE"
//  private val allWords:       NonEmptySet[String] = NonEmptyList.of(targetWord, word1, word2, word3, word4, word5, word6).toNes
//  private val guessConnector: GuessInputConnector = mock[GuessInputConnector]
//
//  private val wordGuessF: String => WordGuess = WordGuess(_, targetWord)
//  private val nextStateF: (FSM, List[WordGuess]) => (FSM, List[WordGuess]) = WordleFSM.nextState(
//    config = wordleConfig,
//    targetWord = targetWord,
//    allWords = allWords,
//    guessConnector = guessConnector
//  )(_, _)
//
//  "WordleFSM" should {
//    "return the next state and guesses for a state" when {
//      "Start" in {
//        nextStateF(Start, Nil) shouldBe (PrintHelp, Nil)
//      }
//
//      "PrintHelp" in {
//        nextStateF(PrintHelp, Nil) shouldBe (PrintGameBoard, Nil)
//      }
//
//      "PrintGameBoard" in {
//        nextStateF(PrintGameBoard, Nil) shouldBe (CheckForWinOrLoss, Nil)
//      }
//
//      "CheckForWinOrLoss" when {
//        "no guesses are present" in {
//          nextStateF(CheckForWinOrLoss, Nil) shouldBe (UserInputGuess, Nil)
//        }
//
//        "guesses are present" when {
//          "all the guesses have been used" in {
//            val guesses: List[WordGuess] = List(word1, word2, word3, word4, word5, word6).map(wordGuessF)
//            nextStateF(CheckForWinOrLoss, guesses) shouldBe (Lose, guesses)
//          }
//
//          "the first guess is correct" in {
//            val guesses: List[WordGuess] = List(targetWord).map(wordGuessF)
//            nextStateF(CheckForWinOrLoss, guesses) shouldBe (Win, guesses)
//          }
//
//          "the last guess is correct" in {
//            val guesses: List[WordGuess] = List(word1, word2, word3, word4, word5, targetWord).map(wordGuessF)
//            nextStateF(CheckForWinOrLoss, guesses) shouldBe (Win, guesses)
//          }
//        }
//      }
//
//      "UserInputGuess" when {
//        "a valid guess is input" when {
//          "the guess is in lowercase" in {
//            (guessConnector.getUserInput _).expects().returns(word1.toLowerCase)
//            nextStateF(UserInputGuess, Nil) shouldBe (PrintGameBoard, List(word1).map(wordGuessF))
//          }
//
//          "the guess is in uppercase" in {
//            (guessConnector.getUserInput _).expects().returns(word1)
//            nextStateF(UserInputGuess, Nil) shouldBe (PrintGameBoard, List(word1).map(wordGuessF))
//          }
//
//          "when the guess has already been input previously" in {
//            (guessConnector.getUserInput _).expects().returns(word1)
//            nextStateF(UserInputGuess, List(word1).map(wordGuessF)) shouldBe
//              (PrintGameBoard, List(word1, word1).map(wordGuessF))
//          }
//        }
//
//        "an invalid guess is input" when {
//          "the guess is the wrong length" when {
//            "empty" in {
//              (guessConnector.getUserInput _).expects().returns("")
//              nextStateF(UserInputGuess, Nil) shouldBe (PrintGameBoard, Nil)
//            }
//
//            "too short" in {
//              (guessConnector.getUserInput _).expects().returns(word1.tail)
//              nextStateF(UserInputGuess, Nil) shouldBe (PrintGameBoard, Nil)
//            }
//
//            "too long" in {
//              (guessConnector.getUserInput _).expects().returns(word1 + "Q")
//              nextStateF(UserInputGuess, Nil) shouldBe (PrintGameBoard, Nil)
//            }
//          }
//
//          "the guess does not match the regex" when {
//            "includes special characters" in {
//              (guessConnector.getUserInput _).expects().returns("&&&&&")
//              nextStateF(UserInputGuess, Nil) shouldBe (PrintGameBoard, Nil)
//            }
//
//            "includes numbers" in {
//              (guessConnector.getUserInput _).expects().returns("12345")
//              nextStateF(UserInputGuess, Nil) shouldBe (PrintGameBoard, Nil)
//            }
//          }
//
//          "the guess does not correspond to a word in the words file" in {
//            (guessConnector.getUserInput _).expects().returns(missingWord)
//            nextStateF(UserInputGuess, Nil) shouldBe (PrintGameBoard, Nil)
//          }
//        }
//      }
//
//      "Win" in {
//        nextStateF(Win, List(targetWord).map(wordGuessF)) shouldBe (Exit, List(targetWord).map(wordGuessF))
//      }
//
//      "Lose" in {
//        nextStateF(Lose, List(word1).map(wordGuessF)) shouldBe (Exit, List(word1).map(wordGuessF))
//      }
//    }
//  }
//
//}
