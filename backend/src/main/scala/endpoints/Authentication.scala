package endpoints

import model.GeneratedPassword
import repository.postgres.{PasswordRepo, UserRepo}

import scala.concurrent.{ExecutionContext, Future}

class Authentication(implicit userRepo: UserRepo,
                     passwordRepo: PasswordRepo,
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
