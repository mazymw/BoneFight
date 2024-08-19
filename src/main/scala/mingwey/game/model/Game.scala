package mingwey.game.model

import scalafx.beans.property.DoubleProperty
import scala.collection.mutable.ArrayBuffer

class Game(val player : Character, val computer: Character) {
  var currentPlayer: Character = player
  var backgroundHeight: Double = 0
  var difficultyLevel: String = "Medium"
  val maxVelocity = 135
  val windValues: Seq[Int] = Seq(-12, - 9, -6, -3, 0, 3, 6, 9, 12)
  var playerIntersectionRange: (Int, Int) = (0,0)


  def resetVariables(): Unit = {
    player.hp = DoubleProperty(player.stats.hp)
    computer.hp = DoubleProperty(computer.stats.hp)
    player.resetSuperpowers()
    currentPlayer = player
  }

  def setCharCoor(character: Character,xCoordinate : (Double, Double), yCoordinate : (Double, Double)): Unit = {
    character.xCoordinate = xCoordinate
    character.yCoordinate = yCoordinate
  }

  def setCharBoneCoor(character: Character, xCoordinate : ArrayBuffer[Double], yCoordinate : ArrayBuffer[Double], boneWidth: Double, boneHeight: Double): Unit = {
    character.bone.xCoordinate = xCoordinate
    character.bone.yCoordinate = yCoordinate
    character.bone.boneWidth = boneWidth
    character.bone.boneHeight = boneHeight
  }

  def switchTurn(): Unit = {
    currentPlayer = if (currentPlayer == player) computer else player
  }


  def checkGameState() : Boolean = {
    if(player.isAlive && computer.isAlive){
      true
    }
    else{
      false
    }
  }


  def takeTurn(velocity: Double,direction : Double, wind: Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    val target = if (currentPlayer == player) computer else player
    val (x, y) = currentPlayer.throwBone(target, velocity, direction : Double, wind)
    (x,y)
  }

  def applyDamage(): Unit = {
    val target = if (currentPlayer == player) computer else player
    if (currentPlayer.bone.isIntercept) {
      target.takeDamage(currentPlayer.atk)
    }
  }

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




  //  def takeTurn(velocity: Double,direction : Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
  //    val target = if (currentPlayer == player) computer else player
  //    val (x, y) =currentPlayer.throwBone(target, velocity, direction : Double)
  //    if (currentPlayer.bone.isIntercept) {
  //      target.takeDamage(currentPlayer.atk)
  //      currentPlayer.bone.isIntercept = false
  //    }
  //    (x,y)
  //  }



//  def animateMovement(shooter: Character, target: Character, velocity: Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
//    var time: Double = 0
//    val flightTime = shooter.bone.getFlightTime(velocity)
//    val xCoordinates: ArrayBuffer[Double] = ArrayBuffer()
//    val yCoordinates: ArrayBuffer[Double] = ArrayBuffer()
//
//    breakable {
//      while (time < flightTime) {
//        val (simulatedXCoordinate, simulatedYCoordinate) = shooter.bone.simulateArc(velocity, time)
//
//        time += 0.1
//
//        if(shooter.bone.checkIntersects(target, velocity, time)){
//          break()
//        }
//
//        xCoordinates.append(simulatedXCoordinate(0))
//        yCoordinates.append(simulatedYCoordinate(0))
//      }
//    }
//    (xCoordinates, yCoordinates)
//  }









}
