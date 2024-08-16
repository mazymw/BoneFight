package mingwey.game.view
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml
import mingwey.game.MainApp._

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

    }

    mediumButton.onMouseClicked = _ => {
      game.difficultyLevel = "Medium"

    }

    hardButton.onMouseClicked = _ => {
      game.difficultyLevel = "Hard"

    }
  }





}