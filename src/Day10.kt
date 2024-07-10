fun main() {
	class Coordinates(val x: Int, val y: Int)
	class Point(val position: Coordinates, val velocity: Coordinates)

	fun parse(input: List<String>): List<Point> {
		return input.map { line ->
			val (x, y, vx, vy) =
				Regex("position=<\\s*(-?\\d+),\\s*(-?\\d+)> velocity=<\\s*(-?\\d+),\\s*(-?\\d+)>").
				matchEntire(line)!!.groupValues.takeLast(4).map { it.toInt() }
			Point(Coordinates(x, y), Coordinates(vx, vy))
		}
	}

	fun area(points: List<Point>): Long {
		val minX = points.minOf { it.position.x }
		val minY = points.minOf { it.position.y }
		val maxX = points.maxOf { it.position.x }
		val maxY = points.maxOf { it.position.y }
		val width = maxX - minX + 1
		val height = maxY - minY + 1
		return width.toLong() * height.toLong()
	}

	fun move(points: List<Point>): List<Point> {
		val result = mutableListOf<Point>()
		for (p in points) {
			result.add(Point(Coordinates(p.position.x + p.velocity.x, p.position.y + p.velocity.y), p.velocity))
		}
		return result
	}

	fun pointsToString(points: List<Point>): String {
		val minX = points.minOf { it.position.x }
		val minY = points.minOf { it.position.y }
		val maxX = points.maxOf { it.position.x }
		val maxY = points.maxOf { it.position.y }
		val grid = Array(maxY - minY + 1) { StringBuilder(".".repeat(maxX - minX + 1)) }
		for (point in points) {
			grid[point.position.y - minY][point.position.x - minX] = '#'
		}
		return grid.joinToString("\n")
	}

	fun part1(input: List<String>): String {
		var points = parse(input)
		var area = area(points)
		while (true) {
			val nextPoints = move(points)
			val nextArea = area(nextPoints)
			if (nextArea > area) break
			points = nextPoints
			area = nextArea
		}
		val result = pointsToString(points)
		println(result)
		println()
		return result
	}

	fun part2(input: List<String>): Int {
		var points = parse(input)
		var area = area(points)
		var time = 0
		while (true) {
			val nextPoints = move(points)
			val nextArea = area(nextPoints)
			if (nextArea > area) break
			points = nextPoints
			area = nextArea
			time++
		}
		return time
	}

	val testInput = readInput("Day10_test")
	check(part1(testInput) ==
			"#...#..###\n" +
			"#...#...#.\n" +
			"#...#...#.\n" +
			"#####...#.\n" +
			"#...#...#.\n" +
			"#...#...#.\n" +
			"#...#...#.\n" +
			"#...#..###")
	check(part2(testInput) == 3)

	val input = readInput("Day10")
	part1(input) // EHAZPZHP
	part2(input).println()
}
