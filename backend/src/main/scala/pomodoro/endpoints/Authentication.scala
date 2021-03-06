package pomodoro.endpoints

import pomodoro.model.GeneratedPassword
import pomodoro.repository.postgres.{PasswordRepo, UserRepo}

import scala.concurrent.{ExecutionContext, Future}

class Authentication(userRepo: UserRepo, passwordRepo: PasswordRepo)(
    implicit
    ec: ExecutionContext) {

  def getPasswordByEmail(email: String): Future[Option[GeneratedPassword]] = {
    for {
      userOpt <- userRepo.findByEmail(email)
      pwOpt <- userOpt match {
        case Some(user) => passwordRepo.getPasswordByUserId(user.id)
        case None       => Future.successful(None)
      }
    } yield pwOpt
  }
}
