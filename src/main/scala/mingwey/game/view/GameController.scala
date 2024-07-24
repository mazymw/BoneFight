
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

  def handleCoordinates(): Unit = {
    val playerXCoor = (charImage1.layoutX.value, charImage1.layoutX.value + charImage1.getFitWidth)
    val playerYCoor = (charImage1.layoutY.value - charImage1.getFitHeight, charImage1.layoutY.value)
    game.setPlayerCoor(playerXCoor,playerYCoor)

    val ComputerXCoor = (charImage2.layoutX.value, charImage2.layoutX.value + charImage2.getFitWidth)
    val ComputerYCoor = (charImage2.layoutY.value - charImage2.getFitHeight, charImage2.layoutY.value)
    game.setComputerCoor(ComputerXCoor, ComputerYCoor)


    game.player.bone.xCoordinate(0) = bone1.layoutX.value
    game.player.bone.xCoordinate(1) = bone1.layoutX.value + bone1.getFitWidth
    game.player.bone.yCoordinate(0) = bone1.layoutY.value - bone1.getFitHeight
    game.player.bone.yCoordinate(1) = bone1.layoutY.value
    game.player.bone.boneWidth = bone1.getFitWidth
    game.player.bone.boneHeight = bone1.getFitHeight

    game.computer.bone.xCoordinate(0) = bone2.layoutX.value
    game.computer.bone.xCoordinate(1) = bone2.layoutX.value + bone2.getFitWidth
    game.computer.bone.yCoordinate(0) = bone2.layoutY.value - bone2.getFitHeight
    game.computer.bone.yCoordinate(1) = bone2.layoutY.value
    game.computer.bone.boneWidth = bone2.getFitWidth
    game.computer.bone.boneHeight = bone2.getFitHeight
    game.checkHit(player, computer, 90)
  }



  def handlePlayerShot(): Unit = {
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

  def handleComputerShot(): Unit = {
    // Perform computer's shooting action
    println("Computer shoots!")
    bone2.setImage(computerBone)

    // Switch back to player's turn
    game.playerTurn = true
  }

  handlePlayerShot()
  handleCoordinates()

}


