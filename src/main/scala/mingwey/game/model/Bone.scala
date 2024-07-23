package mingwey.game.model
import mingwey.game.view.GameController
import scalafx.beans.property.ObjectProperty

class Bone() {
  val damage: Int = 100
  var img: ObjectProperty[String] = ObjectProperty("/Image/bone.png")
  val angle = 30
  var gravitational_force = 10
  var xCoordinate :(Double, Double) = (912,944)
  var yCoordinate :(Double, Double) = (388,391)

  def getFlightTime(velocity : Double): Double = {
    val vertical_velocity = velocity * Math.cos(Math.toRadians(angle))
    val flightime = (2 * vertical_velocity) / gravitational_force
    flightime
  }

  def arc(velocity : Double, time: Double): (Double, Double) = {
    val horizontal_velocity = velocity * Math.sin(Math.toRadians(angle))
    val vertical_velocity = velocity * Math.cos(Math.toRadians(angle))

    var x = horizontal_velocity * time
    var y = vertical_velocity * time  - gravitational_force * time * time / 2
    (x,y)
  }

  def intersects(target: Character): Boolean = {
    val overlapX = this.xCoordinate._1 < target.xCoordinate._2 && this.xCoordinate._2 > target.xCoordinate._1
    val overlapY = this.yCoordinate._1 < target.yCoordinate._2 && this.yCoordinate._2 > target.yCoordinate._1
    overlapX && overlapY
  }

  def checkIntersects(target: Character): Unit = {
    if (intersects(target: Character)){
      println("Target is hit")
    } else {
      println("Target is not hit")
    }
  }


}