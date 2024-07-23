package mingwey.game.model
import mingwey.game.view.GameController
import scalafx.beans.property.ObjectProperty

class Bone() {
  val damage: Int = 100
  var img: ObjectProperty[String] = ObjectProperty("/Image/bone.png")
  val angle = 30
  var gravitational_force = 10

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




}