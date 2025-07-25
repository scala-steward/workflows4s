package workflows4s.wio.builders

import cats.implicits.catsSyntaxOptionId
import workflows4s.wio.*
import workflows4s.wio.internal.{EventHandler, SignalHandler}
import workflows4s.wio.model.{ModelUtils, WIOMeta}

import scala.concurrent.duration.FiniteDuration
import scala.jdk.DurationConverters.*

object DraftBuilder {
  private val draftSignal = SignalDef[Unit, Unit]()

  trait Step0[Ctx <: WorkflowContext]() {

    def draft: DraftBuilderStep1 = DraftBuilderStep1()

    class DraftBuilderStep1 {
      def signal(name: String = null, error: String = null)(using autoName: sourcecode.Name): WIO.Draft[Ctx]                                  = WIO.HandleSignal(
        draftSignal,
        SignalHandler[Unit, Unit, Any]((_, _) => ???),
        dummyEventHandler,
        WIO.HandleSignal.Meta(
          Option(error).map(ErrorMeta.Present(_)).getOrElse(ErrorMeta.noError),
          getEffectiveName(name, autoName),
          None,
        ),
      )
      def timer(name: String = null, duration: FiniteDuration = null)(using autoName: sourcecode.Name): WIO.Timer[Ctx, Any, Nothing, Nothing] =
        WIO.Timer(
          Option(duration) match {
            case Some(value) => WIO.Timer.DurationSource.Static(value.toJava)
            case None        => WIO.Timer.DurationSource.Dynamic(_ => ???)
          },
          dummyEventHandler,
          getEffectiveName(name, autoName).some,
          dummyEventHandler,
        )

      def step(name: String = null, error: String = null, description: String = null)(using autoName: sourcecode.Name): WIO.Draft[Ctx] = WIO.RunIO(
        _ => ???,
        dummyEventHandler,
        WIO.RunIO.Meta(
          Option(error).map(ErrorMeta.Present(_)).getOrElse(ErrorMeta.noError),
          getEffectiveName(name, autoName).some,
          Option(description),
        ),
      )

      def forEach(forEach: WIO.Draft[Ctx], name: String = null)(using autoName: sourcecode.Name): WIO.Draft[Ctx] = {
        val effName = getEffectiveName(name, autoName).some
        WIO.ForEach(_ => ???, forEach, () => ???, null, _ => ???, (_, _, _) => ???, (_, _) => ???, None, null, WIOMeta.ForEach(effName))
      }

      def repeat(conditionName: String = null, releaseBranchName: String = null, restartBranchName: String = null)(
          body: WIO.Draft[Ctx],
          onRestart: WIO.Draft[Ctx] = null,
      ): WIO.Draft[Ctx] = {
        val base: WIO[WCState[Ctx], Nothing, WCState[Ctx], Ctx] = Option(onRestart) match {
          case Some(_) => WIO.build[Ctx].repeat(body).until(_ => ???).onRestart(onRestart).named(conditionName, releaseBranchName, restartBranchName)
          case None    => WIO.build[Ctx].repeat(body).until(_ => ???).onRestartContinue.named(conditionName, releaseBranchName, restartBranchName)
        }
        base.transformInput((_: Any) => ???).map(_ => ???)
      }

    }

  }

  private def dummyEventHandler[EventBase, Evt]: EventHandler[Any, Nothing, EventBase, Evt] = EventHandler(_ => ???, _ => ???, (_, _) => ???)

  private def getEffectiveName(name: String, autoName: sourcecode.Name): String =
    Option(name).getOrElse(ModelUtils.prettifyName(autoName.value))

}
