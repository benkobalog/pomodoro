package repository

import model.User

trait UserRepo {
  def findByEmail(email:String)
}
