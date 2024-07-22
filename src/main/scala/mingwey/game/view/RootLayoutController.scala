package mingwey.game.view
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController(){
  def handleClose(): Unit = {
    System.exit(0)
  }
}