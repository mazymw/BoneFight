package mingwey.game.view

import mingwey.game.model.Character
import mingwey.game.MainApp._
import scalafx.animation.{KeyFrame, Timeline, TranslateTransition}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.AnchorPane
import scalafx.scene.shape.Circle
import scalafx.util.Duration
import scalafxml.core.macros.sfxml

import scala.util.control.Breaks._
import java.time.Instant
import scala.collection.mutable.ArrayBuffer

@sfxml
class GameController(private val circle: Circle,
                     private val background: ImageView,
                     private val charImage1: ImageView,
                     private val charImage2: ImageView,
                     private val bone1: ImageView,
                     private val bone2: ImageView) {

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
      println(duration)
      println()
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
    game.setPlayerCoor(playerXCoor, playerYCoor)

    val computerXCoor = (charImage2.layoutX.value, charImage2.layoutX.value + charImage2.getFitWidth)
    val computerYCoor = (charImage2.layoutY.value - charImage2.getFitHeight, charImage2.layoutY.value)
    game.setComputerCoor(computerXCoor, computerYCoor)

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
    //    game.checkHit(player, computer, 90)
  }


  def animateMovement(shooter: Character, target: Character, velocity: Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    var time: Double = 0
    val flightTime = shooter.bone.getFlightTime(velocity)
    val xCoordinates: ArrayBuffer[Double] = ArrayBuffer()
    val yCoordinates: ArrayBuffer[Double] = ArrayBuffer()

    breakable {
      while (time < flightTime) {
        val (simulatedXCoordinate, simulatedYCoordinate) = shooter.bone.simulateArc(velocity, time)

        time += 0.1

        if(shooter.bone.checkIntersects(target, velocity, time)){
          break()
        }

        if (simulatedXCoordinate(1) > (background.layoutX.value + background.fitWidth.value) ||
          simulatedYCoordinate(1) < background.layoutY.value)
        {
          break()
        }
        xCoordinates.append(simulatedXCoordinate(0))
        yCoordinates.append(simulatedYCoordinate(0))
      }
      println(xCoordinates)
      println(yCoordinates)
    }
    (xCoordinates, yCoordinates)
  }

  def moveImageView(imageView: ImageView, amountX: Double, amountY : Double): Unit = {
    imageView.layoutX = (imageView.layoutX + amountX).doubleValue()
    imageView.layoutY = (imageView.layoutY + amountY).doubleValue()
  }

  def createTranslateTransition(): Unit = {
    val (xCoordinates, yCoordinates) = animateMovement(player, computer, 100)
    var index = 0

    def nextTransition(): Unit = {
      if (index < xCoordinates.length - 1) {
        val xDiff = xCoordinates(index + 1) - xCoordinates(index)
        val yDiff = -(yCoordinates(index + 1) - yCoordinates(index))

        val timeline = new Timeline {
          cycleCount = 1
          keyFrames = Seq(
            KeyFrame(Duration(5), onFinished = _ => {
              moveImageView(bone1, xDiff, yDiff)
              index += 1
              nextTransition()
            })
          )
        }
        timeline.play()
      }
    }
    nextTransition()
  }




//  def handlePlayerShot(): Unit = {
//    if (game.playerTurn) {
//      println("Player shoots!")
//      bone1.setImage(playerBone)
//      mousePressedDuration()
//
//      // Switch to computer's turn
//      game.playerTurn = false
//
//      // Handle computer's turn after a delay
//      new java.util.Timer().schedule(new java.util.TimerTask {
//        def run(): Unit = {
//          javafx.application.Platform.runLater(() => handleComputerShot())
//        }
//      }, 1000)
//    }
//  }
//
//  def handleComputerShot(): Unit = {
//    // Perform computer's shooting action
//    println("Computer shoots!")
////    bone2.setImage(computerBone)
//
//    // Switch back to player's turn
//    game.playerTurn = true
//  }
//
//  handlePlayerShot()
  bone1.setImage(playerBone)
  handleCoordinates()

  createTranslateTransition()
}
