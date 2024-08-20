package mingwey.game.model

import mingwey.game.MainApp.{game, player}
import scalafx.beans.property.ObjectProperty

class Bone {
  // Image path for the bone object
  var img: ObjectProperty[String] = ObjectProperty("/image/bone.png")

  // Coordinates for the bone
  var xCoordinate: (Double, Double) = (0, 0)
  var yCoordinate: (Double, Double) = (0, 0)

  // Width and height of the bone object
  var boneWidth: Double = 0
  var boneHeight: Double = 0

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
    val verticalVelocity = velocity * Math.sin(Math.toRadians(angle))

    // Time to reach maximum height (vertical velocity reaches 0)
    val timeMaxHeight = verticalVelocity / gravitationalForce

    // Calculate the maximum height the bone reaches
    // 50 is to shift up the baseline of the floor to be at the same level as the character
    val hMax = (game.backgroundHeight - 50) - this.yCoordinate._1 + (Math.pow(verticalVelocity, 2) / (2 * gravitationalForce))

    // Time to fall from maximum height back to the ground
    val timeFall = Math.sqrt(2 * hMax / gravitationalForce)

    // Update flightTime and return the total flight time
    flightTime = timeMaxHeight + timeFall
    flightTime
  }

  // Simulate the bone's arc trajectory based on the given parameters
  def simulateArc(velocity: Double, time: Double, direction: Double, wind: Double): ((Double, Double), (Double, Double)) = {
    var horizontalVelocity = velocity * Math.cos(Math.toRadians(angle)) * direction
    val verticalVelocity = velocity * Math.sin(Math.toRadians(angle))

    // Adjust horizontal velocity if it's the player's turn and there's wind
    if (game.currentPlayer == player) {
      horizontalVelocity += wind
      if (horizontalVelocity < 0) {
        horizontalVelocity = 0
      }
    }

    // Update X and Y coordinates based on time, velocities, and gravitational force
    val simulatedXCoordinate = updateXCoordinate(horizontalVelocity, time)
    val simulatedYCoordinate = updateYCoordinate(verticalVelocity, time)

    // Return the updated coordinates
    (simulatedXCoordinate, simulatedYCoordinate)
  }

  // Update X coordinate based on horizontal velocity and time
  private def updateXCoordinate(horizontalVelocity: Double, time: Double): (Double, Double) = {
    val newXStart = xCoordinate._1 + horizontalVelocity * time
    (newXStart, newXStart + boneWidth)
  }

  // Update Y coordinate based on vertical velocity, time, and gravitational force
  private def updateYCoordinate(verticalVelocity: Double, time: Double): (Double, Double) = {
    val newYStart = yCoordinate._1 - verticalVelocity * time + gravitationalForce * time * time / 2
    (newYStart, newYStart + boneHeight)
  }

  // Check if the bone intersects with the target based on simulated trajectory
  def checkIntersects(target: Character, simulatedXCoordinate: (Double, Double), simulatedYCoordinate: (Double, Double)): Boolean = {
    overlap(simulatedXCoordinate, target.xCoordinate) && overlap(simulatedYCoordinate, target.yCoordinate)
  }

  // Helper method to check for overlap between two intervals
  private def overlap(coor1: (Double, Double), coor2: (Double, Double)): Boolean = {
    coor1._1 < coor2._2 && coor1._2 > coor2._1
  }
}
