fun main() {
	fun powerLevel(x: Int, y: Int, serialNumber: Int): Int {
		val rackId = x + 10
		val powerLevel = (rackId * y + serialNumber) * rackId
		return (powerLevel % 1000) / 100 - 5
	}

	fun powerGrid(serialNumber: Int): Array<IntArray> {
		val grid = Array(301) { IntArray(301) }
		for (x in 1 .. 300 - 2) {
			for (y in 1 .. 300 - 2) {
				grid[x][y] = powerLevel(x, y, serialNumber)
			}
		}
		return grid
	}

	fun part1(input: List<String>): String {
		val serialNumber = input[0].toInt()
		val powerGrid = powerGrid(serialNumber)
		var maxPower = Int.MIN_VALUE
		var maxX = 0
		var maxY = 0
		for (x in 1 .. 300 - 3 + 1) {
			for (y in 1 .. 300 - 3 + 1) {
				var power3x3 = 0
				for (a in 0 until 3) {
					for (b in 0 until 3) {
						power3x3 += powerGrid[x + a][y + b]
					}
				}
				if (power3x3 > maxPower) {
					maxPower = power3x3
					maxX = x
					maxY = y
				}
			}
		}
		return "$maxX,$maxY"
	}

	fun part2(input: List<String>): String {
		val serialNumber = input[0].toInt()
		val powerGrid = powerGrid(serialNumber)
		var maxPower = Int.MIN_VALUE
		var maxX = 0
		var maxY = 0
		var maxSize = 0
		val cache = Array(301) { Array(301) { IntArray(301) } }
		for (size in 1 .. 300) {
			for (x in 1..300 - size + 1) {
				for (y in 1..300 - size + 1) {
					var power: Int
					if (size > 1) {
						power = cache[x][y][size - 1]
						for (a in 0 until size) {
							power += powerGrid[x + a][y + size - 1]
						}
						for (b in 0 until size - 1) {
							power += powerGrid[x + size - 1][y + b]
						}
					} else {
						power = powerGrid[x][y]
					}
					cache[x][y][size] = power
					if (power > maxPower) {
						maxPower = power
						maxX = x
						maxY = y
						maxSize = size
					}
				}
			}
		}
		return "$maxX,$maxY,$maxSize"
	}

	check(powerLevel(3, 5, 8) == 4)
	check(powerLevel(122, 79, 57) == -5)
	check(powerLevel(217, 196, 39) == 0)
	check(powerLevel(101, 153, 71) == 4)

	val testInput = readInput("Day11_test")
	val testInput2 = readInput("Day11_test2")
	check(part1(testInput) == "33,45")
	check(part1(testInput2) == "21,61")
	check(part2(testInput) == "90,269,16")
	check(part2(testInput2) == "232,251,12")

	val input = readInput("Day11")
	part1(input).println()
	part2(input).println()
}
