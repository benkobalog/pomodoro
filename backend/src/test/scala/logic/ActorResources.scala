package logic

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait ActorResources {
  implicit val system: ActorSystem = ActorSystem("test-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
}
