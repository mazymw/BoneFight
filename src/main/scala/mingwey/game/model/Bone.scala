package mingwey.game.model
import mingwey.game.view.GameController
import scalafx.beans.property.ObjectProperty

class Bone() {
  val damage: Int = 100
  var img: ObjectProperty[String] = ObjectProperty("/Image/bone.png")


  def isInRange(target: Character): Boolean = {


    true
  }
}