package mingwey.game.view

import scalafx.scene.control.{Button, ProgressBar}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml
import mingwey.game.MainApp._
import scalafx.scene.layout.{GridPane, StackPane}
import scalafx.Includes._
import mingwey.game.model.Character
import scala.collection.mutable.ArrayBuffer

@sfxml
class ChooseCharacterController(
                                 private val gridPane: GridPane,
                                 private val characterName: Text,
                                 private val hpProgressBar: ProgressBar,
                                 private val atkProgressBar: ProgressBar,
                                 private val characterImg: ImageView,
                               ) {

  // Variable to hold the selected character
  var selectedCharacter: Option[Character] = None

  // Map to store the characters' positions in the grid
  val characterMap: Map[(Int, Int), Character] = createCharacterMap()

  // List to store all StackPane elements for easy access
  var allStackPane: ArrayBuffer[javafx.scene.layout.StackPane] = ArrayBuffer[javafx.scene.layout.StackPane]()

  // Maximum values for HP and ATK to normalize progress bars
  val maxHp: Double = Character.allCharacters.maxBy(_.stats.hp).stats.hp.toDouble
  val maxAtk: Double = Character.allCharacters.maxBy(_.stats.atk).stats.atk.toDouble


  def initialize(): Unit = {
    playGameMusic() // Starts playing game background music
    addClickListenersToStackPanes() // Adds click listeners to each StackPane
  }

  // Method to create a map of grid positions to characters
  def createCharacterMap(): Map[(Int, Int), Character] = {
    Character.allCharacters.zipWithIndex.flatMap { case (character, index) =>
      val row = index / 2 //2 columns
      val col = index % 2
      Some((row, col) -> character) // Map the position to the character
    }.toMap
  }

  // Method to add click listeners to all StackPane elements in the grid
  def addClickListenersToStackPanes(): Unit = {
    gridPane.getChildren.forEach {
      case stackPane: javafx.scene.layout.StackPane =>
        allStackPane += stackPane // Store the StackPane in the list
        val row = GridPane.getRowIndex(stackPane) // Get the row of the StackPane
        val col = GridPane.getColumnIndex(stackPane) // Get the column of the StackPane

        // Set up the click event handler for the StackPane
        stackPane.onMouseClicked = _ => {
          handleCharacterSelection(row, col, stackPane)
        }
      case _ =>
    }
  }

  // Method to handle character selection when a StackPane is clicked
  def handleCharacterSelection(row: Int, col: Int, clickedPane: StackPane): Unit = {

    // Remove the selected style from all StackPane elements
    for (stackPane <- allStackPane) {
      stackPane.getStyleClass.remove("stack-pane-selected")
    }

    // Add the selected style to the clicked StackPane
    clickedPane.getStyleClass.add("stack-pane-selected")

    // Update the selected character based on the clicked position
    characterMap.get((row, col)) match {
      case Some(character) =>
        selectedCharacter = Some(character)
    }

    // Display the selected character's details
    displaySelectedCharacter()
  }

  // Method to display the selected character's details in the UI
  def displaySelectedCharacter(): Unit = {
    selectedCharacter match {
      case Some(character) =>
        characterName.text = character.name // Set the character name
        hpProgressBar.progress = character.stats.hp.toDouble / maxHp // Set HP progress
        atkProgressBar.progress = character.stats.atk.toDouble / maxAtk // Set ATK progress
        val characterImage = new Image(getClass.getResourceAsStream(character.img.value))
        characterImg.setImage(characterImage) // Set the character image

        // Adjust image properties to fit and preserve the aspect ratio
        characterImg.setPreserveRatio(true)
        characterImg.setFitWidth(characterImg.getFitWidth)
        characterImg.setFitHeight(characterImg.getFitHeight)
    }
  }

  // Method to start the game with the selected character
  def startGame(): Unit = {
    selectedCharacter match {
      case Some(character) =>
        createGame(character) // Create a new game with the selected character
        showDifficulty() // Show difficulty selection screen
      case None =>
        showAlert() // Show an alert if no character is selected
    }
  }
}
