
package mingwey.game.view

import mingwey.game.model.Character
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.AnchorPane
import scalafx.scene.shape.Circle
import scalafx.scene.transform.Translate
import scalafxml.core.macros.sfxml

import java.time.Instant


@sfxml
class GameController( private val circle: Circle,
                      private val charImage : ImageView
                    ){


  var pressTime = 0f
  var releaseTime = 0f
  circle.onMousePressed = e => {
    pressTime = System.nanoTime()
    println(pressTime)
  }

  circle.onMouseReleased = e => {
    releaseTime = System.nanoTime()
    val duration = (releaseTime - pressTime) / 1000000000
    println(releaseTime)
    println
    println(duration)
    println
  }

  val character: Character = Character.blueCat

  // Load the character image
  val characterImage = new Image(getClass.getResourceAsStream(character.img.value))
  charImage.setImage(characterImage)




  // Get X position and image width
  val xPosition = charImage.layoutX.value
  val yPosition = charImage.layoutY.value
  val imageWidth = charImage.getImage.getWidth








}


