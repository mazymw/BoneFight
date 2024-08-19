package mingwey.game.view

import mingwey.game.MainApp
import mingwey.game.model.Character
import mingwey.game.MainApp._
import scalafx.animation.{KeyFrame, Timeline, TranslateTransition}
import scalafx.application.Platform
import scalafx.beans.property.DoubleProperty
import scalafx.scene.control.{Button, ProgressBar}
import scalafx.scene.effect.{Blend, BlendMode, ColorAdjust, ColorInput}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{AnchorPane, StackPane}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.util.Duration
import scalafxml.core.macros.sfxml

import scala.util.control.Breaks._
import java.time.Instant
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext._
import scala.concurrent.duration.{DurationDouble, DurationInt, FiniteDuration}

@sfxml
class GameController(
                      private val circle: Circle,
                      private val circlePane: StackPane,
                      private val background: ImageView,
                      private val charImage1: ImageView,
                      private val charImage2: ImageView,
                      private val bone1: ImageView,
                      private val bone2: ImageView,
                      private val playerHpBar: ProgressBar,
                      private val computerHpBar: ProgressBar,
                      private val poisonButton: Button,
                      private val healButton: Button,
                      private val aimButton: Button,
                      private val leftWindBar: ProgressBar,
                      private val rightWindBar: ProgressBar,
                      private val leftBubbleText: ImageView,
                      private val rightBubbleText: ImageView,
                      private val pressDurationBar: ProgressBar,
//                      private val testing: Rectangle,

                    ) {

  def initialize(): Unit = {
    handleCoordinates()
    handleTurns()
    bindProgressBar()
    bindPoisonButton()
    bindHealButton()
    bindAimButton()
    resetBonePosition()
    resetSuperpowerState()
  }

  val random = new Random()
  val maxVelocity = 135
  val maxDuration = 2

  var turnInProgress = false
  var aimButtonClicked = false
  var boneInterceptTime : Double = 0


  // Load the character image


  val playerImage = new Image(getClass.getResourceAsStream(player.img.value))
  val computerImage = new Image(getClass.getResourceAsStream(computer.img.value))
  val playerBone = new Image(getClass.getResourceAsStream(player.bone.img.value))
  val computerBone = new Image(getClass.getResourceAsStream(computer.bone.img.value))
  charImage1.setImage(playerImage)
  charImage2.setImage(computerImage)

  charImage1.fitWidth = playerImage.width.value
  charImage1.fitHeight = playerImage.height.value
//  testing.height = playerImage.height.value
//  testing.width =playerImage.width.value
  charImage2.fitWidth = computerImage.width.value
  charImage2.fitHeight = computerImage.height.value
  bone1.fitWidth = playerBone.width.value
  bone1.fitHeight = playerBone.height.value
  bone2.fitWidth = computerBone.width.value
  bone2.fitHeight = computerBone.height.value

//  charImage1.setFitWidth(charImage1.getFitWidth) // Ensure it fits the width




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
    boneInterceptTime = (xCoordinates.length * duration) / 60

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

  def updateWindBar(wind : Double): Unit = {
    if (wind < 0){
      leftWindBar.setProgress(-wind / game.windValues.max)
      rightWindBar.setProgress(0)
    }
    else if(wind == 0) {
      leftWindBar.setProgress(0)
      rightWindBar.setProgress(0)
    }
    else{
      leftWindBar.setProgress(0)
      rightWindBar.setProgress(wind / game.windValues.max)
    }

  }

  def bindPoisonButton(): Unit = {
    poisonButton.onMouseClicked = e => {
      if (turnInProgress && game.currentPlayer == player) {
        game.currentPlayer.useSuperpower(0)
        if (game.currentPlayer.superpowers(0).isActive) {

          poisonButton.disable = true
        }
      }
    }
  }

  def bindHealButton(): Unit = {
    healButton.onMouseClicked = e => {
      if (turnInProgress && game.currentPlayer == player) {
        println("Heal button clicked")
        game.currentPlayer.useSuperpower(1)
        if (game.currentPlayer.superpowers(1).isActive){
          healButton.disable = true
        }

      }
    }
  }

  def bindAimButton(): Unit = {
    aimButton.onMouseClicked = e => {
      if (turnInProgress && game.currentPlayer == player) {
        println("Aim button clicked")
        game.currentPlayer.useSuperpower(2)
        if (game.currentPlayer.superpowers(2).isActive){
          aimButtonClicked = true
          aimButton.disable = true
        }
      }
    }
  }


  def getUserInput(): Future[Double] = {
    // Variables to track mouse press duration
    val userInputPromise = Promise[Double]()
    var pressTime: Long = 0
    var releaseTime: Long = 0

    // Timeline to update the progress bar
    val updateInterval = Duration(100) // Update every 100ms
    val timeline = new Timeline {
      cycleCount = Timeline.Indefinite
      keyFrames = Seq(
        KeyFrame(updateInterval, onFinished = _ => {
          val currentTime = System.nanoTime()
          val duration = (currentTime - pressTime).toDouble / 1e9
          val normalizedDuration = Math.min(duration / maxDuration, 1.0)
          pressDurationBar.progress = normalizedDuration
        })
      )
    }

    // Mouse event handlers
    circle.onMousePressed = e => {
      if (turnInProgress  && game.currentPlayer == player){
        println("Circle pressed")
        pressTime = System.nanoTime()
        pressDurationBar.progress = 0
        pressDurationBar.visible = true
        timeline.play()

      }
    }
    circle.onMouseReleased = e => {
      if (turnInProgress){
        timeline.stop()
        println("Circle Released")
        releaseTime = System.nanoTime()

        // Calculate the duration in seconds
        val duration = (releaseTime - pressTime).toDouble / 1e9
        val upperBound = maxDuration // Upper bound for normalization

        // Normalize duration
        val normalizedDuration = Math.min(duration, upperBound) / upperBound * maxVelocity
        turnInProgress = false
        pressDurationBar.visible = false
        userInputPromise.success(normalizedDuration)
      }
    }
    userInputPromise.future
  }

  def getComputerInput(): Double = game.difficultyLevel match {
    case "Easy" =>
      println("THIS IS EASY MODE")
      val start = 60
      val end = maxVelocity - 15
      val randomNumber = start + random.nextInt( (end - start) + 1 )
      randomNumber

    case "Medium" =>
      println("THIS IS MEDIUM MODE")
      val start = 80
      val end = maxVelocity - 15
      val randomNumber = start + random.nextInt( (end - start) + 1 )
      randomNumber

    case "Hard" =>
      println("THIS IS HARD MODE")
      val start = 95
      val end = 105
      val randomNumber = start + random.nextInt( (end - start) + 1 )
      randomNumber

  }

  def pause(): Unit = {
    MainApp.showPauseDialog()

  }



  def resetSuperpowerState(): Unit = {
    for (i <- 0 to game.currentPlayer.superpowers.length){
      game.currentPlayer.deactivateSuperpower(i)
    }
  }

  def applyDamageEffect(imageView: ImageView): Unit = {
    // Create a semi-transparent red rectangle overlay
    val redOverlay = new ColorInput {
      x = 0
      y = 0
      width <== imageView.fitWidth
      height <== imageView.fitHeight
      paint = Color.Red
    }

    // Create a blend effect to apply the red overlay
    val blend = new Blend {
      mode = BlendMode.SrcAtop
      opacity = 0.50
      topInput = redOverlay
    }

    imageView.effect = blend

    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(1000), onFinished = _ => imageView.effect = null)
      )
    }

    timeline.play()
  }

  def laughingAnimation(): Unit = {
    val bubbleText = if (game.currentPlayer == player) rightBubbleText else leftBubbleText
    showBubbleText(bubbleText)
  }

  def showBubbleText(bubbleText: ImageView): Unit = {
    bubbleText.visible = true
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(2000), onFinished = _ => bubbleText.visible = false)
      )
    }
    timeline.play()
  }

  def waitFor(duration: FiniteDuration): Future[Unit] = {
    val promise = Promise[Unit]()
    Future {
      Thread.sleep(duration.toMillis)
      promise.success(())
    }
    promise.future
  }


  def handlePlayerTurn(): Future[Unit] = {
      turnInProgress = true
      bone2.visible = false
      bone1.visible = true
      circlePane.visible = true
      println("Player shoots!")
      println(player.hp)

      bone1.setImage(playerBone)

      val wind = game.windValues(Random.nextInt(game.windValues.length))
      updateWindBar(wind)

      getUserInput().flatMap { normalizedDuration =>
        val velocity = normalizedDuration

        if (aimButtonClicked){
          val (x, y) = game.takeTurn(100, 1, 0)
          createTranslateTransition(bone1, x, y)
        }
        else{
          val (x, y) = game.takeTurn(velocity, 1, wind)
          createTranslateTransition(bone1, x, y)
        }
        println("bulldog hp is "+ Character.bulldog.stats.hp)

        waitFor(boneInterceptTime.seconds).map { _ =>
          if (game.currentPlayer.bone.isIntercept) {
            game.applyDamage()
            applyDamageEffect(charImage2)
          }
          else{
            laughingAnimation()
          }

          resetSuperpowerState()

        }
      }
  }

  def handleComputerTurn(): Future[Unit] = {
    turnInProgress = true
    println("Computer shoots!")
    bone2.visible = true
    bone1.visible = false
    circlePane.visible = false
    bone2.setImage(computerBone)
    println(computer.hp)

    val velocity = getComputerInput()
    val (x, y) = game.takeTurn(velocity, -1, 0)

    createTranslateTransition(bone2, x, y)
    turnInProgress = false

    waitFor(boneInterceptTime.seconds).map { _ =>
      Platform.runLater {
        if (game.currentPlayer.bone.isIntercept) {
          game.applyDamage()
          applyDamageEffect(charImage1)
        }
        else {
          laughingAnimation()
        }
      }
    }
  }

  def handleTurns(): Unit = {
    if (game.checkGameState()) {
      if (game.currentPlayer == player) {
        handlePlayerTurn().onComplete {
          case Success(_) =>
            waitFor(2.seconds).onComplete {
              case Success(_) =>
                game.switchTurn()

                Platform.runLater(handleTurns)
            }
        }
      }
      else {
        handleComputerTurn().onComplete {
          case Success(_) =>
            waitFor(2.seconds).onComplete {
              case Success(_) =>
                game.switchTurn()
                resetBonePosition()
                Platform.runLater(handleTurns)
            }
            }

      }
    }
    else {
      println("Game over!")
      if (game.player.isAlive){
        Platform.runLater(() => MainApp.showVictoryDialog())
      }
      else{
        Platform.runLater(() => MainApp.showLoseDialog())
      }
    }
  }


}
