package mingwey.game.model

// Abstract class representing a Superpower that characters can use
abstract class Superpower {
  // Flag indicating if the superpower is active
  var isActive = false

  // Method to activate the superpower
  def activate(character: Character): Unit

  // Method to deactivate the superpower
  def deactivate(character: Character): Unit

  // Number of times the superpower can be used
  var number = 1
}

// Superpower that temporarily doubles the character's attack
class DamageBoostSuperpower extends Superpower {

  def activate(character: Character): Unit = {
    if (!isActive && number > 0) {
      character.atk *= 2 // Double the attack value
      isActive = true
      number = number - 1 // Decrement the usage count
    }
  }

  def deactivate(character: Character): Unit = {
    if (isActive) {
      character.atk /= 2 // Reset the attack value to its original state
      isActive = false
    }
  }
}

// Superpower that heals the character by a specified amount
class HealingSuperpower(healAmount: Int) extends Superpower {

  def activate(character: Character): Unit = {
    if (!isActive && number > 0) {
      character.hp.value += healAmount // Increase the character's HP by the heal amount
      if (character.hp.value > character.stats.hp) { // Ensure HP doesn't exceed the max
        character.hp.value = character.stats.hp
      }
      number = number - 1 // Decrement the usage count
      isActive = true
    }
  }

  def deactivate(character: Character): Unit = {
    if (isActive) {
      isActive = false
    }
  }
}

// Superpower that guarantees bone hit
class AimSuperpower() extends Superpower {

  def activate(character: Character): Unit = {
    if (!isActive && number > 0) {
      number = number - 1 // Decrement the usage count
      isActive = true
    }
  }

  def deactivate(character: Character): Unit = {
    if (isActive) {
      isActive = false // Mark the superpower as inactive
    }
  }
}
