package mingwey.game
import javafx.{scene => jfxs}
import mingwey.game.model.{Bone, Character, Game}
import mingwey.game.view.{ChooseCharacterController, DialogController, DifficultyController, GameController, HomeController}
import scalafx.Includes._
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafx.stage.{Modality, Stage, StageStyle}
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
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]

    this.roots.setCenter(roots)

    val controller = loader.getController[HomeController#Controller]
    controller.initialize()
  }

  def showInstructionDialog(): Unit = {
    val resource = getClass.getResource("view/InstructionDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots2 = loader.getRoot[jfxs.Parent]
    val controller = loader.getController[DialogController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene {
        root = roots2
      }
    }

    controller.dialogStage = dialog
    dialog.showAndWait()
  }

  def showGame(): Unit = {
    val resource = getClass.getResource("view/Game.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    //this refer to main app ( refers to the border pane up there)
    this.roots.setCenter(roots)
    val controller = loader.getController[GameController#Controller]
    controller.initialize()
  }

  def showChooseCharacter(): Unit = {
    val resource = getClass.getResource("view/ChooseCharacter.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    //this refer to main app ( refers to the border pane up there)
    this.roots.setCenter(roots)
    val controller = loader.getController[ChooseCharacterController#Controller]
    controller.initialize()
  }

  def showDifficulty(): Unit = {
    val resource = getClass.getResource("view/Difficulty.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    //this refer to main app ( refers to the border pane up there)
    this.roots.setCenter(roots)
    val controller = loader.getController[DifficultyController#Controller]
    controller.initialize()
  }

  def showPauseDialog(): Unit = {
    val resource = getClass.getResource("view/PauseDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots2 = loader.getRoot[jfxs.layout.AnchorPane]
    val controller = loader.getController[DialogController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene {
        root = roots2
      }
    }
    controller.dialogStage = dialog
    dialog.showAndWait()
  }

  def showVictoryDialog(): Unit = {
    val resource = getClass.getResource("view/VictoryDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots2 = loader.getRoot[jfxs.layout.AnchorPane]
    val controller = loader.getController[DialogController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene {
        root = roots2
      }
    }

    controller.dialogStage = dialog
    dialog.showAndWait()
  }

  def showLoseDialog(): Unit = {
    val resource = getClass.getResource("view/LoseDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots2 = loader.getRoot[jfxs.layout.AnchorPane]
    val controller = loader.getController[DialogController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene {
        root = roots2
      }
    }
    controller.dialogStage = dialog
    dialog.showAndWait()
  }

  var player = Character.dinosaur
  var computer = Character.bulldog
  var game = new Game(player,computer)


//  def createGame(): Unit = {
//    player = Character.dinosaur
//    computer = Character.bulldog
//
//    game = new Game(player,computer)
//
//  }

//  val player = Character.dinosaur
//  val computer = Character.bulldog
//
//  val game = new Game(player,computer)




  // call to display home when app start
  showHome()







}
