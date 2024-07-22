
package mingwey.game.view

import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.AnchorPane
import scalafx.scene.shape.Circle
import scalafxml.core.macros.sfxml

import java.time.Instant


@sfxml
class GameController( private val circle: Circle,
                    ){
  var clicked = true

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




}


