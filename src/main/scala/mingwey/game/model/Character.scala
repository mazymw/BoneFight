package mingwey.game.model

import scalafx.beans.property.{StringProperty, ObjectProperty}


case class charStat(hp: Int, atk: Int)

class Character(val nameS: String, val stats: charStat, val image: String) {
  var Name: StringProperty = new StringProperty(nameS)
  var stat: ObjectProperty[charStat] = ObjectProperty(stats)
  var img: ObjectProperty[String] = ObjectProperty(image)
  var bone  = new Bone()



  def takeDamage(amount: Int): Unit = {
    val updatedHP = math.max(0, stat.value.hp - amount)
    stat = ObjectProperty(stat.value.copy(hp = updatedHP))
  }

  def performAttack(target: Character, distance: Double): Unit = {

  }


}

object Character{
  def apply(nameS: String, stats: charStat, image: String) = new Character(nameS, stats, image)
  val blueCat = Character("Cat", charStat(100, 10), "/Image/cat.png")
  val bulldog = Character("dog", charStat(200, 20), "/Image/dog.png")
}

