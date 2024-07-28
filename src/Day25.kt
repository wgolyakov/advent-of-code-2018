import kotlin.math.abs

fun main() {
	data class Point(val x: Int, val y: Int, val z: Int, val t: Int) {
		constructor(l: List<Int>) : this(l[0], l[1], l[2], l[3])
		fun distance(p: Point) = abs(x - p.x) + abs(y - p.y) + abs(z - p.z) + abs(t - p.t)
		fun joins(p: Point) = distance(p) <= 3
	}

	fun part1(input: List<String>): Int {
		val points = input.map { line -> Point(line.split(',').map { it.toInt() }) }
		val constellations = mutableListOf<MutableSet<Point>>()
		for (point in points) {
			val myConstellations = constellations.filter { c -> c.any { it.joins(point) } }
			when (myConstellations.size) {
				0 -> constellations.add(mutableSetOf(point))
				1 -> myConstellations.single().add(point)
				else -> {
					val con = mutableSetOf(point)
					for (c in myConstellations) con.addAll(c)
					constellations.removeAll(myConstellations)
					constellations.add(con)
				}
			}
		}
		return constellations.size
	}

	val testInput = readInput("Day25_test")
	val testInput2 = readInput("Day25_test2")
	val testInput3 = readInput("Day25_test3")
	val testInput4 = readInput("Day25_test4")
	check(part1(testInput) == 2)
	check(part1(testInput2) == 4)
	check(part1(testInput3) == 3)
	check(part1(testInput4) == 8)

	val input = readInput("Day25")
	part1(input).println()
}
