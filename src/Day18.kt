fun main() {
	fun countAdjacentAcres(grid: List<String>, x: Int, y: Int): Pair<Int, Int> {
		var tree = 0
		var lumberyard = 0
		for (a in x - 1 .. x + 1) {
			for (b in y - 1 .. y + 1) {
				if (a == x && b == y) continue
				if (a < 0 || b < 0 || b >= grid.size || a >= grid[b].length) continue
				val c = grid[b][a]
				if (c == '|') tree++ else if (c == '#') lumberyard++
			}
		}
		return tree to lumberyard
	}

	fun changeLandscape(grid: List<String>): List<String> {
		val result = List(grid.size) { StringBuilder(grid[it]) }
		for ((y, row) in grid.withIndex()) {
			for ((x, c) in row.withIndex()) {
				val (t, l) = countAdjacentAcres(grid, x, y)
				when (c) {
					'.' -> if (t >= 3) result[y][x] = '|'
					'|' -> if (l >= 3) result[y][x] = '#'
					'#' -> if (l == 0 || t == 0) result[y][x] = '.'
				}
			}
		}
		return result.map { it.toString() }
	}

	fun resourceValue(grid: List<String>): Int {
		val woods = grid.sumOf { row -> row.count { it == '|' } }
		val lumberyards = grid.sumOf { row -> row.count { it == '#' } }
		return woods * lumberyards
	}

	fun part1(input: List<String>): Int {
		var grid = input
		for (time in 1..10) {
			grid = changeLandscape(grid)
			//println(grid.joinToString("\n") + "\n")
		}
		return resourceValue(grid)
	}

	fun part2(input: List<String>): Int {
		var grid = input
		val cache = mutableListOf(grid)
		for (time in 1..1000) {
			grid = changeLandscape(grid)
			if (grid in cache) {
				val t0 = cache.indexOf(grid)
				val period = time - t0
				val r = (1000000000 - time) % period
				for (t in 1..r) {
					grid = changeLandscape(grid)
				}
				return resourceValue(grid)
			}
			cache.add(grid)
		}
		return -1
	}

	val testInput = readInput("Day18_test")
	check(part1(testInput) == 1147)
	check(part2(testInput) == 0)

	val input = readInput("Day18")
	part1(input).println()
	part2(input).println()
}
