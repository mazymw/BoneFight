package mingwey.game.model
import mingwey.game.view.GameController
import scalafx.beans.property.ObjectProperty
import scala.collection.mutable.ArrayBuffer

class Bone() {
  var img: ObjectProperty[String] = ObjectProperty("/Image/bone.png")
  val angle = 75
  var gravitational_force = 10
  var xCoordinate: ArrayBuffer[Double] = ArrayBuffer(0,0)
  var yCoordinate: ArrayBuffer[Double] = ArrayBuffer(0,0)
  var boneWidth: Double = 0
  var boneHeight: Double  = 0
  var isIntercept: Boolean = false

  def getFlightTime(velocity: Double): Double = {
    // Convert angle from degrees to radians
    val vertical_velocity = velocity * Math.sin(Math.toRadians(angle))

    // Time to reach maximum height
    val timeMaxHeight =  vertical_velocity / gravitational_force

    // Maximum height
    val hMax = this.xCoordinate(0) + (Math.pow( vertical_velocity, 2) / (2 * gravitational_force))

    // Time to fall from maximum height
    val timeFall = Math.sqrt(2 * hMax / gravitational_force)

    // Total flight time
    timeMaxHeight  + timeFall
  }


  def simulateArc(velocity : Double, time: Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    val horizontal_velocity = velocity * Math.cos(Math.toRadians(angle))
    val vertical_velocity = velocity * Math.sin(Math.toRadians(angle))

    val simulatedXCoordinate = ArrayBuffer[Double](xCoordinate(0), xCoordinate(1))
    val simulatedYCoordinate = ArrayBuffer[Double](yCoordinate(0), yCoordinate(1))

    simulatedXCoordinate(0) = simulatedXCoordinate(0) + horizontal_velocity * time
    simulatedXCoordinate(1) = simulatedXCoordinate(0) + boneWidth
    simulatedYCoordinate(0) = simulatedYCoordinate(0) + vertical_velocity * time  - gravitational_force * time * time / 2
    simulatedYCoordinate(1) = simulatedYCoordinate(0) + boneHeight
    (simulatedXCoordinate, simulatedYCoordinate)
  }

  def checkIntersects(target: Character, velocity: Double, time: Double): Boolean = {
    val (simulatedXCoordinate, simulatedYCoordinate) = simulateArc(velocity, time)
    val overlapX = simulatedXCoordinate(0) < target.xCoordinate._2 && simulatedXCoordinate(1) > target.xCoordinate._1
    val overlapY = simulatedYCoordinate(0) < target.yCoordinate._2 && simulatedYCoordinate(1) > target.yCoordinate._1
    overlapX && overlapY
  }

  // might not need this(Just for checking)
  def checkIntersectsAndPrint(target: Character, velocity: Double, time: Double): Unit = {
    if (checkIntersects(target, velocity, time)){
      println("Target is hit")
    } else {
      println("Target is not hit")
    }
  }
}
