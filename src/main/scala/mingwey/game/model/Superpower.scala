package mingwey.game.model



abstract class Superpower {
  var isActive = false
  def activate(character: Character): Unit
  def deactivate(character: Character): Unit
  var number = 1
}

class DamageBoostSuperpower extends Superpower {

  def activate(character: Character): Unit = {
    if (!isActive && number > 0) {
      println("Use Superpower")
      character.atk *= 2
      isActive = true
      number = number - 1
    }
  }

  def deactivate(character: Character): Unit = {
    if (isActive) {
      character.atk /= 2
      isActive = false
    }
  }
}

class HealingSuperpower(healAmount: Int) extends Superpower {

  def activate(character: Character): Unit = {
    if (!isActive && number > 0) {
      println("heal effect applied")
      character.hp.value += healAmount
      if (character.hp.value > character.stats.hp) {
        character.hp.value = character.stats.hp // Cap the HP to maximum
        number = number - 1
      }
      isActive = true
    }
  }

  def deactivate(character: Character): Unit = {
    if (isActive) {
      isActive = false
    }
  }
}
