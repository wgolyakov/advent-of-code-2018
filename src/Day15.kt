fun main() {
	data class Point(val x: Int, val y: Int) : Comparable<Point> {
		override fun compareTo(other: Point): Int {
			if (y != other.y) return y.compareTo(other.y)
			return x.compareTo(other.x)
		}

		fun neighbors() = listOf(Point(x, y - 1), Point(x - 1, y), Point(x + 1, y), Point(x, y + 1))
	}

	class Unit(var point: Point, val type: Char, var hit: Int = 200) : Comparable<Unit> {
		override fun compareTo(other: Unit): Int {
			return point.compareTo(other.point)
		}

		fun enemy() = if (type == 'E') 'G' else 'E'
		override fun toString() = "$type($hit)"
	}

	val attackComparator = Comparator<Unit> { o1, o2 ->
		if (o1.hit != o2.hit) o1.hit.compareTo(o2.hit) else o1.point.compareTo(o2.point)
	}

	fun parse(input: List<String>): Pair<MutableMap<Point, Unit>, List<StringBuilder>> {
		val units = mutableMapOf<Point, Unit>()
		val grid = mutableListOf<StringBuilder>()
		for ((y, row) in input.withIndex()) {
			grid.add(StringBuilder(row))
			for ((x, c) in row.withIndex()) {
				if (c == 'E' || c == 'G') units[Point(x, y)] = Unit(Point(x, y), c)
			}
		}
		return units to grid
	}

	fun bfsFindWay(unit: Unit, grid: List<StringBuilder>): Point? {
		val distances = grid.map { row -> IntArray(row.length) { -1 } }
		distances[unit.point.y][unit.point.x] = 0
		val queue = mutableListOf<Point>()
		queue.addAll(unit.point.neighbors())
		var distance = 1
		val nearestEnemies = mutableListOf<Point>()
		while (queue.isNotEmpty()) {
			val level = queue.toList()
			queue.clear()
			for (curr in level) {
				if (distances[curr.y][curr.x] != -1) continue
				val c = grid[curr.y][curr.x]
				if (c == unit.enemy()) nearestEnemies.add(curr)
				if (c == '.') {
					distances[curr.y][curr.x] = distance
					for (next in curr.neighbors()) {
						if (distances[next.y][next.x] != -1) continue
						val nc = grid[next.y][next.x]
						if (nc == '.' || nc == unit.enemy()) queue.add(next)
					}
				}
			}
			if (nearestEnemies.isNotEmpty()) break
			distance++
		}
		if (nearestEnemies.isEmpty()) return null
		val enemy = nearestEnemies.min()
		var point = enemy
		while (distance > 1) {
			distance--
			point = point.neighbors().filter { distances[it.y][it.x] == distance }.min()
		}
		return point
	}

	fun battle(input: List<String>, elfPower: Int): Pair<Int, Int> {
		val (units, grid) = parse(input)
		var elfCount = grid.sumOf { row -> row.count { it == 'E' } }
		var goblinCount = grid.sumOf { row -> row.count { it == 'G' } }
		val elfInitial = elfCount
		var turn = 0
		while (true) {
			for (unit in units.values.sorted()) {
				if (unit.hit <= 0) continue
				if (elfCount == 0 || goblinCount == 0) {
					val hits = units.values.sumOf { it.hit }
					return turn * hits to (elfInitial - elfCount)
				}
				val enemy = unit.enemy()
				var adjacent = unit.point.neighbors().filter { grid[it.y][it.x] == enemy }
				if (adjacent.isEmpty()) {
					// Move
					val target = bfsFindWay(unit, grid)
					if (target != null) {
						units.remove(unit.point)
						grid[unit.point.y][unit.point.x] = '.'
						unit.point = target
						units[target] = unit
						grid[target.y][target.x] = unit.type
						adjacent = target.neighbors().filter { grid[it.y][it.x] == enemy }
					}
				}
				if (adjacent.isNotEmpty()) {
					// Attack
					val targets = adjacent.map { units[it]!! }
					val target = targets.minWith(attackComparator)
					target.hit -= if (unit.type == 'E') elfPower else 3
					if (target.hit <= 0) {
						grid[target.point.y][target.point.x] = '.'
						units.remove(target.point)
						if (target.type == 'E') elfCount-- else goblinCount--
					}
				}
			}
			turn++
		}
	}

	fun part1(input: List<String>): Int {
		return battle(input, 3).first
	}

	fun part2(input: List<String>): Int {
		for (elfPower in 3 .. 1000) {
			val (outcome, elvesDie) = battle(input, elfPower)
			if (elvesDie == 0) return outcome
		}
		return -1
	}

	val testInput = readInput("Day15_test")
	val testInput2 = readInput("Day15_test2")
	val testInput3 = readInput("Day15_test3")
	val testInput4 = readInput("Day15_test4")
	val testInput5 = readInput("Day15_test5")
	val testInput6 = readInput("Day15_test6")
	check(part1(testInput) == 27730)
	check(part1(testInput2) == 36334)
	check(part1(testInput3) == 39514)
	check(part1(testInput4) == 27755)
	check(part1(testInput5) == 28944)
	check(part1(testInput6) == 18740)
	check(part2(testInput) == 4988)
	check(part2(testInput3) == 31284)
	check(part2(testInput4) == 3478)
	check(part2(testInput5) == 6474)
	check(part2(testInput6) == 1140)

	val input = readInput("Day15")
	part1(input).println()
	part2(input).println()
}
