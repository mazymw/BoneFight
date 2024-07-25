package mingwey.game.model

import scalafx.beans.property.{StringProperty, ObjectProperty}
import java.time.LocalDate

class Game(val player : Character, val computer: Character) {
  var playerTurn: Boolean = true
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
    if (player.stats.hp > 0 && computer.stats.hp > 0 ){
      gameState
    }
    else{
      gameState = false
      gameState
    }
  }

//  def checkHit(shooter: Character, target: Character, velocity: Double) = {
//    var time:Double = 0
//    val flightTime = shooter.bone.getFlightTime(velocity)
//
//    while (time < flightTime ){
//      var (simulatedXCoordinate, simulatedYCoordinate) =shooter.bone.simulateArc(velocity, time)
//      shooter.bone.checkIntersectsAndPrint(target,velocity, time)
//      println()
//      time = time + 0.5
//    }
//  }










}
