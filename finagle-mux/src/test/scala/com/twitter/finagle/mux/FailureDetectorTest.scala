package com.twitter.finagle.mux

import com.twitter.finagle.stats.NullStatsReceiver
import com.twitter.util.Future
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FailureDetectorTest extends FunSuite
{
  def ping = () => Future.Done
  def close = () => Future.Done
  val statsReceiver = NullStatsReceiver

  test("default settings") {
    val FailureDetector.Param(failDetectorConfig) = FailureDetector.Param.param.default
    assert(NullFailureDetector == FailureDetector(failDetectorConfig, ping, close, statsReceiver))
  }

  test("default settings with flag override") {
    sessionFailureDetector.let("threshold") {
      val FailureDetector.Param(failDetectorConfig) = FailureDetector.Param.param.default
      assert(FailureDetector(failDetectorConfig, ping, close, statsReceiver).isInstanceOf[ThresholdFailureDetector])
    }
  }

  test("flag settings with flag set to none") {
    sessionFailureDetector.let("none") {
      assert(NullFailureDetector == FailureDetector(FailureDetector.GlobalFlagConfig, ping, close, statsReceiver))
    }
  }

  test("flag settings with invalid string") {
    sessionFailureDetector.let("tacos") {
      assert(NullFailureDetector == FailureDetector(FailureDetector.GlobalFlagConfig, ping, close, statsReceiver))
    }
  }

  test("flag settings with valid string") {
    sessionFailureDetector.let("threshold") {
      assert(FailureDetector(FailureDetector.GlobalFlagConfig, ping, close, statsReceiver).isInstanceOf[ThresholdFailureDetector])
    }
  }

  test("request null gets null") {
    assert(NullFailureDetector == FailureDetector(FailureDetector.NullConfig, ping, close, statsReceiver))
  }

  test("explicit threshold used") {
    assert(FailureDetector(new FailureDetector.ThresholdConfig, ping, close, statsReceiver).isInstanceOf[ThresholdFailureDetector])
  }
}
