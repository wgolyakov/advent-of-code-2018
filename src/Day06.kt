import kotlin.math.abs

fun main() {
	data class Point(val x: Int, val y: Int)

	fun parse(input: List<String>) = input.map { line ->
		line.split(", ").let { Point(it[0].toInt(), it[1].toInt()) }
	}

	fun distance(p1: Point, p2: Point) = abs(p1.x - p2.x) + abs(p1.y - p2.y)

	fun bounds(points: List<Point>): List<Int> {
		val minX = points.minOf { it.x }
		val minY = points.minOf { it.y }
		val maxX = points.maxOf { it.x }
		val maxY = points.maxOf { it.y }
		return listOf(minX, minY, maxX, maxY)
	}

	fun closest(p: Point, points: List<Point>): Int {
		val distances = IntArray(points.size)
		var minDist = Int.MAX_VALUE
		var minPoint = -1
		for ((i, point) in points.withIndex()) {
			val d = distance(point, p)
			distances[i] = d
			if (d < minDist) {
				minDist = d
				minPoint = i
			}
		}
		if (distances.count { it == minDist } > 1) return -1
		return minPoint
	}

	fun infinitePoints(points: List<Point>, grid: Map<Point, Int>,
					   minX: Int, minY: Int, maxX: Int, maxY: Int): Set<Int> {
		val result = mutableSetOf<Int>()
		l1@ for (i in points.indices) {
			for (x in minX .. maxX) {
				if (grid[Point(x, minY)] == i || grid[Point(x, maxY)] == i) {
					result.add(i)
					break@l1
				}
			}
			for (y in minY .. maxY) {
				if (grid[Point(minX, y)] == i || grid[Point(maxX, y)] == i) {
					result.add(i)
					break@l1
				}
			}
		}
		return result
	}

	fun inRegion(point: Point, points: List<Point>, r: Int): Boolean {
		var totalDistance = 0
		for (p in points) {
			totalDistance += distance(p, point)
		}
		return totalDistance < r
	}

	fun part1(input: List<String>): Int {
		val points = parse(input)
		val (minX, minY, maxX, maxY) = bounds(points)
		val grid = mutableMapOf<Point, Int>()
		val areas = IntArray(points.size)
		for (x in minX .. maxX) {
			for (y in minY .. maxY) {
				val p = Point(x, y)
				val minPoint = closest(p, points)
				grid[p] = minPoint
				if (minPoint != -1) areas[minPoint]++
			}
		}
		val inf = infinitePoints(points, grid, minX, minY, maxX, maxY)
		return areas.withIndex().filter { it.index !in inf }.maxOf { it.value }
	}

	fun part2(input: List<String>, r: Int = 10000): Int {
		val points = parse(input)
		val (minX, minY, maxX, maxY) = bounds(points)
		val x0 = (minX + maxX) / 2
		val y0 = (minY + maxY) / 2
		var regionSize = 1
		var found: Boolean
		for (i in 1 until r) {
			found = false
			for (x in x0 - i .. x0 + i) {
				if (inRegion(Point(x, y0 - i), points, r)) {
					regionSize++
					found = true
				}
				if (inRegion(Point(x, y0 + i), points, r)) {
					regionSize++
					found = true
				}
			}
			for (y in y0 - i + 1 .. y0 + i - 1) {
				if (inRegion(Point(x0 - i, y), points, r)) {
					regionSize++
					found = true
				}
				if (inRegion(Point(x0 + i, y), points, r)) {
					regionSize++
					found = true
				}
			}
			if (!found) break
		}
		return regionSize
	}

	val testInput = readInput("Day06_test")
	check(part1(testInput) == 17)
	check(part2(testInput, 32) == 16)

	val input = readInput("Day06")
	part1(input).println()
	part2(input).println()
}
