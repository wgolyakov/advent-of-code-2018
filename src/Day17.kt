fun main() {
	fun findMinMax(input: List<String>): List<Int> {
		var minX = Int.MAX_VALUE
		var minY = Int.MAX_VALUE
		var maxX = Int.MIN_VALUE
		var maxY = Int.MIN_VALUE
		for (line in input) {
			val (part1, part2) = line.split(", ")
			if (line[0] == 'x') {
				val x = part1.substringAfter("x=").toInt()
				val (y1, y2) = part2.substringAfter("y=").split("..").map { it.toInt() }
				if (x < minX) minX = x
				if (x > maxX) maxX = x
				if (y1 < minY) minY = y1
				if (y2 > maxY) maxY = y2
			} else {
				val y = part1.substringAfter("y=").toInt()
				val (x1, x2) = part2.substringAfter("x=").split("..").map { it.toInt() }
				if (y < minY) minY = y
				if (y > maxY) maxY = y
				if (x1 < minX) minX = x1
				if (x2 > maxX) maxX = x2
			}
		}
		return listOf(minX - 2, minY, maxX + 2, maxY)
	}

	fun parse(input: List<String>, minX: Int, minY: Int, maxX: Int, maxY: Int): Array<StringBuilder> {
		val width = maxX - minX + 1
		val height = maxY - minY + 1
		val grid = Array(height) { StringBuilder(".".repeat(width)) }
		for (line in input) {
			val (part1, part2) = line.split(", ")
			if (line[0] == 'x') {
				val x = part1.substringAfter("x=").toInt()
				val (y1, y2) = part2.substringAfter("y=").split("..").map { it.toInt() }
				for (y in y1..y2) grid[y - minY][x - minX] = '#'
			} else {
				val y = part1.substringAfter("y=").toInt()
				val (x1, x2) = part2.substringAfter("x=").split("..").map { it.toInt() }
				for (x in x1..x2) grid[y - minY][x - minX] = '#'
			}
		}
		return grid
	}

	class WaterFiller(val grid: Array<StringBuilder>) {
		fun fillDown(x: Int, y0: Int) {
			var y = y0
			while (y < grid.size) {
				if (grid[y][x] == '#') {
					fillUp(x, y - 1, y0)
					break
				}
				if (grid[y][x] == '~') {
					fillUp(x, y, y0)
					break
				}
				grid[y][x] = '~'
				y++
			}
		}

		fun fillUp(x: Int, y0: Int, y1: Int) {
			var y = y0
			while (y >= y1) {
				val wallLeft = fillLeft(x - 1, y)
				val wallRight = fillRight(x + 1, y)
				if (!wallLeft || !wallRight) break
				y--
			}
		}

		fun fillLeft(x0: Int, y: Int): Boolean {
			var x = x0
			while (x >= 0) {
				if (grid[y][x] == '#') return true
				if (grid[y + 1][x] == '.') {
					if (grid[y + 1][x + 1] != '#') break
					fillDown(x, y)
					if (grid[y][x - 1] == '.') break
				}
				grid[y][x] = '~'
				x--
			}
			return false
		}

		fun fillRight(x0: Int, y: Int): Boolean {
			var x = x0
			while (x < grid[y].length) {
				if (grid[y][x] == '#') return true
				if (grid[y + 1][x] == '.') {
					if (grid[y + 1][x - 1] != '#') break
					fillDown(x, y)
					if (grid[y][x + 1] == '.') break
				}
				grid[y][x] = '~'
				x++
			}
			return false
		}
	}

	class WaterDrainer(val grid: Array<StringBuilder>) {
		fun drain() {
			val y = grid.size - 1
			for ((x, c) in grid[y].withIndex()) {
				if (c == '~') drainUp(x, y)
			}
		}

		fun drainUp(x: Int, y0: Int) {
			var y = y0
			while (y >= 0) {
				if (grid[y][x] != '~') break
				grid[y][x] = '.'
				if (grid[y][x - 1] == '~') drainLeft(x - 1, y)
				if (grid[y][x + 1] == '~') drainRight(x + 1, y)
				y--
			}
		}

		fun drainLeft(x0: Int, y: Int) {
			var x = x0
			while (x >= 0) {
				if (grid[y][x] != '~') break
				grid[y][x] = '.'
				if (grid[y - 1][x] == '~') drainUp(x, y - 1)
				x--
			}
		}

		fun drainRight(x0: Int, y: Int) {
			var x = x0
			while (x < grid[y].length) {
				if (grid[y][x] != '~') break
				grid[y][x] = '.'
				if (grid[y - 1][x] == '~') drainUp(x, y - 1)
				x++
			}
		}
	}

	fun part1(input: List<String>): Int {
		val (minX, minY, maxX, maxY) = findMinMax(input)
		val grid = parse(input, minX, minY, maxX, maxY)
		WaterFiller(grid).fillDown(500 - minX, 0)
		//println(grid.joinToString("\n"))
		return grid.sumOf { row -> row.count { it == '~' } }
	}

	fun part2(input: List<String>): Int {
		val (minX, minY, maxX, maxY) = findMinMax(input)
		val grid = parse(input, minX, minY, maxX, maxY)
		WaterFiller(grid).fillDown(500 - minX, 0)
		WaterDrainer(grid).drain()
		//println(grid.joinToString("\n"))
		return grid.sumOf { row -> row.count { it == '~' } }
	}

	val testInput = readInput("Day17_test")
	check(part1(testInput) == 57)
	check(part2(testInput) == 29)

	val input = readInput("Day17")
	part1(input).println()
	part2(input).println()
}
