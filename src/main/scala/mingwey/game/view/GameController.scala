package mingwey.game.view

import mingwey.game.model.Character
import mingwey.game.MainApp._
import scalafx.animation.{KeyFrame, Timeline, TranslateTransition}
import scalafx.scene.control.{Button, ProgressBar}
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
import scala.concurrent.duration.{DurationInt, FiniteDuration}

@sfxml
class GameController(
                      private val circle: Circle,
                      private val background: ImageView,
                      private val charImage1: ImageView,
                      private val charImage2: ImageView,
                      private val bone1: ImageView,
                      private val bone2: ImageView,
                      private val playerHpBar: ProgressBar,
                      private val computerHpBar: ProgressBar,
                      private val poisonButton: Button

                    ) {


  val maxVelocity = 120
  var turnInProgress = false
  var userActionPhase = false
  var poisonButtonClicked = false
  var boneInterceptTime : Double = 0

  // Load the character image
  val playerImage = new Image(getClass.getResourceAsStream(player.img.value))
  val computerImage = new Image(getClass.getResourceAsStream(computer.img.value))
  val playerBone = new Image(getClass.getResourceAsStream(player.bone.img.value))
  val computerBone = new Image(getClass.getResourceAsStream(computer.bone.img.value))
  charImage1.setImage(playerImage)
  charImage2.setImage(computerImage)

  def initialize(): Unit = {
    handleCoordinates()
    handleTurns()
    bindProgressBar()
    bindPoisonButton()
  }

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

  def resetBonePosition(): Unit = {
      bone1.layoutX = player.bone.xCoordinate(0)
      bone1.layoutY = player.bone.yCoordinate(0)

      bone2.layoutX = computer.bone.xCoordinate(0)
      bone2.layoutY = computer.bone.yCoordinate(0)
  }

  def moveImageView(imageView: ImageView, amountX: Double, amountY : Double): Unit = {
    imageView.layoutX = (imageView.layoutX + amountX).doubleValue()
    imageView.layoutY = (imageView.layoutY + amountY).doubleValue()
  }

  def createTranslateTransition(imageView: ImageView, x: ArrayBuffer[Double], y: ArrayBuffer[Double]): Unit = {
    val (xCoordinates, yCoordinates) = (x,y)
    var index = 0
    val duration = 1.0
    boneInterceptTime = (xCoordinates.length * duration) / 50
    println(s"Total animation duration: $boneInterceptTime seconds")

    def nextTransition(): Unit = {
      if (index < xCoordinates.length - 1) {

        val xDiff = xCoordinates(index + 1) - xCoordinates(index)
        val yDiff = -(yCoordinates(index + 1) - yCoordinates(index))

        val timeline = new Timeline {
          cycleCount = 1
          keyFrames = Seq(
            KeyFrame(Duration(duration), onFinished = _ => {
              moveImageView(imageView, xDiff, yDiff)
              index += 1
              nextTransition()
            })
          )
        }
        timeline.play()
      }
    }
    println(xCoordinates.length)
    nextTransition()

  }

  def bindProgressBar(): Unit = {
    // Add listener to update progress bar when hp changes
    player.hp.addListener((_, _, newValue) => {
        playerHpBar.setProgress(newValue.doubleValue() / player.stats.hp)

    })

    computer.hp.addListener((_, _, newValue) => {
        computerHpBar.setProgress(newValue.doubleValue() / computer.stats.hp)

    })

  }

  def bindPoisonButton(): Unit = {
    poisonButton.onMouseClicked = e => {
      if (userActionPhase && game.currentPlayer == player) {
        poisonButtonClicked = true
        poisonButton.disable = true
      }
    }
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
//        turnInProgress = false
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
    userActionPhase = true
    bone2.visible = false
    bone1.visible = true
    circle.visible = true
    println("Player shoots!")

    bone1.setImage(playerBone)

    val playerTurnPromise = Promise[Unit]()
    getUserInput().map { normalizedDuration =>
      val velocity = normalizedDuration
      if (poisonButtonClicked) {
        println(game.currentPlayer.atk)
        game.currentPlayer.useSuperpower(0)
      }
      val (x, y) = game.takeTurn(velocity,1)
      if (poisonButtonClicked) {
        poisonButtonClicked = false
        game.currentPlayer.deactivateSuperpower(0)
      }
      userActionPhase = false
      createTranslateTransition(bone1, x, y)

      playerTurnPromise.success(())
    }
    playerTurnPromise.future
  }

  def handleComputerTurn(): Future[Unit] = {
    turnInProgress = true
    println("Computer shoots!")
    bone2.visible = true
    bone1.visible = false
    circle.visible = false
    bone2.setImage(computerBone)
    println(computer.hp)
    val velocity = getComputerInput()
    val (x, y) = game.takeTurn(velocity, -1)
    createTranslateTransition(bone2, x, y)
//    changeProgressBar(computer)
    Future.successful(())

  }

  def waitFor(duration: FiniteDuration): Future[Unit] = {
    val promise = Promise[Unit]()
    Future {
      Thread.sleep(duration.toMillis)
      promise.success(())
    }
    promise.future
  }

  def handleTurns(): Unit = {
    if (game.checkGameState()) {
      if (game.currentPlayer == player) {
        handlePlayerTurn().onComplete {
          case Success(_) =>
            waitFor((boneInterceptTime).toInt.second).onComplete {
              case Success(_) =>
                game.applyDamage()
                waitFor(1.second).onComplete{
                  case Success(_)=>
                    game.switchTurn()
                    turnInProgress = false
                    handleTurns()
                }
            }
        }
      }
      else {
        handleComputerTurn().onComplete {
          case Success(_) =>

            waitFor((boneInterceptTime + 1).toInt.second).onComplete {
              case Success(_) =>
                game.switchTurn()
                turnInProgress = false
                resetBonePosition()
                handleTurns()
            }

        }
      }
    }
    else {
      println("Game over!")
    }
  }


  initialize()

}
