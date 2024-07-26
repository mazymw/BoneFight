package mingwey.game
import javafx.{scene => jfxs}
import mingwey.game.model.{Bone, Character, Game}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafx.stage.StageStyle
import scalafxml.core.{FXMLLoader, NoDependencyResolver}


object MainApp extends JFXApp {


  // transform path of RootLayout.fxml to URI for resource location.
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  // initialize the loader object.
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  // Load root layout from fxml file.
  loader.load();
  // retrieve the root component BorderPane from the FXML
  val roots = loader.getRoot[jfxs.layout.BorderPane]
  // initialize stage
  stage = new PrimaryStage {
    title = "AddressApp"
//    initStyle(StageStyle.Undecorated)
    scene = new Scene {
      root = roots
    }
  }

  def showHome() = {
    val resource = getClass.getResource("view/Home.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  def showGame(): Unit = {
    val resource = getClass.getResource("view/Game.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    //this refer to main app ( refers to the border pane up there)
    this.roots.setCenter(roots)
  }

  val player = Character.dinosaur
  val computer = Character.bulldog

  val game = new Game(player,computer)




  // call to display home when app start
  showHome()







}
