package mingwey.game
import javafx.{scene => jfxs}
import mingwey.game.model.{Bone, Character, Game}
import mingwey.game.view.{ChooseCharacterController, DialogController, DifficultyController, GameController, HomeController}
import scalafx.Includes._
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}



object MainApp extends JFXApp {
  var mediaPlayer: MediaPlayer = null

  // Transform path of RootLayout.fxml to URI for resource location.
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  // Initialize the loader object.
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  // Load root layout from fxml file.
  loader.load();
  // Retrieve the root component BorderPane from the FXML
  val roots = loader.getRoot[jfxs.layout.BorderPane]
  // Initialize stage
  stage = new PrimaryStage {
    title = "AddressApp"
    scene = new Scene {
      root = roots
    }
  }

  // Show the home page
  def showHome() = {
    val resource = getClass.getResource("view/Home.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]

    this.roots.setCenter(roots)

    val controller = loader.getController[HomeController#Controller]
    controller.initialize()
  }

  // Show the how to play page
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


  // Show the choose character page
  def showChooseCharacter(): Unit = {
    val resource = getClass.getResource("view/ChooseCharacter.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
    val controller = loader.getController[ChooseCharacterController#Controller]
    controller.initialize()
  }

  //Show the choose difficulty page
  def showDifficulty(): Unit = {
    val resource = getClass.getResource("view/Difficulty.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
    val controller = loader.getController[DifficultyController#Controller]
    controller.initialize()
  }

  // Show the game page
  def showGame(): Unit = {
    val resource = getClass.getResource("view/Game.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
    val controller = loader.getController[GameController#Controller]
    controller.initialize()
  }

  // Show the victory dialog
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
    playVictoryEffect()
    controller.dialogStage = dialog
    dialog.showAndWait()
  }

  // Show the lose dialog
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
    playLoseEffect()
    controller.dialogStage = dialog
    dialog.showAndWait()
  }

  // Show the alert when character is not selected
  def showAlert(): Unit = {
    val alert = new Alert(AlertType.Warning) {
      initOwner(MainApp.stage)
      title = "No Character Selected"
      headerText = "Character Selection Required"
      contentText = "Please select a character before starting the game."
    }
    alert.showAndWait()
  }


  // Play the background Music
  def playMusic(filePath: String, loop: Boolean = false): Unit = {
    val musicResource = getClass.getResource(filePath)
    val media = new Media(musicResource.toString)
    if (mediaPlayer != null) mediaPlayer.stop()
    mediaPlayer = new MediaPlayer(media)
    mediaPlayer.setCycleCount(if (loop) MediaPlayer.Indefinite else 1)
    mediaPlayer.play()
  }

  def playHomeMusic(): Unit = {
    playMusic("/audio/HomePageBackgroundMusic.mp3", loop = true)
  }

  def playGameMusic(): Unit = {
    playMusic("/audio/gameBackgroundMusic.mp3", loop = true)
  }

  def playVictoryEffect(): Unit = {
    playMusic("/audio/winEffect.wav")
  }

  def playLoseEffect(): Unit = {
    playMusic("/audio/loseEffect.wav")
  }

  var player = Character.dinosaur
  var computer = Character.bulldog
  var game = new Game(player,computer)

  // Initialize a game
  def createGame(playerChar: Character): Unit = {
    player = playerChar
    computer = Character.bulldog
    game = new Game(player,computer)
  }

  showHome()
}
