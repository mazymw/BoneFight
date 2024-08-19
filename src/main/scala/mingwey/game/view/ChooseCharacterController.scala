package mingwey.game.view

import scalafx.scene.control.ProgressBar
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
                                 private val characterImg: ImageView
                               ) {

  var selectedCharacter: Character = Character.cat
  val characters: Seq[Character] = Character.allCharacters // List of all available characters
  val characterMap: Map[(Int, Int), Character] = createCharacterMap() // Map to link grid positions to characters
  var allStackPane: ArrayBuffer[StackPane] = ArrayBuffer[StackPane]() // ArrayBuffer holding all StackPanes
  var isCharacterSelected: Boolean = false // Flag to check if a character has been selected


  def initialize(): Unit = {
    playGameMusic()
    addClickListenersToStackPanes()
  }

  // Creates a map of grid coordinates to corresponding characters
  private def createCharacterMap(): Map[(Int, Int), Character] = {
    characters.zipWithIndex.flatMap { case (character, index) =>
      val row = index / 2 // Two characters per row
      val col = index % 2
      Some((row, col) -> character)
    }.toMap
  }

  // Adds click listeners to each StackPane in the grid
  def addClickListenersToStackPanes(): Unit = {
    gridPane.getChildren.forEach {
      case stackPane: StackPane =>
        allStackPane += stackPane
        val row = GridPane.getRowIndex(stackPane)
        val col = GridPane.getColumnIndex(stackPane)

        stackPane.onMouseClicked = _ => {
          handleCharacterSelection(row, col, stackPane) // Handle character selection when clicked
          isCharacterSelected = true // Mark that a character has been selected
        }
      case _ =>
        isCharacterSelected = false
    }
  }

  // Handles the logic when a character is selected
  def handleCharacterSelection(row: Int, col: Int, clickedPane: StackPane): Unit = {

    // Remove the "selected" style from all StackPanes
    for (stackPane <- allStackPane) {
      stackPane.getStyleClass.remove("stack-pane-selected")
    }

    // Add the "selected" style to the clicked StackPane
    clickedPane.getStyleClass.add("stack-pane-selected")

    // Update the selected character based on the clicked position
    characterMap.get((row, col)) match {
      case Some(character) =>
        selectedCharacter = character
      case _ => // Do nothing if no character is found
    }
    displaySelectedCharacter() // Update the display to show selected character details
  }

  // Displays the selected character's details (name, HP, attack, and image)
  def displaySelectedCharacter(): Unit = {
    characterName.text = selectedCharacter.name
    hpProgressBar.progress = selectedCharacter.stats.hp.toDouble / Character.allCharacters.maxBy(_.stats.hp).stats.hp.toDouble
    atkProgressBar.progress = selectedCharacter.stats.atk.toDouble / Character.allCharacters.maxBy(_.stats.atk).stats.atk.toDouble
    val characterImage = new Image(getClass.getResourceAsStream(selectedCharacter.img.value))
    characterImg.setImage(characterImage)

    characterImg.setPreserveRatio(true)
    characterImg.setFitWidth(characterImg.getFitWidth)
    characterImg.setFitHeight(characterImg.getFitHeight)
  }

  // Starts the game with the selected character
  def startGame(): Unit = {
    if (isCharacterSelected) { // Proceed if a character has been selected
      createGame(selectedCharacter)
      showDifficulty()
    } else {
      showAlert() // Show an alert if no character is selected
    }
  }

}
