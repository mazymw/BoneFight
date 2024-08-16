package mingwey.game.model
import mingwey.game.MainApp.{game, player}
import mingwey.game.view.GameController
import scalafx.beans.property.ObjectProperty

import scala.collection.mutable.ArrayBuffer

class Bone() {
  var img: ObjectProperty[String] = ObjectProperty("/Image/bone.png")
//  var initialXCoordinate: ArrayBuffer[Double] = ArrayBuffer(0,0)
//  val initialYCoordinate: ArrayBuffer[Double] = ArrayBuffer(0,0)
  var xCoordinate: ArrayBuffer[Double] = ArrayBuffer(0,0)
  var yCoordinate: ArrayBuffer[Double] = ArrayBuffer(0,0)
  var boneWidth: Double = 0
  var boneHeight: Double  = 0
  val angle = 75
  var gravitational_force = 10
  var isIntercept: Boolean = false
  var flightTime: Double = 0


  def getFlightTime(velocity: Double): Double = {
    val vertical_velocity = velocity * Math.sin(Math.toRadians(angle))

    // Time to reach maximum height
    val timeMaxHeight =  vertical_velocity / gravitational_force

    // Maximum height
    val hMax = (game.backgroundHeight - 50) - this.yCoordinate(0) + (Math.pow( vertical_velocity, 2) / (2 * gravitational_force))

    // Time to fall from maximum height
    val timeFall = Math.sqrt(2 * hMax / gravitational_force)
    flightTime =  timeMaxHeight  + timeFall
    // Total flight time
    timeMaxHeight  + timeFall
  }


  def simulateArc(velocity : Double, time: Double , direction : Double, wind : Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    var horizontal_velocity = velocity * Math.cos(Math.toRadians(angle)) * direction
    val vertical_velocity = velocity * Math.sin(Math.toRadians(angle))


    if (game.currentPlayer == player){
      horizontal_velocity = horizontal_velocity + wind
      if(horizontal_velocity < 0){
        horizontal_velocity = 0
      }
    }


    val simulatedXCoordinate = ArrayBuffer[Double](xCoordinate(0), xCoordinate(1))
    val simulatedYCoordinate = ArrayBuffer[Double](yCoordinate(0), yCoordinate(1))

    simulatedXCoordinate(0) = simulatedXCoordinate(0) + horizontal_velocity * time
    simulatedXCoordinate(1) = simulatedXCoordinate(0) + boneWidth
    simulatedYCoordinate(0) = simulatedYCoordinate(0) + vertical_velocity * time  - gravitational_force * time * time / 2
    simulatedYCoordinate(1) = simulatedYCoordinate(0) + boneHeight
    (simulatedXCoordinate, simulatedYCoordinate)
  }

  def checkIntersects(target: Character, velocity: Double, time: Double,direction : Double, wind: Double): Boolean = {
    val (simulatedXCoordinate, simulatedYCoordinate) = simulateArc(velocity, time, direction : Double, wind)
    val overlapX = simulatedXCoordinate(0) < target.xCoordinate._2 && simulatedXCoordinate(1) > target.xCoordinate._1
    val overlapY = simulatedYCoordinate(0) < target.yCoordinate._2 && simulatedYCoordinate(1) > target.yCoordinate._1
    overlapX && overlapY
  }

//  def findIntersectionTime(target: Character, velocity: Double, direction: Double, timeStep: Double = 0.01): Option[Double] = {
//    val totalFlightTime = getFlightTime(velocity)
//    var time = 0.0
//    while (time <= totalFlightTime) {
//      if (checkIntersects(target, velocity, time, direction)) {
//        return Some(time)
//      }
//      time += timeStep
//    }
//    None
//  }

//  // might not need this(Just for checking)
//  def checkIntersectsAndPrint(target: Character, velocity: Double, time: Double,direction : Double): Unit = {
//    if (checkIntersects(target, velocity, time,direction : Double)){
//      println("Target is hit")
//    } else {
//      println("Target is not hit")
//    }
//  }
}
