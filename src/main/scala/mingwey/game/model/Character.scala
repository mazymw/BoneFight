package mingwey.game.model

import javafx.beans.property.SimpleDoubleProperty
import mingwey.game.MainApp.{computer, game}
import scalafx.beans.property.{DoubleProperty, IntegerProperty, ObjectProperty, StringProperty}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}


case class charStat(hp: Int, atk: Int)

class Character(val nameS: String, val stats: charStat, val image: String) {
  val Name: StringProperty = new StringProperty(nameS)
  val img: ObjectProperty[String] = ObjectProperty(image)
  val bone  = new Bone()
  var xCoordinate :(Double, Double) = (0,0)
  var yCoordinate :(Double, Double) = (0,0)
  var hp: DoubleProperty = DoubleProperty(stats.hp)
  var atk: Int = stats.atk
  val superpowers: ArrayBuffer[Superpower] = createDefaultSuperpowers()

  def isAlive: Boolean = hp.value > 0

  // Method to reset superpowers
  def resetSuperpowers(): Unit = {
    superpowers.clear()
    superpowers ++= createDefaultSuperpowers()
  }

  // To create the default set of superpowers
  private def createDefaultSuperpowers(): ArrayBuffer[Superpower] = {
    ArrayBuffer(
      new DamageBoostSuperpower(),
      new HealingSuperpower(this.stats.hp * 10 / 100),
      new AimSuperpower()
    )
  }

  def throwBone(target: Character, velocity: Double,direction : Double, wind : Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    var time: Double = 0
    val flightTime = bone.getFlightTime(velocity)
    val xCoordinates: ArrayBuffer[Double] = ArrayBuffer()
    val yCoordinates: ArrayBuffer[Double] = ArrayBuffer()


    breakable {
      while (time < flightTime) {
        val (simulatedXCoordinate, simulatedYCoordinate) = bone.simulateArc(velocity, time,direction : Double, wind)

        time += 0.1

        if(bone.checkIntersects(target, velocity, time,direction : Double, wind)){
          bone.isIntercept = true
          val interceptTime = time
          break()
        }
        xCoordinates.append(simulatedXCoordinate(0))
        yCoordinates.append(simulatedYCoordinate(0))
      }
    }

    (xCoordinates, yCoordinates)
  }


  def takeDamage(dmg : Int): Unit = {
    hp.value -= dmg
    if (hp.value < 0) hp.value = 0
  }

  def useSuperpower(index: Int): Unit = {
    if (index >= 0 && index < superpowers.length) {
      superpowers(index).activate(this)
    }
  }

  def deactivateSuperpower(index: Int): Unit = {
    if (index >= 0 && index < superpowers.length) {
      superpowers(index).deactivate(this)
    }
  }

}

object Character{
  def apply(nameS: String, stats: charStat, image: String) = new Character(nameS, stats, image)
  val blueCat = Character("Cat", charStat(100, 10), "/Image/cat.png")
  val bulldog = Character("dog", charStat(20, 20), "/Image/dog.png")
  val dinosaur = Character("dinosaur", charStat(50, 10), "/Image/dinosaur.png")
}

