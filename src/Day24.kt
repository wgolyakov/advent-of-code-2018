@Suppress("EnumEntryName")
private enum class AttackType {
	bludgeoning,
	slashing,
	fire,
	cold,
	radiation
}

private enum class ArmyType {
	ImmuneSystem,
	Infection
}

fun main() {
	class Group(var units: Int, val hitPoints: Int, val attackDamage: Int,
				val attackType: AttackType,	val initiative: Int, val weaknesses: List<AttackType>,
				val immunities: List<AttackType>, val armyType: ArmyType) {

		fun effectivePower() = units * attackDamage

		fun damageBy(group: Group): Int {
			if (group.attackType in weaknesses) return group.effectivePower() * 2
			if (group.attackType in immunities) return 0
			return group.effectivePower()
		}

		fun copy(boost: Int) = Group(units, hitPoints, attackDamage + boost,
			attackType, initiative, weaknesses, immunities, armyType)

		override fun toString() = "Group(units=$units)"
	}

	class Army(val groups: MutableList<Group> = mutableListOf()) {
		fun units() = groups.sumOf { it.units }
		fun copy(boost: Int) = Army(groups.map { it.copy(boost) }.toMutableList())
	}

	fun parse(input: List<String>): List<Army> {
		val armies = mutableListOf<Army>()
		var army = Army()
		var armyType = ArmyType.ImmuneSystem
		for (line in input) {
			if (line == "Immune System:" || line.isEmpty()) continue
			if (line == "Infection:") {
				armies.add(army)
				army = Army()
				armyType = ArmyType.Infection
				continue
			}
			val list1 = line.substringBefore(" hit points").split(' ')
			val units = list1.first().toInt()
			val hitPoints = list1.last().toInt()
			val weaknesses = mutableListOf<AttackType>()
			val immunities = mutableListOf<AttackType>()
			if ('(' in line) {
				val list2 = line.substringAfter('(').substringBefore(')').split("; ")
				for (s in list2) {
					val types = s.substringAfter("to ").split(", ").map { AttackType.valueOf(it) }
					if (s.startsWith("weak")) {
						weaknesses.addAll(types)
					} else if (s.startsWith("immune")) {
						immunities.addAll(types)
					}
				}
			}
			val list3 = line.substringAfter("with an attack that does ").split(' ')
			val attackDamage = list3.first().toInt()
			val attackType = AttackType.valueOf(list3[1])
			val initiative = list3.last().toInt()
			val group = Group(units, hitPoints, attackDamage, attackType, initiative, weaknesses, immunities, armyType)
			army.groups.add(group)
		}
		armies.add(army)
		return armies
	}

	fun fight(immuneSystem: Army, infection: Army) {
		// Target selection
		val immuneGroups = immuneSystem.groups.toMutableList()
		val infectionGroups = infection.groups.toMutableList()
		val allGroups = immuneSystem.groups + infection.groups
		val orderedGroups = allGroups.sortedWith(compareBy({ -it.effectivePower() }, { -it.initiative }))
		val chooses = mutableListOf<Pair<Group, Group?>>()
		for (group in orderedGroups) {
			val enemyGroups = if (group.armyType == ArmyType.Infection) immuneGroups else infectionGroups
			val target = enemyGroups.sortedWith(
				compareBy({ -it.damageBy(group) }, { -it.effectivePower() }, { -it.initiative })).firstOrNull()
			if (target == null || target.damageBy(group) == 0) {
				chooses.add(group to null)
			} else {
				chooses.add(group to target)
				enemyGroups.remove(target)
			}
		}
		// Attacking
		for ((attacking, defending) in chooses.sortedByDescending { (a, _) -> a.initiative }) {
			if (attacking.units <= 0) continue
			if (defending == null) continue
			defending.units -= defending.damageBy(attacking) / defending.hitPoints
			if (defending.units <= 0) {
				val army = if (defending.armyType == ArmyType.Infection) infection else immuneSystem
				army.groups.remove(defending)
			}
		}
	}

	fun combat(immuneSystem: Army, infection: Army) {
		while (immuneSystem.groups.isNotEmpty() && infection.groups.isNotEmpty()) {
			fight(immuneSystem, infection)
			// Optimization
			if (immuneSystem.groups.size == 1 && infection.groups.size == 1) {
				val immuneGroup = immuneSystem.groups.single()
				val infectionGroup = infection.groups.single()
				if (infectionGroup.attackType in immuneGroup.immunities) {
					infection.groups.clear()
				} else if (immuneGroup.attackType in infectionGroup.immunities) {
					immuneSystem.groups.clear()
				}
			}
		}
	}

	fun part1(input: List<String>): Int {
		val (immuneSystem, infection) = parse(input)
		combat(immuneSystem, infection)
		return immuneSystem.units() + infection.units()
	}

	fun part2(input: List<String>): Int {
		val (immuneSystem0, infection0) = parse(input)
		for (boost in 0 until 2000) {
			val immuneSystem = immuneSystem0.copy(boost)
			val infection = infection0.copy(0)
			combat(immuneSystem, infection)
			if (immuneSystem.groups.size > 0) return immuneSystem.units()
		}
		return 0
	}

	val testInput = readInput("Day24_test")
	check(part1(testInput) == 5216)
	check(part2(testInput) == 51)

	val input = readInput("Day24")
	part1(input).println()
	part2(input).println()
}
