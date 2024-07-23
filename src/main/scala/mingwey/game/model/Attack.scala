package mingwey.game.model

class Attack(val damage: Int, val range: Double) {
  def performAttack(attacker: Character, target: Character): Unit = {
    if (isInRange(attacker, target)) {
      target.takeDamage(damage)
      println(s"${attacker.Name.value} attacks ${target.Name.value} for $damage damage.")
    } else {
      println(s"${target.Name.value} is out of range for ${attacker.Name.value}'s attack.")
    }
  }

  private def isInRange(attacker: Character, target: Character): Boolean = {
    // Example range check logic
    true // Simplified for example purposes
  }
}