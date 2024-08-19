package mingwey.game.model

import mingwey.game.MainApp.{game, player}
import scalafx.beans.property.ObjectProperty
import scala.collection.mutable.ArrayBuffer

class Bone {
  // Image path for the bone object
  var img: ObjectProperty[String] = ObjectProperty("/image/bone.png")

  // Coordinates for the bone
  var xCoordinate: ArrayBuffer[Double] = ArrayBuffer(0,0)
  var yCoordinate: ArrayBuffer[Double] = ArrayBuffer(0,0)

  // Width and height of the bone object
  var boneWidth: Double = 0
  var boneHeight: Double  = 0

  // Launch angle of the bone (in degrees)
  val angle = 75

  // Gravitational force acting on the bone (affects vertical motion)
  var gravitationalForce = 10

  // Flag to determine if the bone has intersected with a target
  var isIntercept: Boolean = false

  // Total flight time of the bone
  var flightTime: Double = 0

  // Calculate and return the total flight time based on initial velocity
  def getFlightTime(velocity: Double): Double = {
    val vertical_velocity = velocity * Math.sin(Math.toRadians(angle))

    // Time to reach maximum height (vertical velocity reaches 0)
    val timeMaxHeight =  vertical_velocity / gravitationalForce

    // Calculate the maximum height the bone reaches
    val hMax = (game.backgroundHeight - 50) - this.yCoordinate(0) + (Math.pow(vertical_velocity, 2) / (2 * gravitationalForce))

    // Time to fall from maximum height back to the ground
    val timeFall = Math.sqrt(2 * hMax / gravitationalForce)

    // Update flightTime and return the total flight time
    flightTime =  timeMaxHeight  + timeFall
    timeMaxHeight  + timeFall
  }

  // Simulate the bone's arc trajectory based on the given parameters
  def simulateArc(velocity : Double, time: Double , direction : Double, wind : Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    var horizontal_velocity = velocity * Math.cos(Math.toRadians(angle)) * direction
    val vertical_velocity = velocity * Math.sin(Math.toRadians(angle))

    // Adjust horizontal velocity if it's the player's turn and there's wind
    if (game.currentPlayer == player){
      horizontal_velocity = horizontal_velocity + wind
      if(horizontal_velocity < 0){
        horizontal_velocity = 0
      }
    }

    // Create copies of the bone's current coordinates
    val simulatedXCoordinate = ArrayBuffer[Double](xCoordinate(0), xCoordinate(1))
    val simulatedYCoordinate = ArrayBuffer[Double](yCoordinate(0), yCoordinate(1))

    // Update X and Y coordinates based on time, velocities, and gravitational force
    simulatedXCoordinate(0) = simulatedXCoordinate(0) + horizontal_velocity * time
    simulatedXCoordinate(1) = simulatedXCoordinate(0) + boneWidth
    simulatedYCoordinate(0) = simulatedYCoordinate(0) - vertical_velocity * time  + gravitationalForce * time * time / 2
    simulatedYCoordinate(1) = simulatedYCoordinate(0) + boneHeight

    // Return the updated coordinates
    (simulatedXCoordinate, simulatedYCoordinate)
  }

  // Check if the bone intersects with the target based on simulated trajectory
  def checkIntersects(target: Character, simulatedXCoordinate: ArrayBuffer[Double], simulatedYCoordinate: ArrayBuffer[Double]): Boolean = {

    // Check for overlap in the X and Y coordinates between the bone and the target
    val overlapX = simulatedXCoordinate(0) < target.xCoordinate._2 && simulatedXCoordinate(1) > target.xCoordinate._1
    val overlapY = simulatedYCoordinate(0) < target.yCoordinate._2 && simulatedYCoordinate(1) > target.yCoordinate._1

    // Return true if both X and Y coordinates overlap (indicating an intersection)
    overlapX && overlapY
  }

}
