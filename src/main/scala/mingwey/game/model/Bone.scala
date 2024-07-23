package mingwey.game.model
import mingwey.game.view.GameController
import scalafx.beans.property.ObjectProperty
import scala.collection.mutable.ArrayBuffer

class Bone() {
  val damage: Int = 100
  var img: ObjectProperty[String] = ObjectProperty("/Image/bone.png")
  val angle = 30
  var gravitational_force = 10
  var xCoordinate :ArrayBuffer[Double] = ArrayBuffer(0,0)
  var yCoordinate :ArrayBuffer[Double] = ArrayBuffer(0,0)

  def getFlightTime(velocity : Double): Double = {
    val vertical_velocity = velocity * Math.cos(Math.toRadians(angle))
    val flightime = (2 * vertical_velocity) / gravitational_force
    flightime
  }

  def arc(velocity : Double, time: Double): Unit = {
    val horizontal_velocity = velocity * Math.sin(Math.toRadians(angle))
    val vertical_velocity = velocity * Math.cos(Math.toRadians(angle))

    xCoordinate(0) = horizontal_velocity * time
    xCoordinate(1) = xCoordinate(1) + 1
    yCoordinate(0) = vertical_velocity * time  - gravitational_force * time * time / 2
    yCoordinate(1) = yCoordinate(1) + 1
  }

  def intersects(target: Character): Boolean = {
    val overlapX = this.xCoordinate(0) < target.xCoordinate._2 && this.xCoordinate(1) > target.xCoordinate._1
    val overlapY = this.yCoordinate(0) < target.yCoordinate._2 && this.yCoordinate(1) > target.yCoordinate._1
    overlapX && overlapY
  }


  // might not need this(Just for checking)
  def checkIntersects(target: Character): Unit = {
    if (intersects(target: Character)){
      println("Target is hit")
    } else {
      println("Target is not hit")
    }
  }


}