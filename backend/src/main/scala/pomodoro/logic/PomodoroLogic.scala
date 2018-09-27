package pomodoro.logic

import java.util.UUID

import pomodoro.model._
import pomodoro.model.wsmessage._
import pomodoro.repository.PomodoroRepo

import scala.concurrent.{ExecutionContext, Future}

class PomodoroLogic(pomodoroRepo: PomodoroRepo)(implicit ec: ExecutionContext) {

  def getState(userId: UUID): Future[PomodoroState] = {
    pomodoroRepo.getState(userId)
  }

  def stateChanges(userId: UUID,
                   message: UserRequest,
                   currentState: PomodoroState): MessageType =
    (message, currentState) match {
      case (RequestInit, _) =>
        Self(currentState)

      case (StartPomodoro, Idle) =>
        startPomodoro(userId)
        BroadCast(Running(currentTime))

      case (StartBreak(kind), Running(started)) =>
        startBreak(userId, kind)
        BroadCast(Break(kind, currentTime))

      case (EndPomodoro, Running(started)) =>
        finishPomodoro(userId)
        BroadCast(Idle)

      case (EndBreak, _: Break) =>
        finishPomodoro(userId)
        BroadCast(Idle)

      case _ => NoMessage
    }

  private def startPomodoro(userId: UUID) =
    pomodoroRepo.start(userId, "pomodoro", currentTime)

  private def finishPomodoro(userId: UUID) =
    pomodoroRepo.finish(userId)

  private def startBreak(userId: UUID, kind: String) =
    pomodoroRepo
      .finish(userId)
      .flatMap(_ => pomodoroRepo.start(userId, kind, currentTime))

  private def currentTime: Long =
    System.currentTimeMillis()

  private implicit class SomeOps[A](a: A) {
    def some: Option[A] = Some(a)
  }
}
