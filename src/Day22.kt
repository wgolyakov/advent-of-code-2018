private const val TORCH = 0
private const val CLIMBING_GEAR = 1
private const val NEITHER = 2

fun main() {
	fun char(type: Int) = when (type) {
		0 -> '.'
		1 -> '='
		2 -> '|'
		else -> error("Wrong type: $type")
	}

	fun risk(c: Char) = when (c) {
		'.' -> 0
		'=' -> 1
		'|' -> 2
		else -> error("Wrong char: $c")
	}

	fun makeGrid(depth: Int, xt: Int, yt: Int, xMax: Int, yMax: Int): Array<StringBuilder> {
		val levels = Array(yMax + 1) { IntArray(xMax + 1) }
		val grid = Array(yMax + 1) { StringBuilder(" ".repeat(xMax + 1)) }
		grid[0][0] = char((depth % 20183) % 3)
		for (x in 1 .. xMax) {
			val geologicIndex = x * 16807
			val erosionLevel = (geologicIndex + depth) % 20183
			levels[0][x] = erosionLevel
			grid[0][x] = char(erosionLevel % 3)
		}
		for (y in 1 .. yMax) {
			val geologicIndex = y * 48271
			val erosionLevel = (geologicIndex + depth) % 20183
			levels[y][0] = erosionLevel
			grid[y][0] = char(erosionLevel % 3)
		}
		for (y in 1 .. yMax) {
			for (x in 1 .. xMax) {
				if (x == xt && y == yt) {
					grid[y][x] = char((depth % 20183) % 3)
					continue
				}
				val geologicIndex = levels[y][x - 1] * levels[y - 1][x]
				val erosionLevel = (geologicIndex + depth) % 20183
				levels[y][x] = erosionLevel
				grid[y][x] = char(erosionLevel % 3)
			}
		}
		return grid
	}

	fun toolSuitableForType(tool: Int, type: Char) = when (type) {
		'.' -> tool == TORCH || tool == CLIMBING_GEAR
		'=' -> tool == CLIMBING_GEAR || tool == NEITHER
		'|' -> tool == TORCH || tool == NEITHER
		else -> error("Wrong type: $type")
	}

	fun part1(input: List<String>): Int {
		val depth = input[0].substringAfter("depth: ").toInt()
		val (xt, yt) = input[1].substringAfter("target: ").split(',').map { it.toInt() }
		val grid = makeGrid(depth, xt, yt, xt, yt)
		//println(grid.joinToString("\n") + "\n")
		return grid.sumOf { row -> row.sumOf { risk(it) } }
	}

	// 1-K BFS with cutting tool changing into 7 one-minute intervals
	fun part2(input: List<String>): Int {
		val depth = input[0].substringAfter("depth: ").toInt()
		val (xt, yt) = input[1].substringAfter("target: ").split(',').map { it.toInt() }
		val grid = makeGrid(depth, xt, yt, xt * 60, yt * 3)
		// 1-K BFS
		val times = Array(yt * 3) { Array(xt * 60) { IntArray(3) { -1 } } }
		times[0][0][TORCH] = 0
		val queue = mutableListOf((0 to 0) to (TORCH to 0))
		while (queue.isNotEmpty()) {
			val (point, toolData) = queue.removeFirst()
			val (x, y) = point
			val (tool, toolChangeCountdown) = toolData
			if (x == xt && y == yt && tool == TORCH && toolChangeCountdown == 0) break
			val time = times[y][x][tool]
			val type = grid[y][x]
			if (toolChangeCountdown == 0) {
				// Move
				for ((dx, dy) in listOf(0 to -1, 0 to 1, -1 to 0, 1 to 0)) {
					val nx = x + dx
					val ny = y + dy
					if (nx < 0 || ny < 0) continue
					val nType = grid[ny][nx]
					if (!toolSuitableForType(tool, nType)) continue
					if (times[ny][nx][tool] != -1) continue
					queue.add((nx to ny) to (tool to 0))
					times[ny][nx][tool] = time + 1
				}
				// Change tool
				for (nTool in 0..2) {
					if (nTool == tool) continue
					if (!toolSuitableForType(nTool, type)) continue
					if (times[y][x][nTool] != -1) continue
					queue.add((x to y) to (nTool to 6))
				}
			} else {
				if (times[y][x][tool] != -1) continue
				queue.add((x to y) to (tool to toolChangeCountdown - 1))
				if (toolChangeCountdown == 1) {
					val pTime = times[y][x].first { it != -1 }
					times[y][x][tool] = pTime + 7
				}
			}
		}
		return times[yt][xt][TORCH]
	}

	val testInput = readInput("Day22_test")
	check(part1(testInput) == 114)
	check(part2(testInput) == 45)

	val input = readInput("Day22")
	part1(input).println()
	part2(input).println()
}
