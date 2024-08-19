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

  def throwBone(target: Character, velocity: Double, direction : Double, wind : Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    var time: Double = 0
    val flightTime = bone.getFlightTime(velocity)
    val xCoordinates: ArrayBuffer[Double] = ArrayBuffer()
    val yCoordinates: ArrayBuffer[Double] = ArrayBuffer()
    bone.isIntercept = false


    breakable {
      while (time < flightTime) {
        val (simulatedXCoordinate, simulatedYCoordinate) = bone.simulateArc(velocity, time, direction : Double, wind)

        time += 0.1

        if(bone.checkIntersects(target, velocity, time, direction : Double, wind)){
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
  val bulldog = Character("Dog", charStat(20, 20), "/Image/dog.png")
  val cat = Character("Cat", charStat(100, 10), "/Image/cat.png")
  val buffalo = Character("Buffalo",charStat(200, 40), "/Image/buffalo.png")
  val rat = Character("Rat",charStat(70, 50), "/Image/rat.png")
  val dinosaur = Character("Dinosaur", charStat(50, 10), "/Image/dinosaur.png")
  val gorilla = Character("Gorilla", charStat(50, 10), "/Image/gorilla.png")
  val hyena = Character("Gorilla", charStat(50, 10), "/Image/hyena.png")

  val allCharacters: List[Character] = List(cat, buffalo, rat, dinosaur, gorilla, hyena)

}

