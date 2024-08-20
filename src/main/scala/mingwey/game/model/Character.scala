package mingwey.game.model

import scalafx.beans.property.{DoubleProperty, ObjectProperty, StringProperty}
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

// Case class to define character stats like HP and Attack
case class charStat(hp: Int, atk: Int)

// Main class representing a Character in the game
class Character(val nameS: String, val stats: charStat, val image: String) {

  // Properties of the character
  val name: String = nameS
  val img: ObjectProperty[String] = ObjectProperty(image) // Image associated with the character
  val bone  = new Bone() // The bone object associated with the character, used for attacking
  var xCoordinate :(Double, Double) = (0,0) // X coordinates of the character (current and previous)
  var yCoordinate :(Double, Double) = (0,0) // Y coordinates of the character (current and previous)
  var hp: DoubleProperty = DoubleProperty(stats.hp) // Health points of the character
  var atk: Int = stats.atk // Attack value of the character
  val superpowers: ArrayBuffer[Superpower] = createDefaultSuperpowers() // List of superpowers available to the character

  // Check if the character is still alive
  def isAlive: Boolean = hp.value > 0

  //Reset the character's superpowers to the default set
  def resetSuperpowers(): Unit = {
    superpowers.clear()
    superpowers ++= createDefaultSuperpowers()
  }

  // Create the default set of superpowers
  def createDefaultSuperpowers(): ArrayBuffer[Superpower] = {
    ArrayBuffer(
      new DamageBoostSuperpower(), // Superpower that boosts damage
      new HealingSuperpower(this.stats.hp * 10 / 100), // Superpower that heals 10% of the character's HP
      new AimSuperpower() // Superpower that improves aim
    )
  }

  // Method for throwing a bone towards a target, calculating its trajectory
  def throwBone(target: Character, velocity: Double, direction : Double, wind : Double): (ArrayBuffer[Double], ArrayBuffer[Double]) = {
    var time: Double = 0
    val flightTime = bone.getFlightTime(velocity) // Calculate the flight time of the bone
    val xCoordinates: ArrayBuffer[Double] = ArrayBuffer() // Store X coordinates of the bone during its flight
    val yCoordinates: ArrayBuffer[Double] = ArrayBuffer() // Store Y coordinates of the bone during its flight
    bone.isIntercept = false // Initially, no interception

    // Loop to simulate the bone's flight trajectory
    breakable {
      while (time < flightTime) {
        val (simulatedXCoordinate, simulatedYCoordinate) = bone.simulateArc(velocity, time, direction, wind)

        time += 0.1

        // Check if the bone intersects with the target
        if(bone.checkIntersects(target, simulatedXCoordinate, simulatedYCoordinate)){
          bone.isIntercept = true
          break() // Stop the loop if an intersection occurs
        }

        // Store the coordinates of the bone's flight path
        xCoordinates.append(simulatedXCoordinate._1)
        yCoordinates.append(simulatedYCoordinate._1)
      }
    }
    (xCoordinates, yCoordinates) // Return the calculated flight path coordinates
  }

  // Reduce the character's HP by a specified damage value
  def takeDamage(dmg: Double): Unit = {
    hp.value -= Math.min(dmg, hp.value) // Subtract damage from HP, but don't go below 0
    if (hp.value < 0) hp.value = 0 // Ensure HP doesn't become negative
  }

  // Use a superpower based on its index in the list
  def useSuperpower(index: Int): Unit = {
    if (index >= 0 && index < superpowers.length) {
      superpowers(index).activate(this) // Activate the superpower for this character
    }
  }

  // Deactivate a superpower based on its index
  def deactivateSuperpower(index: Int): Unit = {
    if (index >= 0 && index < superpowers.length) {
      superpowers(index).deactivate(this) // Deactivate the superpower for this character
    }
  }

}

// Companion object for Character, providing predefined characters
object Character {
  def apply(nameS: String, stats: charStat, image: String) = new Character(nameS, stats, image)

  // Predefined characters with their stats and associated images
  val bulldog: Character = Character("Dog", charStat(70, 10), "/image/dog.png")
  val cat: Character = Character("Cat", charStat(50, 20), "/image/cat.png")
  val buffalo: Character = Character("Buffalo", charStat(100, 5), "/image/buffalo.png")
  val rat: Character = Character("Rat", charStat(40, 25), "/image/rat.png")
  val dinosaur: Character = Character("Dinosaur", charStat(70, 15), "/image/dinosaur.png")
  val gorilla: Character = Character("Gorilla", charStat(80, 10), "/image/gorilla.png")
  val hyena: Character = Character("Hyena", charStat(35, 30), "/image/hyena.png")

  // List of all available characters
  val allCharacters: List[Character] = List(cat, buffalo, rat, dinosaur, gorilla, hyena)
}
