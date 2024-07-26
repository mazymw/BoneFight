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
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext._

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
  var turnInProgress = false

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

    game.backgroundHeight = background.layoutY.value + background.fitHeight.value
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

  def getUserInput(): Future[Double] = {
    // Variables to track mouse press duration
    val userInputPromise = Promise[Double]()
    var pressTime: Long = 0
    var releaseTime: Long = 0

    // Mouse event handlers
    circle.onMousePressed = e => {
      if (turnInProgress  && game.currentPlayer == player){
        println("Circle pressed")
        pressTime = System.nanoTime()
      }

    }

    circle.onMouseReleased = e => {
      if (turnInProgress){
        println("Circle Released")
        releaseTime = System.nanoTime()

        // Calculate the duration in seconds
        val duration = (releaseTime - pressTime).toDouble / 1e9
        val upperBound = 2 // Upper bound for normalization

        // Normalize duration
        val normalizedDuration = Math.min(duration, upperBound) / upperBound * maxVelocity
        turnInProgress = false
        userInputPromise.success(normalizedDuration)
      }
    }
    userInputPromise.future
  }

  def getComputerInput(): Double = {
    val random = new Random()
    val start = 70
    val end = maxVelocity
    val randomNumber = start + random.nextInt( (end - start) + 1 )
    randomNumber
  }

  def handlePlayerTurn(): Future[Unit] = {
    turnInProgress = true
    println("Player shoots!")
    bone1.setImage(playerBone)

    val playerTurnPromise = Promise[Unit]()
    getUserInput().map { normalizedDuration =>
      val velocity = normalizedDuration
      val (x, y) = game.takeTurn(velocity)
      createTranslateTransition(x, y)
      playerTurnPromise.success(())
    }
    playerTurnPromise.future
  }

  def handleComputerTurn(): Future[Unit] = {
    println("Computer shoots!")
    bone2.setImage(computerBone)
    turnInProgress = true
    val velocity = getComputerInput()
    val (x, y) = game.takeTurn(velocity)
    createTranslateTransition(x, y)
    Future.successful(())

  }

  def handleTurns(): Unit = {
    if (game.checkGameState()) {
      if (game.currentPlayer == game.player) {

        handlePlayerTurn().onComplete {
          case Success(_) =>
            game.switchTurn()
            handleTurns()
            turnInProgress = false
        }
      } else {
        handleComputerTurn().onComplete {
          case Success(_) =>
            game.switchTurn()
            handleTurns()
            turnInProgress = false
        }
      }
    } else {
      println("Game over!")
    }
  }
  handleCoordinates()
  handleTurns()



}
