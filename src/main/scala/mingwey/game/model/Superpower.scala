package mingwey.game.model



abstract class Superpower {
  var isActive = false
  def apply(character: Character): Unit
  def activate(character: Character): Unit
  def deactivate(character: Character): Unit
  val number = 1
}

class DamageBoostSuperpower extends Superpower {
  def apply(character: Character): Unit ={

  }

  def activate(character: Character): Unit = {
    if (!isActive && number > 0) {
      println("Use Superpower")
      character.atk *= 2
      isActive = true
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

  def apply(character: Character): Unit = {
    activate(character)
    character.hp.value += healAmount
    if (character.hp.value > character.stats.hp) {
      character.hp.value = character.stats.hp // Cap the HP to maximum
    }
    deactivate(character)
  }

  override def activate(character: Character): Unit = {
    if (!isActive) {
      isActive = true
    }
  }

  override def deactivate(character: Character): Unit = {
    if (isActive) {
      isActive = false
    }
  }
}
