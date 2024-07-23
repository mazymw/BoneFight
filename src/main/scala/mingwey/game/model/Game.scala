package mingwey.game.model

import scalafx.beans.property.{StringProperty, ObjectProperty}
import java.time.LocalDate

class Game(val player : Character, computer: Character) {
  var playerTurn: Boolean = true
  var gameState : Boolean = true

  def setPlayerCoor(xCoordinate : Int, yCoordinate : Int): Unit = {
    player.x_coordinate = xCoordinate
    player.y_coordinate = yCoordinate
  }

  def setComputerCoor(xCoordinate : Int, yCoordinate : Int): Unit = {
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
      if (coodinate._2  <= target. )
    }
  }










}
