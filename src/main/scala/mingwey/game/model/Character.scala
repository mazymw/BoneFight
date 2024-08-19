package mingwey.game.model

import scalafx.beans.property.{DoubleProperty, ObjectProperty, StringProperty}
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}


case class charStat(hp: Int, atk: Int)

class Character(val nameS: String, val stats: charStat, val image: String) {
  val name: String = nameS
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
  def createDefaultSuperpowers(): ArrayBuffer[Superpower] = {
    ArrayBuffer(
      new DamageBoostSuperpower(),
      new HealingSuperpower(this.stats.hp * 10 / 100),
      new AimSuperpower()
    )
  }

  def throwBone(target: Character, velocity: Double, direction : Double, wind : Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    var time: Double = 0
    val flightTime = bone.getFlightTime(velocity)
    val xCoordinates: ArrayBuffer[Double] = ArrayBuffer()
    val yCoordinates: ArrayBuffer[Double] = ArrayBuffer()
    bone.isIntercept = false

    breakable {
      while (time < flightTime) {
        val (simulatedXCoordinate, simulatedYCoordinate) = bone.simulateArc(velocity, time, direction, wind)

        time += 0.1

        if(bone.checkIntersects(target, simulatedXCoordinate, simulatedYCoordinate)){
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


  def takeDamage(dmg : Double): Unit = {
    hp.value -= Math.min(dmg, hp.value)
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
  val bulldog: Character = Character("Dog", charStat(70, 10), "/image/dog.png")
  val cat: Character = Character("Cat", charStat(50, 20), "/image/cat.png")
  val buffalo :Character = Character("Buffalo",charStat(100, 5), "/image/buffalo.png")
  val rat :Character = Character("Rat",charStat(40, 25), "/image/rat.png")
  val dinosaur :Character = Character("Dinosaur", charStat(70, 20), "/image/dinosaur.png")
  val gorilla :Character = Character("Gorilla", charStat(80, 10), "/image/gorilla.png")
  val hyena :Character = Character("Hyena", charStat(35, 30), "/image/hyena.png")

  val allCharacters: List[Character] = List(cat, buffalo, rat, dinosaur, gorilla, hyena)

}

