package mingwey.game.model

import scalafx.beans.property.{ObjectProperty, StringProperty}

import java.time.LocalDate
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

class Game(val player : Character, val computer: Character) {
  var currentPlayer: Character = player
  var gameState : Boolean = true
  var backgroundHeight: Double = 0


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
    println(s"Turn switched. Current player: ${currentPlayer.nameS}")
  }


  def checkGameState() : Boolean = {
    if(player.isAlive && computer.isAlive){
      true
    }
    else{
      false
    }
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


  def takeTurn(velocity: Double,direction : Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    val target = if (currentPlayer == player) computer else player
    val (x, y) =currentPlayer.throwBone(target, velocity, direction : Double)
    (x,y)
  }

  def applyDamage(): Unit = {
    val target = if (currentPlayer == player) computer else player
    if (currentPlayer.bone.isIntercept) {
      println(currentPlayer.atk)
      target.takeDamage(currentPlayer.atk)
      currentPlayer.bone.isIntercept = false
    }
  }





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
