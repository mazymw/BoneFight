package mingwey.game.view
import mingwey.game.MainApp._
import scalafx.scene.image.ImageView
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml

@sfxml
class HomeController( private val title: Text,
                      private val catImage: ImageView
                    ){

  def initialize(): Unit = {
    playHomeMusic()
  }

  def startGame(): Unit = {
    showChooseCharacter()
  }

  def showInstruction(): Unit = {
    showInstructionDialog()
  }


}