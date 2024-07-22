package mingwey.game.view
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml

@sfxml
class DifficultyController(private val easyButton: Button,
                           private val mediumButton: Button,
                           private val hardButton: Button,
                          ){
  def startGame(): Unit = {

  }
}