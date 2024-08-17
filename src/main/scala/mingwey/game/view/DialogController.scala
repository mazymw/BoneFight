package mingwey.game.view

import mingwey.game.MainApp
import mingwey.game.model.Character
import mingwey.game.MainApp._
import scalafx.animation.{KeyFrame, Timeline, TranslateTransition}
import scalafx.scene.control.{Button, ProgressBar}
import scalafx.scene.effect.{Blend, BlendMode, ColorAdjust, ColorInput}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{AnchorPane, StackPane}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.stage.Stage
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
class VictoryDialogController(
                               private val playAgainButton: Button,
                               private val exitButton: Button,

                             )
{
  var dialogStage : Stage  = null
  game.resetVariables()

  def playAgain(): Unit = {
    MainApp.showDifficulty()
    dialogStage.close()
  }

  def exit(): Unit = {
    MainApp.showHome()

    dialogStage.close()
  }

}