package mingwey.game.model

import scalafx.beans.property.{StringProperty, ObjectProperty}
import java.time.LocalDate

class Game(val character1 : Character, character2: Character) {
  var playerTurn: Boolean = true
  var gameState : Boolean = true
  character1.bone

  def checkGameState = {
    if (character1.stats.hp > 0 && character1.stats.hp > 0 ){
      gameState
    }
    else{
      gameState = false

    }

  }










}
