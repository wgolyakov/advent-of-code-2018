import java.awt.Rectangle

fun main() {
	fun parse(input: List<String>): List<Rectangle> {
		return input.map { line ->
			val (point, size) = line.substringAfter(" @ ").split(": ")
			val (x, y) = point.split(',').map { it.toInt() }
			val (width, height) = size.split('x').map { it.toInt() }
			Rectangle(x, y, width, height)
		}
	}

	fun part1(input: List<String>): Int {
		val claims = parse(input)
		var overlaps = 0
		for (x in 0 until 1000) {
			for (y in 0 until 1000) {
				val within = claims.count { it.contains(x, y) }
				if (within >= 2) overlaps++
			}
		}
		return overlaps
	}

	fun part2(input: List<String>): Int {
		val claims = parse(input)
		for ((i, c) in claims.withIndex()) {
			val overlaps = claims.count { it.intersects(c) }
			if (overlaps == 1) return i + 1
		}
		return -1
	}

	val testInput = readInput("Day03_test")
	check(part1(testInput) == 4)
	check(part2(testInput) == 3)

	val input = readInput("Day03")
	part1(input).println()
	part2(input).println()
}
