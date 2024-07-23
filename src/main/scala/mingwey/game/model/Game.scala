package mingwey.game.model

import scalafx.beans.property.{StringProperty, ObjectProperty}
import java.time.LocalDate

class Game(val player : Character, computer: Character) {
  var playerTurn: Boolean = true
  var gameState : Boolean = true

  def setPlayerCoor(xCoordinate : (Double, Double), yCoordinate : (Double, Double)): Unit = {
    player.x_coordinate = xCoordinate
    player.y_coordinate = yCoordinate
    println(player.x_coordinate)
    println(player.y_coordinate)
  }

  def setComputerCoor(xCoordinate : (Double, Double), yCoordinate : (Double, Double)): Unit = {
    computer.x_coordinate = xCoordinate
    computer.y_coordinate = yCoordinate
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

  def checkHit(shooter: Character, target: Character, velocity: Double) = {
    val time = 0
    val flightTime = shooter.bone.getFlightTime(velocity)

    while (time < flightTime){
      var coodinate = shooter.bone.arc(velocity, time)
//      if (coodinate._2  <= target. )
    }
  }










}
