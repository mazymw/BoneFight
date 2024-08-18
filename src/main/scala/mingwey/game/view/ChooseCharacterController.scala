package mingwey.game.view
import mingwey.game.MainApp
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml
import mingwey.game.MainApp._
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{GridPane, StackPane}
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.scene.layout.GridPane.getColumnIndex
import mingwey.game.model.Character

import scala.collection.mutable.ArrayBuffer


@sfxml
class ChooseCharacterController(
                                 private val gridPane: GridPane,


                          ){


  val characters = List(Character.blueCat, Character.bulldog)
  private val characterMap: Map[(Int, Int), Character] = createCharacterMap()
  var allStackPane: ArrayBuffer[javafx.scene.layout.StackPane] = ArrayBuffer[javafx.scene.layout.StackPane]()

  def initialize(): Unit = {
    addClickListenersToStackPanes()
  }

  private def createCharacterMap(): Map[(Int, Int), Character] = {
    characters.zipWithIndex.flatMap { case (character, index) =>
      val row = index / 3 // Assuming 3 columns
      val col = index % 3
      Some((row, col) -> character)
    }.toMap
  }

  def addClickListenersToStackPanes(): Unit = {
    gridPane.getChildren.forEach {
      case stackPane: javafx.scene.layout.StackPane =>
        allStackPane += stackPane
        val row = GridPane.getRowIndex(stackPane)
        val col = GridPane.getColumnIndex(stackPane)
        println(row + col)
        stackPane.onMouseClicked = e => {
          handleCharacterSelection(row, col,stackPane)


        }
      case _ =>
    }
  }

  def handleCharacterSelection(row: Int, col: Int,clickedPane: StackPane): Character = {
    var selectedCharacter: Character = null
    for (stackPane <- allStackPane) {
      stackPane.getStyleClass.remove("stack-pane-selected")
    }

    clickedPane.getStyleClass.add("stack-pane-selected")



    characterMap.get((row, col)) match {
      case Some(character) =>
        selectedCharacter = character
      case _ =>

    }
    selectedCharacter
  }




}