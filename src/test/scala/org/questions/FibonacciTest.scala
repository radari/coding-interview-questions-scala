package org.questions

import org.questions.fibonacci.{Fibonacci, Iterative, Recursive, TailRecursion}
import org.scalacheck.Gen
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * @author maximn
 * @since 26-Oct-2015
 */
trait FibonacciTest extends Specification with FibonacciProperty {
  val fib: Fibonacci

  "negative input" should {
    "be invalid" in {
      fib.nth(-1) must throwA[IllegalArgumentException]
    }
  }

  "0th" should {
    "be 0" in {
      fib.nth(0) must be_===(0)
    }
  }

  "1st" should {
    "be 1" in {
      fib.nth(1) must be_===(1)
    }
  }

  "2nd" should {
    "be 1" in {
      fib.nth(1) must be_===(1)
    }
  }
}

trait BigN {
  self: FibonacciTest =>

  import scala.concurrent.ExecutionContext.Implicits.global

  "big N" should {
    "be computed in sub seconds" in {
      val calcBigN = Future.apply {
        fib.nth(100)
      }

      Await.ready(calcBigN, Duration("1 second")).isCompleted must beTrue
    }
  }
}

trait FibonacciProperty extends ScalaCheck {
  self: FibonacciTest =>

  val smallInteger = Gen.choose[Int](2,30)

  "fib(n)" should {
    "be equal to the sum of the results of last 2 elements" >> prop { n: Int =>
      fib.nth(n) must be_===(fib.nth(n - 1) + fib.nth(n - 2))
    }.setGen(smallInteger)
  }
}

class RecursiveTest extends FibonacciTest {
  override val fib = new Recursive
}

class IterativeTest extends FibonacciTest with BigN {
  override val fib = new Iterative
}

class TailRecursionTest extends FibonacciTest with BigN {
  override val fib: Fibonacci = new TailRecursion
}