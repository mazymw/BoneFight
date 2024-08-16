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
    easyButton.onMouseClicked = _ => {
      game.difficultyLevel = "Easy"
      MainApp.showGame()
    }

    mediumButton.onMouseClicked = _ => {
      game.difficultyLevel = "Medium"
      MainApp.showGame()
    }

    hardButton.onMouseClicked = _ => {
      game.difficultyLevel = "Hard"
      MainApp.showGame()
    }
  }





}