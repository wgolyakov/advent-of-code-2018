private enum class Direction(val dx: Int, val dy: Int) {
	Up(0, -1),
	Down(0, 1),
	Left(-1, 0),
	Right(1, 0);

	fun left(): Direction {
		return when (this) {
			Up -> Left
			Down -> Right
			Left -> Down
			Right -> Up
		}
	}

	fun right(): Direction {
		return when (this) {
			Up -> Right
			Down -> Left
			Left -> Up
			Right -> Down
		}
	}

	companion object {
		fun create(c: Char) = when (c) {
			'^' -> Up
			'v' -> Down
			'<' -> Left
			'>' -> Right
			else -> error("Wrong cart: $c")
		}
	}
}

fun main() {
	class Cart(var x: Int, var y: Int, var crossCount: Int, var direction: Direction) : Comparable<Cart> {
		override fun compareTo(other: Cart): Int {
			if (y != other.y) return y.compareTo(other.y)
			return x.compareTo(other.x)
		}
	}

	val cartChars = setOf('<', '>', '^', 'v')

	fun parse(input: List<String>): Pair<MutableList<Cart>, List<StringBuilder>> {
		val carts = mutableListOf<Cart>()
		val grid = mutableListOf<StringBuilder>()
		for ((y, row) in input.withIndex()) {
			grid.add(StringBuilder(row))
			for ((x, c) in row.withIndex()) {
				if (c in cartChars) {
					carts.add(Cart(x, y, 0, Direction.create(c)))
					grid[y][x] = if (c == '<' || c == '>') '-' else '|'
				} else {
					grid[y][x] = c
				}
			}
		}
		return carts to grid
	}

	fun move(cart: Cart, grid: List<StringBuilder>) {
		cart.x += cart.direction.dx
		cart.y += cart.direction.dy
		when (grid[cart.y][cart.x]) {
			'/' -> cart.direction = when (cart.direction) {
				Direction.Up -> Direction.Right
				Direction.Down -> Direction.Left
				Direction.Left -> Direction.Down
				Direction.Right -> Direction.Up
			}
			'\\' -> cart.direction = when (cart.direction) {
				Direction.Up -> Direction.Left
				Direction.Down -> Direction.Right
				Direction.Left -> Direction.Up
				Direction.Right -> Direction.Down
			}
			'+' -> {
				cart.direction = when (cart.crossCount % 3) {
					0 -> cart.direction.left()
					2 -> cart.direction.right()
					else -> cart.direction
				}
				cart.crossCount++
			}
		}
	}

	fun part1(input: List<String>): String {
		val (carts, grid) = parse(input)
		while (true) {
			for (cart in carts.sorted()) {
				move(cart, grid)
				val x = cart.x
				val y = cart.y
				val cartsInCell = carts.count { it.x == x && it.y == y }
				if (cartsInCell > 1) return "$x,$y"
			}
		}
	}

	fun part2(input: List<String>): String {
		val (carts, grid) = parse(input)
		while (carts.size > 1) {
			for (cart in carts.sorted()) {
				move(cart, grid)
				val x = cart.x
				val y = cart.y
				val cartsInCell = carts.count { it.x == x && it.y == y }
				if (cartsInCell > 1) carts.removeIf { it.x == x && it.y == y }
			}
		}
		return "${carts[0].x},${carts[0].y}"
	}

	val testInput = readInput("Day13_test")
	check(part1(testInput) == "7,3")
	val testInput2 = readInput("Day13_test2")
	check(part2(testInput2) == "6,4")

	val input = readInput("Day13")
	part1(input).println()
	part2(input).println()
}
