
package mingwey.game.view

import mingwey.game.model.Character
import mingwey.game.MainApp._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.AnchorPane
import scalafx.scene.shape.Circle
import scalafx.scene.transform.Translate
import scalafxml.core.macros.sfxml

import java.time.Instant


@sfxml
class GameController( private val circle: Circle,
                      private val charImage1 : ImageView,
                      private val charImage2 : ImageView,
                      private val bone1 : ImageView,
                      private val bone2 : ImageView,

                    ){

  def mousePressedDuration(): Unit = {
    var pressTime = 0f
    var releaseTime = 0f
    circle.onMousePressed = e => {
      pressTime = System.nanoTime()
      println(pressTime)
    }

    circle.onMouseReleased = e => {
      releaseTime = System.nanoTime()
      val duration = (releaseTime - pressTime) / 1000000000
      println(releaseTime)
      println
      println(duration)
      println
    }
  }

  // Load the character image
  val characterImage1 = new Image(getClass.getResourceAsStream(character1.img.value))
  val characterImage2 = new Image(getClass.getResourceAsStream(character2.img.value))
  val character1Bone = new Image(getClass.getResourceAsStream(character1.bone.img.value))
  val character2Bone = new Image(getClass.getResourceAsStream(character2.bone.img.value))
  charImage1.setImage(characterImage1)
  charImage2.setImage(characterImage2)

  private def handlePlayerShot(): Unit = {
    if (game.playerTurn) {
      println("Player shoots!")
      bone1.setImage(character1Bone)
      mousePressedDuration()




      // Switch to computer's turn
      game.playerTurn = false

      // Handle computer's turn after a delay
      new java.util.Timer().schedule(new java.util.TimerTask {
        def run(): Unit = {
          javafx.application.Platform.runLater(() => handleComputerShot())
        }
      }, 1000)
    }
  }

  private def handleComputerShot(): Unit = {
    // Perform computer's shooting action
    println("Computer shoots!")
    bone2.setImage(character2Bone)

    // Switch back to player's turn
    game.playerTurn = true
  }

  handlePlayerShot()
}


