fun main() {
	class Mapper(val regex: String) {
		var x0 = 0
		var y0 = 0
		var widths = 1
		var height = 1
		val grid = mutableListOf(
			StringBuilder("###"),
			StringBuilder("#X#"),
			StringBuilder("###")
		)

		fun cx(x: Int) = (x0 + x) * 2 + 1
		fun cy(y: Int) = (y0 + y) * 2 + 1

		fun ensureGridSize(x: Int, y: Int) {
			val sx = x0 + x
			val sy = y0 + y
			if (sx < 0) {
				x0 += 1
				widths++
				for (b in 0 until height) {
					grid[b * 2].insert(0, "##")
					grid[b * 2 + 1].insert(0, "#.")
				}
				grid[grid.size - 1].insert(0, "##")
			} else if (sx >= widths) {
				for (b in 0 until height) {
					grid[b * 2].insert(widths * 2 + 1, "##")
					grid[b * 2 + 1].insert(widths * 2 + 1, ".#")
				}
				grid[grid.size - 1].insert(widths * 2 + 1, "##")
				widths++
			} else if (sy < 0) {
				y0 += 1
				height++
				grid.add(0, StringBuilder("##".repeat(widths)).append('#'))
				grid.add(1, StringBuilder("#.".repeat(widths)).append('#'))
			} else if (sy >= height) {
				grid.add(height * 2 + 1, StringBuilder("#.".repeat(widths)).append('#'))
				grid.add(height * 2 + 2, StringBuilder("##".repeat(widths)).append('#'))
				height++
			}
		}

		fun mapRecurs(startIndex: Int, startX: Int, startY: Int): Pair<Int, Set<Pair<Int, Int>>> {
			var i = startIndex
			var x = startX
			var y = startY
			val branches = mutableSetOf<Pair<Int, Int>>()
			while (true) {
				val c = regex[i]
				when (c) {
					'^' -> {}
					'N' -> {
						grid[cy(y) - 1][cx(x)] = '-'
						y--
						ensureGridSize(x, y)
					}
					'S' -> {
						grid[cy(y) + 1][cx(x)] = '-'
						y++
						ensureGridSize(x, y)
					}
					'E' -> {
						grid[cy(y)][cx(x) + 1] = '|'
						x++
						ensureGridSize(x, y)
					}
					'W' -> {
						grid[cy(y)][cx(x) - 1] = '|'
						x--
						ensureGridSize(x, y)
					}
					'(' -> {
						val (currI, branchPoints) = mapRecurs(i + 1, x, y)
						i = currI
						if (branchPoints.size == 1) {
							val p = branchPoints.single()
							x = p.first
							y = p.second
						} else {
							branches.addAll(branchPoints)
						}
					}
					'|' -> {
						branches.add(x to y)
						x = startX
						y = startY
					}
					')' -> {
						branches.add(x to y)
						return i to branches
					}
					'$' -> return i to branches
				}
				i++
			}
		}

		fun bfs(): Array<IntArray> {
			val distances = Array(height) { IntArray(widths) { -1 } }
			distances[y0][x0] = 0
			val queue = mutableListOf(x0 to y0)
			while (queue.isNotEmpty()) {
				val (x, y) = queue.removeFirst()
				val distance = distances[y][x]
				val cx = x * 2 + 1
				val cy = y * 2 + 1
				for ((dx, dy) in listOf(0 to -1, 0 to 1, -1 to 0, 1 to 0)) {
					if (grid[cy + dy][cx + dx] != '#' && distances[y + dy][x + dx] == -1) {
						queue.add(x + dx to y + dy)
						distances[y + dy][x + dx] = distance + 1
					}
				}
			}
			return distances
		}
	}

	fun part1(input: String): Int {
		val mapper = Mapper(input)
		mapper.mapRecurs(0, 0, 0)
		//println(mapper.grid.joinToString("\n") + "\n")
		return mapper.bfs().maxOf { it.max() }
	}

	fun part2(input: String): Int {
		val mapper = Mapper(input)
		mapper.mapRecurs(0, 0, 0)
		return mapper.bfs().sumOf { row -> row.count { it >= 1000 } }
	}

	val testInput = readInput("Day20_test")
	check(part1(testInput[0]) == 3)
	check(part1(testInput[1]) == 10)
	check(part1(testInput[2]) == 18)
	check(part1(testInput[3]) == 23)
	check(part1(testInput[4]) == 31)

	val input = readInput("Day20")
	part1(input[0]).println()
	part2(input[0]).println()
}
