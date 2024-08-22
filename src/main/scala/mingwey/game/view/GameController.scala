package mingwey.game.view

import mingwey.game.MainApp._
import mingwey.game.view.GameConstants._
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.Platform
import scalafx.beans.property.DoubleProperty
import scalafx.scene.control.{Button, ProgressBar}
import scalafx.scene.effect.{Blend, BlendMode, ColorInput}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.StackPane
import scalafx.scene.media.AudioClip
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.util.Duration
import scalafxml.core.macros.sfxml

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Random, Success}
import scala.concurrent.duration.{DurationDouble, DurationInt, FiniteDuration}

@sfxml
class GameController(
                      private val holdCircle: Circle,
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

                    ) {

  def initialize(): Unit = {
    handleCoordinates()
    game.checkPlayerIntersectionRange()
    bindProgressBar()
    bindPoisonButton()
    bindHealButton()
    bindAimButton()
    resetBonePosition()
    game.resetSuperpowerState()
    handleTurns()
  }

  //Flag to track if a turn is currently in progress.
  var turnInProgress = false

  // Stores the time it takes for a bone to intercept.
  var boneInterceptTime : Double = 0

  // Load the character image
  val playerImage = new Image(getClass.getResourceAsStream(player.img.value))
  val computerImage = new Image(getClass.getResourceAsStream(computer.img.value))
  val playerBone = new Image(getClass.getResourceAsStream(player.bone.img.value))
  val computerBone = new Image(getClass.getResourceAsStream(computer.bone.img.value))

  // Set the loaded images to the respective ImageView elements.
  charImage1.setImage(playerImage)
  charImage2.setImage(computerImage)
  bone1.setImage(playerBone)
  bone2.setImage(computerBone)
  imageAdjust()

  // Adjusts the player character's image to fit the appropriate dimensions.
  def imageAdjust(): Unit = {
    charImage1.fitWidth = playerImage.width.value
    if (playerImage.height.value < charImage1.fitHeight.value){
      charImage1.fitHeight = playerImage.height.value
    }
    else{
      charImage1.layoutY = charImage1.getLayoutY - (playerImage.height.value - charImage1.fitHeight.value)
      charImage1.fitHeight = playerImage.height.value
    }
  }

  // Retrieves the coordinates of a character's image
  def getCharCoordinates(imageView: ImageView): ((Double, Double), (Double, Double)) = {
    val xCoor = (imageView.layoutX.value, imageView.layoutX.value + imageView.getFitWidth)
    val yCoor = ( imageView.layoutY.value,imageView.layoutY.value + playerImage.height.value)
    (xCoor, yCoor)
  }

  // Retrieves the coordinates and dimensions of a bone's image
  def getBoneCoordinates(bone: ImageView): ((Double, Double),  (Double, Double), Double, Double) = {
    val xCoor = (bone.layoutX.value, bone.layoutX.value + bone.getFitWidth)
    val yCoor = (bone.layoutY.value , bone.layoutY.value  + bone.getFitHeight)
    val width = bone.getFitWidth
    val height = bone.getFitHeight
    (xCoor, yCoor, width, height)
  }

  // Setting the coordinates of the characters and bone retrieved from scene builder to perform calculation
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

  // Resets the positions of the bones to their initial coordinates.
  def resetBonePosition(): Unit = {
      bone1.layoutX = player.bone.xCoordinate._1
      bone1.layoutY = player.bone.yCoordinate._1

      bone2.layoutX = computer.bone.xCoordinate._1
      bone2.layoutY = computer.bone.yCoordinate._1
  }

  // Binds the progress bars to reflect the player's and computer's HP.
  def bindProgressBar(): Unit = {
    updateHpBar(player.hp, playerHpBar, player.stats.hp)
    updateHpBar(computer.hp, computerHpBar, computer.stats.hp)
  }

  // Update the HP bar based on the current HP value
  def updateHpBar(hpProperty: DoubleProperty, hpBar: ProgressBar, totalHp: Int): Unit = {
    hpProperty.addListener((_, _, newValue) => {
      hpBar.setProgress(newValue.doubleValue() / totalHp)
    })
  }

  // Binds the Poison button's action to apply the Poison superpower when clicked.
  def bindPoisonButton(): Unit = {
    poisonButton.onMouseClicked = e => {
      if (turnInProgress && game.currentPlayer == player) {
        game.currentPlayer.useSuperpower(0)
        if (game.currentPlayer.superpowers(0).isActive) {
          playIncreaseDamageEffect()
          poisonButton.disable = true
        }
      }
    }
  }

  // Binds the Heal button's action to apply the Heal superpower when clicked.
  def bindHealButton(): Unit = {
    healButton.onMouseClicked = e => {
      if (turnInProgress && game.currentPlayer == player) {
        game.currentPlayer.useSuperpower(1)
        if (game.currentPlayer.superpowers(1).isActive){
          playHealEffect()
          healButton.disable = true
        }

      }
    }
  }

  // Binds the Aim button's action to apply the Aim superpower when clicked.
  def bindAimButton(): Unit = {
    aimButton.onMouseClicked = e => {
      if (turnInProgress && game.currentPlayer == player) {
        game.currentPlayer.useSuperpower(2)
        if (game.currentPlayer.superpowers(2).isActive){
          playAimEffect()
          aimButton.disable = true
        }
      }
    }
  }

  // Move the ImageView by the specified X and Y amounts
  def moveImageView(imageView: ImageView, amountX: Double, amountY : Double): Unit = {
    imageView.layoutX = (imageView.layoutX + amountX).doubleValue()
    imageView.layoutY = (imageView.layoutY + amountY).doubleValue()
  }

  // Create a translate transition for the bone's movement, updating position step by step
  def createTranslateTransition(imageView: ImageView, xCoordinates: ArrayBuffer[Double], yCoordinates: ArrayBuffer[Double]): Unit = {
    var index = 0
    boneInterceptTime = (xCoordinates.length * AnimationDuration) / 60

    // Recursive function to handle the bone's continuous movement between coordinates
    def nextTransition(): Unit = {
      if (index < xCoordinates.length - 1) {

        val xDiff = xCoordinates(index + 1) - xCoordinates(index)
        val yDiff = -(yCoordinates(index) - yCoordinates(index + 1))

        val timeline = new Timeline {
          cycleCount = 1
          keyFrames = Seq(
            KeyFrame(Duration(AnimationDuration), onFinished = _ => {
              moveImageView(imageView, xDiff, yDiff)
              index += 1
              nextTransition() // Continue to next movement step
            })
          )
        }
        timeline.play()
      }
    }
    nextTransition()
  }

  // Update the wind bars based on the current wind value
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

  // Apply a red damage effect overlay on the character's image
  def applyDamageEffect(imageView: ImageView): Unit = {
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

    // Remove the damage effect after a duration
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(DamageEffectDurationMs), onFinished = _ => imageView.effect = null)
      )
    }

    timeline.play()
  }

  // Show a laughing animation when the bone missed the character
  def laughingAnimation(): Unit = {
    val bubbleText = if (game.currentPlayer == player) rightBubbleText else leftBubbleText
    showBubbleText(bubbleText)
  }

  def showBubbleText(bubbleText: ImageView): Unit = {
    bubbleText.visible = true
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(BubbleTextDisplayDurationMs), onFinished = _ => bubbleText.visible = false)
      )
    }
    timeline.play()
  }

  // Play sound effect when increase damage superpower is used
  def playIncreaseDamageEffect(): Unit = {
    val effect = new AudioClip(getClass.getResource("/audio/increaseDamageEffect.wav").toString)
    effect.play()
  }

  // Play sound effect when heal superpower is used
  def playHealEffect(): Unit = {
    val effect = new AudioClip(getClass.getResource("/audio/healEffect.wav").toString)
    effect.play()
  }

  // Play sound effect when aim superpower is used
  def playAimEffect(): Unit = {
    val effect = new AudioClip(getClass.getResource("/audio/aimEffect.wav").toString)
    effect.play()
  }

  // Play sound effect when character is damaged by the bone
  def playInjuredEffect(): Unit = {
    val effect = new AudioClip(getClass.getResource("/audio/hitEffect.wav").toString)
    effect.play()
  }

  // Play sound effect when character misses their bone
  def playLaughEffect(): Unit = {
    val effect = new AudioClip(getClass.getResource("/audio/laughEffect.wav").toString)
    effect.play()
  }

  // Handle the event when the bone intercepts the target
  def handleBoneIntercept(charImage: ImageView): Unit = {
    if (game.currentPlayer.bone.isIntercept) {
      game.applyDamage()
      applyDamageEffect(charImage)
      playInjuredEffect()
    }
    else {
      playLaughEffect()
      laughingAnimation()
    }
  }

  // Utility function to wait for a specified duration
  def waitFor(duration: FiniteDuration): Future[Unit] = {
    val promise = Promise[Unit]()
    Future {
      Thread.sleep(duration.toMillis)
      promise.success(())
    }
    promise.future
  }

  // Capture the player's input based on how long the mouse is pressed
  def getUserInput(): Future[Double] = {
    // Variables to track mouse press duration
    val userInputPromise = Promise[Double]()
    var pressTime: Long = 0
    var releaseTime: Long = 0

    // Timeline to update the progress bar
    val updateInterval = Duration(ProgressBarUpdateIntervalMs)
    val timeline = new Timeline {
      cycleCount = Timeline.Indefinite
      keyFrames = Seq(
        KeyFrame(updateInterval, onFinished = _ => {
          val currentTime = System.nanoTime()
          val duration = (currentTime - pressTime).toDouble / 1e9
          val normalizedDuration = Math.min(duration / MaxMouseDuration, 1.0)
          pressDurationBar.progress = normalizedDuration
        })
      )
    }

    // Mouse press and release handlers
    holdCircle.onMousePressed = e => {
      if (turnInProgress  && game.currentPlayer == player){
        pressTime = System.nanoTime()
        pressDurationBar.progress = 0
        pressDurationBar.visible = true
        timeline.play()
      }
    }

    holdCircle.onMouseReleased = e => {
      if (turnInProgress){
        timeline.stop()
        releaseTime = System.nanoTime()

        // Calculate the duration in seconds
        val duration = (releaseTime - pressTime).toDouble / 1e9
        val upperBound = MaxMouseDuration // Upper bound for normalization

        // Normalize duration
        val normalizedDuration = Math.min(duration, upperBound) / upperBound * game.maxVelocity
        turnInProgress = false
        pressDurationBar.visible = false
        userInputPromise.success(normalizedDuration)
      }
    }
    userInputPromise.future
  }


  // Handle the player's turn in the game
  def handlePlayerTurn(): Future[Unit] = {
    turnInProgress = true
    bone2.visible = false
    bone1.visible = true
    circlePane.visible = true

    val wind = game.windValues(Random.nextInt(game.windValues.length))
    updateWindBar(wind)

    getUserInput().flatMap { normalizedDuration =>
      val velocity = normalizedDuration

      if (game.currentPlayer.superpowers(2).isActive){
        val (x, y) = game.takeTurn((game.playerIntersectionRange._1 + game.playerIntersectionRange._2 ) / 2, 0)
        createTranslateTransition(bone1, x, y)
      }
      else{
        val (x, y) = game.takeTurn(velocity, wind)
        createTranslateTransition(bone1, x, y)
      }

      waitFor(boneInterceptTime.seconds).map { _ =>
        handleBoneIntercept(charImage2)
        game.resetSuperpowerState()
      }
    }
  }

  // Handle the computer's turn in the game
  def handleComputerTurn(): Future[Unit] = {
    turnInProgress = true
    bone2.visible = true
    bone1.visible = false
    circlePane.visible = false

    val velocity = game.getComputerInput
    val (x, y) = game.takeTurn(velocity, 0)

    createTranslateTransition(bone2, x, y)
    turnInProgress = false

    waitFor(boneInterceptTime.seconds).map { _ =>
      Platform.runLater {
        handleBoneIntercept(charImage1)
      }
    }
  }

  // Handle the alternating turns between the player and the computer
  def handleTurns(): Unit = {
    if (game.isGameOngoing) {
      if (game.currentPlayer == player) {
        handlePlayerTurn().onComplete {
          case Success(_) =>
            waitFor(TurnTransitionDelaySec.seconds).onComplete {
              case Success(_) =>
                game.switchTurn()
                Platform.runLater(handleTurns)
            }
        }
      }
      else {
        handleComputerTurn().onComplete {
          case Success(_) =>
            waitFor(TurnTransitionDelaySec.seconds).onComplete {
              case Success(_) =>
                game.switchTurn()
                resetBonePosition()
                Platform.runLater(handleTurns)
            }
            }

      }
    }
    else {
      // Display victory or loss dialog based on game outcome
      if (game.player.isAlive){
        Platform.runLater(() => showVictoryDialog())
      }
      else{
        Platform.runLater(() => showLoseDialog())
      }
    }
  }
}

object GameConstants {
  val MaxMouseDuration: Double = 2.0
  val AnimationDuration: Double = 1.0
  val ProgressBarUpdateIntervalMs: Int = 100
  val BubbleTextDisplayDurationMs: Int = 2000
  val DamageEffectDurationMs: Int = 1000
  val TurnTransitionDelaySec: Int = 2
}
