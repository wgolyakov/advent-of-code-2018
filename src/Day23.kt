import java.math.BigInteger
import kotlin.math.abs

fun main() {
	// 3D point
	data class Point(val x: Int, val y: Int, val z: Int) {
		fun distance(p: Point) = abs(x - p.x) + abs(y - p.y) + abs(z - p.z)
		fun distance0() = abs(x) + abs(y) + abs(z)
	}

	// Plain by 3 points
	class Plain(p1: Point, p2: Point, p3: Point) {
		// Plain equation: Ax + By + Cz + D = 0
		val a: BigInteger
		val b: BigInteger
		val c: BigInteger
		val d: BigInteger

		init {
			// Calculating plane coefficients by coordinates of 3 points
			val x1 = p1.x.toBigInteger()
			val y1 = p1.y.toBigInteger()
			val z1 = p1.z.toBigInteger()
			val x2 = p2.x.toBigInteger()
			val y2 = p2.y.toBigInteger()
			val z2 = p2.z.toBigInteger()
			val x3 = p3.x.toBigInteger()
			val y3 = p3.y.toBigInteger()
			val z3 = p3.z.toBigInteger()
			a = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2)
			b = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2)
			c = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)
			d = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1))
		}

		// Intersection point of x-axis (y = 0, z = 0)
		fun x() = -d / a
		override fun toString() = "Plain(x=${x()})"
	}

	fun matrix3x3Determinant(
		a11: BigInteger, a12: BigInteger, a13: BigInteger,
		a21: BigInteger, a22: BigInteger, a23: BigInteger,
		a31: BigInteger, a32: BigInteger, a33: BigInteger
	) =	a11 * a22 * a33 - a21 * a12 * a33 - a32 * a23 * a11 - a31 * a22 * a13 + a21 * a32 * a13 + a12 * a23 * a31

	// Intersection point of 3 planes
	fun threePlainPoint(pl1: Plain, pl2: Plain, pl3: Plain): Point {
		val d = matrix3x3Determinant(
			pl1.a, pl1.b, pl1.c,
			pl2.a, pl2.b, pl2.c,
			pl3.a, pl3.b, pl3.c)
		val x = -matrix3x3Determinant(
			pl1.d, pl1.b, pl1.c,
			pl2.d, pl2.b, pl2.c,
			pl3.d, pl3.b, pl3.c) / d
		val y = -matrix3x3Determinant(
			pl1.a, pl1.d, pl1.c,
			pl2.a, pl2.d, pl2.c,
			pl3.a, pl3.d, pl3.c) / d
		val z = -matrix3x3Determinant(
			pl1.a, pl1.b, pl1.d,
			pl2.a, pl2.b, pl2.d,
			pl3.a, pl3.b, pl3.d) / d
		return Point(x.toInt(), y.toInt(), z.toInt())
	}

	// 3D Cell by 8 Plains. May represent a single nanobot octahedron or their intersection.
	class Cell(val plains: List<Plain>) {
		override fun toString() = "Cell$plains"

		fun isIntersect(c: Cell): Boolean {
			for (i in plains.indices step 2) {
				val x1 = plains[i].x()
				val x2 = plains[i + 1].x()
				val x3 = c.plains[i].x()
				val x4 = c.plains[i + 1].x()
				if (x2 < x3 || x1 > x4) return false
			}
			return true
		}

		fun intersect(c: Cell): Cell {
			val interPlains = mutableListOf<Plain>()
			for (i in plains.indices step 2) {
				val pl1 = plains[i]
				val pl2 = plains[i + 1]
				val pl3 = c.plains[i]
				val pl4 = c.plains[i + 1]
				val x1 = pl1.x()
				val x2 = pl2.x()
				val x3 = pl3.x()
				val x4 = pl4.x()
				if (x2 < x3 || x1 > x4) error("No intersection")
				val plr1 = if (x1 <= x3) pl3 else pl1
				val plr2 = if (x2 <= x4) pl2 else pl4
				interPlains.add(plr1)
				interPlains.add(plr2)
			}
			return Cell(interPlains)
		}

		fun vertices(): List<Point> {
			val p0 = threePlainPoint(plains[0], plains[2], plains[6])
			val p1 = threePlainPoint(plains[1], plains[5], plains[7])
			val p2 = threePlainPoint(plains[0], plains[5], plains[6])
			val p3 = threePlainPoint(plains[2], plains[4], plains[7])
			val p4 = threePlainPoint(plains[1], plains[3], plains[6])
			val p5 = threePlainPoint(plains[0], plains[2], plains[7])
			return listOf(p0, p1, p2, p3, p4, p5)
		}

		fun minMaxPoints(): Pair<Point, Point> {
			val vertices = vertices()
			val xMin = vertices.minOf { it.x }
			val xMax = vertices.maxOf { it.x }
			val yMin = vertices.minOf { it.y }
			val yMax = vertices.maxOf { it.y }
			val zMin = vertices.minOf { it.z }
			val zMax = vertices.maxOf { it.z }
			return Point(xMin, yMin, zMin) to Point(xMax, yMax, zMax)
		}
	}

	// Octahedron of Nanobot
	class Nanobot(val pos: Point, val r: Int) {
		val cell: Cell by lazy { cell() }
		fun inRange(p: Point) = pos.distance(p) <= r
		fun inRange(n: Nanobot) = inRange(n.pos)
		override fun toString() = "Nanobot(pos=$pos, r=$r)"

		fun vertices() = listOf(
			Point(pos.x - r, pos.y, pos.z), Point(pos.x + r, pos.y, pos.z),
			Point(pos.x, pos.y - r, pos.z), Point(pos.x, pos.y + r, pos.z),
			Point(pos.x, pos.y, pos.z - r), Point(pos.x, pos.y, pos.z + r)
		)

		private fun cell(): Cell {
			val plains = mutableListOf<Plain>()
			val v = vertices()
			plains.add(Plain(v[0], v[2], v[5])) //0
			plains.add(Plain(v[3], v[4], v[1])) //1
			plains.add(Plain(v[0], v[3], v[5])) //2
			plains.add(Plain(v[2], v[4], v[1])) //3
			plains.add(Plain(v[0], v[3], v[4])) //4
			plains.add(Plain(v[2], v[5], v[1])) //5
			plains.add(Plain(v[0], v[2], v[4])) //6
			plains.add(Plain(v[3], v[5], v[1])) //7
			return Cell(plains)
		}
	}

	fun parse(input: List<String>) = input.map { line ->
		val (x, y, z, r) = Regex("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)")
			.matchEntire(line)!!.groupValues.takeLast(4).map { it.toInt() }
		Nanobot(Point(x, y, z), r)
	}

	fun part1(input: List<String>): Int {
		val nanobots = parse(input)
		val strongest = nanobots.maxBy { it.r }
		return nanobots.count { strongest.inRange(it) }
	}

	fun part2(input: List<String>): Int {
		val nanobots = parse(input)
		var interCell = nanobots.first().cell
		for (nanobot in nanobots) {
			if (interCell.isIntersect(nanobot.cell)) {
				interCell = interCell.intersect(nanobot.cell)
			}
		}
		val (min, max) = interCell.minMaxPoints()
		var maxInRange = 0
		var maxPoint = Point(0, 0, 0)
		for (x in min.x .. max.x) {
			for (y in min.y .. max.y) {
				for (z in min.z .. max.z) {
					val point = Point(x, y, z)
					val inRange = nanobots.count { it.inRange(point) }
					if (inRange > maxInRange ||	(inRange == maxInRange && point.distance0() < maxPoint.distance0())) {
						maxInRange = inRange
						maxPoint = point
					}
				}
			}
		}
		//println("maxInRange: $maxInRange")
		//println("maxPoint: $maxPoint")
		return maxPoint.distance0()
	}

	val testInput = readInput("Day23_test")
	check(part1(testInput) == 7)
	val testInput2 = readInput("Day23_test2")
	check(part2(testInput2) == 36)

	val input = readInput("Day23")
	part1(input).println()
	part2(input).println()
}
