package mingwey.game.model
import mingwey.game.view.GameController

class Bone() {
  val damage: Int = 100
  var range: Double = 0

  def throwBone(target: Character ,_range: Double): Unit = {
    range = range
    if (isInRange(target)) {
      target.takeDamage(damage)
      println(s"attacks ${target.Name.value} for $damage damage.")
    } else {
      println(s"${target.Name.value} is out of range fors attack.")
    }
  }

  def isInRange(target: Character): Boolean = {
    if (range == xPosition )

    true
  }
}