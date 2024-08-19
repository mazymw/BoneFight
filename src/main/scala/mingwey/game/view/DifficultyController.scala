package mingwey.game.view

import scalafx.scene.control.Button
import scalafxml.core.macros.sfxml
import mingwey.game.MainApp._


@sfxml
class DifficultyController(private val easyButton: Button,
                           private val mediumButton: Button,
                           private val hardButton: Button
                          ){


  def initialize(): Unit = {
    bindDifficultyButtons()
  }

  def bindDifficultyButtons(): Unit = {
    easyButton.onMouseClicked = _ => setDifficultyAndStartGame("Easy")
    mediumButton.onMouseClicked = _ => setDifficultyAndStartGame("Medium")
    hardButton.onMouseClicked = _ => setDifficultyAndStartGame("Hard")
  }

  def setDifficultyAndStartGame(difficulty: String): Unit = {
    game.difficultyLevel = difficulty
    showGame()
  }





}