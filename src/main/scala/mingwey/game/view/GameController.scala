package mingwey.game.view

import mingwey.game.model.Character
import mingwey.game.MainApp._
import scalafx.animation.{KeyFrame, Timeline, TranslateTransition}
import scalafx.scene.control.ProgressBar
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.AnchorPane
import scalafx.scene.shape.Circle
import scalafx.util.Duration
import scalafxml.core.macros.sfxml

import scala.util.control.Breaks._
import java.time.Instant
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

@sfxml
class GameController(
                      private val circle: Circle,
                      private val background: ImageView,
                      private val charImage1: ImageView,
                      private val charImage2: ImageView,
                      private val bone1: ImageView,
                      private val bone2: ImageView,
                      private val playerHpBar: ProgressBar,
                    ) {


  val maxVelocity = 110

  // Load the character image
  val playerImage = new Image(getClass.getResourceAsStream(player.img.value))
  val computerImage = new Image(getClass.getResourceAsStream(computer.img.value))
  val playerBone = new Image(getClass.getResourceAsStream(player.bone.img.value))
  val computerBone = new Image(getClass.getResourceAsStream(computer.bone.img.value))
  charImage1.setImage(playerImage)
  charImage2.setImage(computerImage)

  private def getCharCoordinates(imageView: ImageView): ((Double, Double), (Double, Double)) = {
    val xCoor = (imageView.layoutX.value, imageView.layoutX.value + imageView.getFitWidth)
    val yCoor = (imageView.layoutY.value - imageView.getFitHeight, imageView.layoutY.value)
    (xCoor, yCoor)
  }

  private def getBoneCoordinates(bone: ImageView): (ArrayBuffer[Double], ArrayBuffer[Double], Double, Double) = {
    val xCoor = ArrayBuffer(bone.layoutX.value, bone.layoutX.value + bone.getFitWidth)
    val yCoor = ArrayBuffer(bone.layoutY.value, bone.layoutY.value + bone.getFitHeight)
    val width = bone.getFitWidth
    val height = bone.getFitHeight
    (xCoor, yCoor, width, height)
  }

  def handleCoordinates(): Unit = {
    val (playerXCoor, playerYCoor) = getCharCoordinates(charImage1)
    game.setCharCoor(player, playerXCoor, playerYCoor)

    val (computerXCoor, computerYCoor) = getCharCoordinates(charImage2)
    game.setCharCoor(computer, computerXCoor, computerYCoor)

    val (playerBoneXCoor, playerBoneYCoor, playerBoneWidth, playerBoneHeight) = getBoneCoordinates(bone1)
    game.setCharBoneCoor(player, playerBoneXCoor, playerBoneYCoor, playerBoneWidth, playerBoneHeight)

    val (computerBoneXCoor, computerBoneYCoor, computerBoneWidth, computerBoneHeight) = getBoneCoordinates(bone2)
    game.setCharBoneCoor(computer, computerBoneXCoor, computerBoneYCoor, computerBoneWidth, computerBoneHeight)
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


      // Normalize duration
      val normalizedDuration = Math.min(duration, upperBound) / upperBound * maxVelocity
      callback(normalizedDuration)
    }
  }

  def getComputerInput(): Double = {
    val random = new Random()
    val start = 70
    val end = maxVelocity
    val randomNumber = start + random.nextInt( (end - start) + 1 )
    randomNumber
  }

  def handleTurn(): Unit = {
    if (game.currentPlayer == game.player) {
      println("Player shoots!")
      bone1.setImage(playerBone)
      println(background.layoutY.value + background.fitHeight.value)

      // Use getUserInput with a callback
      getUserInput { normalizedDuration =>
        val velocity = normalizedDuration
        val (x,y) = game.takeTurn(velocity)

        createTranslateTransition(x, y)
      }
    } else {
      println("Computer shoots!")
      bone2.setImage(computerBone)
      val velocity = getComputerInput()
      game.takeTurn(velocity)
      val (x,y) = game.takeTurn(velocity)
      createTranslateTransition(x, y)

    }
  }
  handleTurn()


//  def turn(): Unit = {
//    if (game.currentPlayer == player) {
//      println("Player shoots!")
//      bone1.setImage(playerBone)
//
//
//
//        val (x, y) = game.player.throwBone(computer, normalizedDuration)
//        // Create the translate transition
//        createTranslateTransition(x, y)
//
//
//    }
//  }

//  turn()

//  def bindHPBars(): Unit = {
//    game.player.hp <== playerHpBar
//  }
//
//  val dogHpBar = new ProgressBar {
//    progress <== Bindings.createDoubleBinding(
//      () => game.dog.hp.value / 100.0,
//      game.dog.hp
//    )
//  }



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
