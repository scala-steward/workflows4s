package workflows4s.wio

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant

class WIOEndTest extends AnyFreeSpec with Matchers {

  import TestCtx.*

  "WIO.End" - {

    "state" in {
      val wf: ActiveWorkflow[Ctx] = WIO.end.toWorkflow("initialState")

      val state = wf.liveState(Instant.now)

      assert(state == "initialState")
    }

    "proceed no-op" in {
      val wf: ActiveWorkflow[Ctx] = WIO.end.toWorkflow("initialState")

      val resultOpt = wf.proceed(Instant.now)

      assert(resultOpt.isEmpty)
    }

    "event handling no-op" in {
      val wf: ActiveWorkflow[Ctx] = WIO.end.toWorkflow("initialState")

      val resultOpt = wf.handleEvent("my-event", Instant.now)

      assert(resultOpt.isEmpty)
    }

    "handle signal no-op" in {
      val wf: ActiveWorkflow[Ctx] = WIO.end.toWorkflow("initialState")

      val resultOpt = wf.handleSignal(SignalDef[String, String]())("", Instant.now)
      assert(resultOpt.isEmpty)
    }
  }
}
