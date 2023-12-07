package com.tcooling.wordle.model

import cats.implicits.toShow
import com.tcooling.wordle.model.UserInputError.{
  showUserInputError,
  IncorrectLength,
  NonLetterCharacter,
  WordDoesNotExist
}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class UserInputErrorTest extends AnyWordSpecLike with Matchers {

  private val blue: String  = Console.BLUE
  private val reset: String = Console.RESET

  "UserInputError" should {
    "show an error in blue" in {
      val errors: List[UserInputError] = List(IncorrectLength(1), NonLetterCharacter, WordDoesNotExist)
      errors.foreach { error =>
        withClue(s"for an error of type $error") {
          error.show shouldBe (blue + error.errorMessage + reset)
        }
      }
    }
  }
}
