package mingwey.game.model

import scalafx.beans.property.{ObjectProperty, StringProperty}

import java.time.LocalDate
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

class Game(val player : Character, val computer: Character) {
  var currentPlayer: Character = player
  var gameState : Boolean = true

  def setPlayerCoor(xCoordinate : (Double, Double), yCoordinate : (Double, Double)): Unit = {
    player.xCoordinate = xCoordinate
    player.yCoordinate = yCoordinate
    println(player.xCoordinate)
    println(player.yCoordinate)
  }

  def setComputerCoor(xCoordinate : (Double, Double), yCoordinate : (Double, Double)): Unit = {
    computer.xCoordinate = xCoordinate
    computer.yCoordinate = yCoordinate
    println(computer.xCoordinate)
    println(computer.yCoordinate)
  }


  def checkGameState() : Boolean = {
    if(player.isAlive && computer.isAlive){
      true
    }
    else{
      false
    }
  }


  def takeTurn(velocity: Double): Unit = {
    val target = if (currentPlayer == player) computer else player
    currentPlayer.throwBone(target, velocity)
    if (currentPlayer.bone.isIntercept) {
      target.takeDamage(1)
    }

    currentPlayer = target
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
