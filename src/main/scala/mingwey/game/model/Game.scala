package mingwey.game.model


import scalafx.beans.property.DoubleProperty

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Game(val player: Character, val computer: Character) {

  val Random = new Random()
  var currentPlayer: Character = player
  var backgroundHeight: Double = 0
  var difficultyLevel: String = "Medium"
  val maxVelocity = 135
  val windValues: Seq[Int] = Seq(-12, -9, -6, -3, 0, 3, 6, 9, 12)
  var playerIntersectionRange: (Int, Int) = (0, 0)

  // Resets game variables
  def resetVariables(): Unit = {
    player.hp = DoubleProperty(player.stats.hp)
    computer.hp = DoubleProperty(computer.stats.hp)
    player.resetSuperpowers()
    currentPlayer = player
  }

  // Sets the character's coordinates
  def setCharCoor(character: Character, xCoordinate: (Double, Double), yCoordinate: (Double, Double)): Unit = {
    character.xCoordinate = xCoordinate
    character.yCoordinate = yCoordinate
  }

  // Sets the bone's coordinates and dimensions
  def setCharBoneCoor(character: Character, xCoordinate: (Double, Double), yCoordinate: (Double, Double), boneWidth: Double, boneHeight: Double): Unit = {
    character.bone.xCoordinate = xCoordinate
    character.bone.yCoordinate = yCoordinate
    character.bone.boneWidth = boneWidth
    character.bone.boneHeight = boneHeight
  }

  // Switches turn between the player and the computer
  def switchTurn(): Unit = {
    currentPlayer = if (currentPlayer == player) computer else player
  }

  // Checks if both the player and computer are still alive
  def isGameOngoing: Boolean = player.isAlive && computer.isAlive

  // Handles the actions during a turn, calculating bone trajectory
  def takeTurn(velocity: Double, wind: Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    val target = if (currentPlayer == player) computer else player
    val direction = if (currentPlayer == player) 1 else -1
    currentPlayer.throwBone(target, velocity, direction, wind)
  }

  // Applies damage to the opponent if the bone intersects
  def applyDamage(): Unit = {
    val target = if (currentPlayer == player) computer else player
    if (currentPlayer.bone.isIntercept) {
      target.takeDamage(currentPlayer.atk)
    }
  }

  // Calculates the range of velocities at which the computer can hit the player
  def checkPlayerIntersectionRange(): Unit = {
    currentPlayer = computer
    val hittingVelocities: ArrayBuffer[Int] = ArrayBuffer[Int]()
    for (velocity <- 0 to maxVelocity) {
      computer.throwBone(player, velocity, -1, 0)
      if (computer.bone.isIntercept) {
        hittingVelocities.append(velocity)
        computer.bone.isIntercept = false
      }
    }
    currentPlayer = player
    playerIntersectionRange = (hittingVelocities.head, hittingVelocities.last)
  }

  def getComputerInput: Double = {
    def getRandomWithinRange(offset: Int): Double = {
      val start = playerIntersectionRange._1 - offset
      val end = Math.min(playerIntersectionRange._2 + offset, maxVelocity)
      start + Random.nextInt((end - start) + 1)
    }

    difficultyLevel match {
      case "Easy"   => getRandomWithinRange(20)
      case "Medium" => getRandomWithinRange(10)
      case "Hard"   => getRandomWithinRange(1)
    }
  }

  def resetSuperpowerState(): Unit = {
    for (i <- 0 to currentPlayer.superpowers.length){
      currentPlayer.deactivateSuperpower(i)
    }
  }
}
