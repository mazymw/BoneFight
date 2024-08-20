package mingwey.game.model

// Abstract class representing a Superpower that characters can use
abstract class Superpower {
  // Flag indicating if the superpower is active
  var isActive = false

  // Number of times the superpower can be used
  var number = 1

  // Common logic for activating a superpower
  def activate(character: Character): Unit = {
    if (!isActive && number > 0) {
      number -= 1 // Decrement the usage count
      isActive = true
    }
  }

  // Method to deactivate the superpower
  def deactivate(character: Character): Unit = {
    if (isActive) {
      isActive = false
    }
  }
}

// Superpower that temporarily doubles the character's attack
class DamageBoostSuperpower extends Superpower {

  override def activate(character: Character): Unit = {
    super.activate(character)
    if (isActive) {
      character.atk *= 2 // Double the attack value
    }
  }

  override def deactivate(character: Character): Unit = {
    if (isActive) {
      character.atk /= 2 // Reset the attack value to its original state
      super.deactivate(character)
    }
  }
}

// Superpower that heals the character by a specified amount
class HealingSuperpower(healAmount: Int) extends Superpower {

  override def activate(character: Character): Unit = {
    super.activate(character)
    if (isActive) {
      character.hp.value += healAmount // Increase the character's HP by the heal amount
      if (character.hp.value > character.stats.hp) { // Ensure HP doesn't exceed the max
        character.hp.value = character.stats.hp
      }
    }
  }

}

// Superpower that guarantees a bone hit
class AimSuperpower extends Superpower