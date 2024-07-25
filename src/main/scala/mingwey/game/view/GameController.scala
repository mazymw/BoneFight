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


//  def test(): Double = {
//    var pressStartTime: Double = 0
//    var pressEndTime: Double = 0
//    var isPressed: Boolean = false
//
//    circle.onMousePressed = event => {
//      pressStartTime = System.currentTimeMillis()
//      isPressed = true
//    }
//    circle.onMouseReleased = event => {
//      pressEndTime = System.currentTimeMillis()
//      isPressed = false
//    }
//
//    def getPressDuration: Double = {
//      if (isPressed) {
//        System.currentTimeMillis() - pressStartTime
//      } else {
//        pressEndTime - pressStartTime
//      }
//    }
//    getPressDuration
//  }


//  def mousePressedDuration(): Unit = {
//    var pressTime = 0f
//    var releaseTime = 0f
//    var duration = 0f
//    val upperBound = 10// change if needed
//    val MaxVelocity = 120
//
//    circle.onMousePressed = e => {
//      pressTime = System.nanoTime()
//    }
//
//    circle.onMouseReleased = e => {
//      releaseTime = System.nanoTime()
//      duration = (releaseTime - pressTime) / 1000000000
//      if (duration > upperBound) {
//        duration = upperBound
//      }
//      println(duration)
//      duration / upperBound * 120
//    }
//  }

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


  def moveImageView(imageView: ImageView, amountX: Double, amountY : Double): Unit = {
    imageView.layoutX = (imageView.layoutX + amountX).doubleValue()
    imageView.layoutY = (imageView.layoutY + amountY).doubleValue()
  }

  def createTranslateTransition(x: ArrayBuffer[Double], y: ArrayBuffer[Double]): Unit = {
    val (xCoordinates, yCoordinates) = (x,y)
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

  def getUserInput(callback: Double => Unit): Unit = {
    // Variables to track mouse press duration
    var pressTime: Long = 0
    var releaseTime: Long = 0

    // Mouse event handlers
    circle.onMousePressed = e => {
      pressTime = System.nanoTime()
    }

    circle.onMouseReleased = e => {
      releaseTime = System.nanoTime()

      // Calculate the duration in seconds
      val duration = (releaseTime - pressTime).toDouble / 1e9
      val upperBound = 2 // Upper bound for normalization
      val maxVelocity = 120

      // Normalize duration
      val normalizedDuration = Math.min(duration, upperBound) / upperBound * maxVelocity
      callback(normalizedDuration)
    }
  }

  def getComputerInput(): Double = {


  }

  def handleTurn(): Unit = {
    if (game.currentPlayer == game.player) {
      // Use getUserInput with a callback
      getUserInput { normalizedDuration =>
        val velocity = normalizedDuration
        game.takeTurn(velocity)
      }
    } else {
      val velocity = getComputerInput()
      game.takeTurn(velocity)

    }
  }


  def turn(): Unit = {
    if (game.currentPlayer == player) {
      println("Player shoots!")
      bone1.setImage(playerBone)



        val (x, y) = game.player.throwBone(computer, normalizedDuration)
        // Create the translate transition
        createTranslateTransition(x, y)


    }
  }

  turn()

//
//  val catHpBar = new ProgressBar {
//    progress <== Bindings.createDoubleBinding(
//      () => game.cat.hp.value / 100.0,
//      game.cat.hp
//    )
//  }
//
//  val dogHpBar = new ProgressBar {
//    progress <== Bindings.createDoubleBinding(
//      () => game.dog.hp.value / 100.0,
//      game.dog.hp
//    )
//  }
//


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


}
