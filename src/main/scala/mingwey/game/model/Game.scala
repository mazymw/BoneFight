package mingwey.game.model

import scalafx.beans.property.DoubleProperty
import scala.collection.mutable.ArrayBuffer

class Game(val player : Character, val computer: Character) {
  // The current player whose turn it is
  var currentPlayer: Character = player

  // Height of the game background
  var backgroundHeight: Double = 0

  // Difficulty level of the game
  var difficultyLevel: String = "Medium"

  // Maximum velocity a character can throw the bone
  val maxVelocity = 135

  // Possible wind values affecting bone trajectory
  val windValues: Seq[Int] = Seq(-12, -9, -6, -3, 0, 3, 6, 9, 12)

  // Range of velocities that intersect with the player
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
  def setCharBoneCoor(character: Character, xCoordinate: ArrayBuffer[Double], yCoordinate: ArrayBuffer[Double], boneWidth: Double, boneHeight: Double): Unit = {
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
  def checkGameState(): Boolean = {
    if (player.isAlive && computer.isAlive) {
      true
    } else {
      false
    }
  }

  // Handles the actions during a turn, calculating bone trajectory
  def takeTurn(velocity: Double, wind: Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    val target = if (currentPlayer == player) computer else player
    val direction = if(currentPlayer == player) 1 else -1
    val (x, y) = currentPlayer.throwBone(target, velocity, direction: Double, wind)
    (x, y)
  }

  // Applies damage to the opponent if the bone intersects
  def applyDamage(): Unit = {
    val target = if (currentPlayer == player) computer else player
    if (currentPlayer.bone.isIntercept) {
      target.takeDamage(currentPlayer.atk)
    }
  }

  // Calculates the range of velocities at which the computer can hit the player
  def checkPlayerIntersectionRange(computer: Character, player: Character): Unit = {
    currentPlayer = computer
    val hittingVelocities: ArrayBuffer[Int] = ArrayBuffer[Int]()
    for (velocity <- 0 to maxVelocity) {
      val _ = computer.throwBone(player, velocity, -1, 0)

      if (computer.bone.isIntercept) {
        hittingVelocities.append(velocity)
        computer.bone.isIntercept = false
      }
    }
    currentPlayer = player
    playerIntersectionRange = (hittingVelocities.head, hittingVelocities.last)
  }
}
