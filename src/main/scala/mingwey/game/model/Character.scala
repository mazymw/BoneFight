package mingwey.game.model

import scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}


case class charStat(hp: Int, atk: Int)

class Character(val nameS: String, val stats: charStat, val image: String) {
  val Name: StringProperty = new StringProperty(nameS)
  val img: ObjectProperty[String] = ObjectProperty(image)
  val bone  = new Bone()
  var xCoordinate :(Double, Double) = (0,0)
  var yCoordinate :(Double, Double) = (0,0)
  val hp: IntegerProperty = IntegerProperty(stats.hp)

  def isAlive: Boolean = hp.value > 0

  def throwBone(target: Character, velocity: Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    var time: Double = 0
    val flightTime = bone.getFlightTime(velocity)
    val xCoordinates: ArrayBuffer[Double] = ArrayBuffer()
    val yCoordinates: ArrayBuffer[Double] = ArrayBuffer()

    breakable {
      while (time < flightTime) {
        val (simulatedXCoordinate, simulatedYCoordinate) = bone.simulateArc(velocity, time)

        time += 0.1

        if(bone.checkIntersects(target, velocity, time)){
          bone.isIntercept = true
          break()
        }
        xCoordinates.append(simulatedXCoordinate(0))
        yCoordinates.append(simulatedYCoordinate(0))
      }
    }
    (xCoordinates, yCoordinates)
  }


  def takeDamage(multiplier: Double): Unit = {
    hp.value -= stats.atk * multiplier
    if (hp.value < 0) hp.value = 0
  }



}

object Character{
  def apply(nameS: String, stats: charStat, image: String) = new Character(nameS, stats, image)
  val blueCat = Character("Cat", charStat(100, 10), "/Image/cat.png")
  val bulldog = Character("dog", charStat(200, 20), "/Image/dog.png")
  val dinosaur = Character("dinosaur", charStat(200, 20), "/Image/dinosaur.png")
}

