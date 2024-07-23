
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
  val playerImage = new Image(getClass.getResourceAsStream(player.img.value))
  val computerImage = new Image(getClass.getResourceAsStream(computer.img.value))
  val playerBone = new Image(getClass.getResourceAsStream(player.bone.img.value))
  val computerBone = new Image(getClass.getResourceAsStream(computer.bone.img.value))
  charImage1.setImage(playerImage)
  charImage2.setImage(computerImage)



  private def handlePlayerShot(): Unit = {
    if (game.playerTurn) {
      println("Player shoots!")
      bone1.setImage(playerBone)
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
    bone2.setImage(computerBone)

    // Switch back to player's turn
    game.playerTurn = true
  }

  handlePlayerShot()
}


