package mingwey.game.view

import mingwey.game.MainApp._
import scalafx.scene.control.Button
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml



@sfxml
class DialogController(
                        private val playAgainButton: Button,
                        private val exitButton: Button,
                      )
{
  var dialogStage : Stage  = null


  def playAgain(): Unit = {
    showDifficulty()
    game.resetVariables()
    dialogStage.close()
  }

  def exit(): Unit = {
    showHome()
    game.resetVariables()
    dialogStage.close()
  }

  def continueGame(): Unit = {
    dialogStage.close()
  }

}