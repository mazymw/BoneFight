package mingwey.game.view

import scalafx.scene.control.{Button, ProgressBar}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml
import mingwey.game.MainApp._
import scalafx.scene.input.MouseEvent
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

                          ){


  var selectedCharacter: Character = Character.cat
  val characters: Seq[Character] = Character.allCharacters
  val characterMap: Map[(Int, Int), Character] = createCharacterMap()
  var allStackPane: ArrayBuffer[javafx.scene.layout.StackPane] = ArrayBuffer[javafx.scene.layout.StackPane]()
  var isCharacterSelected: Boolean = false

  def initialize(): Unit = {
    playGameMusic()
    addClickListenersToStackPanes()
  }

  private def createCharacterMap(): Map[(Int, Int), Character] = {
    characters.zipWithIndex.flatMap { case (character, index) =>
      val row = index / 2//two columns
      val col = index % 2
      Some((row, col) -> character)
    }.toMap
  }

  def addClickListenersToStackPanes(): Unit = {
    gridPane.getChildren.forEach {
      case stackPane: javafx.scene.layout.StackPane =>
        allStackPane += stackPane
        val row = GridPane.getRowIndex(stackPane)
        val col = GridPane.getColumnIndex(stackPane)

        stackPane.onMouseClicked = _ => {
          handleCharacterSelection(row, col,stackPane)
          isCharacterSelected = true
        }
      case _ =>
        isCharacterSelected = false
    }
  }

  def handleCharacterSelection(row: Int, col: Int, clickedPane: StackPane): Unit= {

    for (stackPane <- allStackPane) {
      stackPane.getStyleClass.remove("stack-pane-selected")
    }

    clickedPane.getStyleClass.add("stack-pane-selected")

    characterMap.get((row, col)) match {
      case Some(character) =>
        selectedCharacter = character
      case _ =>

    }
    displaySelectedCharacter()

  }

  def displaySelectedCharacter(): Unit = {
    characterName.text = selectedCharacter.name
    hpProgressBar.progress = selectedCharacter.stats.hp.toDouble / Character.allCharacters.maxBy(_.stats.hp).stats.hp.toDouble
    atkProgressBar.progress = selectedCharacter.stats.atk.toDouble / Character.allCharacters.maxBy(_.stats.atk).stats.atk.toDouble
    val characterImage = new Image(getClass.getResourceAsStream(selectedCharacter.img.value))
    characterImg.setImage(characterImage)

    characterImg.setPreserveRatio(true) // Maintain the aspect ratio
    characterImg.setFitWidth(characterImg.getFitWidth) // Ensure it fits the width
    characterImg.setFitHeight(characterImg.getFitHeight)
  }


  def startGame(): Unit = {
    if (isCharacterSelected) {
      createGame(selectedCharacter)
      showDifficulty()
    } else {
      showAlert()
    }
  }


}