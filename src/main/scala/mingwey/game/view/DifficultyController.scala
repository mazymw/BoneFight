package mingwey.game.view
import mingwey.game.MainApp
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml
import mingwey.game.MainApp._
import scalafx.stage.Stage

@sfxml
class DifficultyController(private val easyButton: Button,
                           private val mediumButton: Button,
                           private val hardButton: Button
                          ){


  def initialize(): Unit = {
    bindDifficultyButtons()
  }

  def bindDifficultyButtons(): Unit = {
    easyButton.onMouseClicked = _ => setDifficultyAndStartGame("Easy")
    mediumButton.onMouseClicked = _ => setDifficultyAndStartGame("Medium")
    hardButton.onMouseClicked = _ => setDifficultyAndStartGame("Hard")
  }

  def setDifficultyAndStartGame(difficulty: String): Unit = {
    game.difficultyLevel = difficulty
    MainApp.showGame()
  }





}