package mingwey.game.view
import scalafx.scene.image.ImageView
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml

@sfxml
class HomeController( private val title: Text,
                      private val catImage: ImageView
                    ){
  def startGame(): Unit = {

  }
}