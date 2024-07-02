fun main() {
	fun part1(input: List<String>) = input.sumOf { it.toInt() }

	fun part2(input: List<String>): Int {
		val changes = input.map { it.toInt() }
		var frequency = 0
		val values = mutableSetOf(frequency)
		while (true) {
			for (change in changes) {
				frequency += change
				if (frequency in values) return frequency
				values.add(frequency)
			}
		}
	}

	fun changes(input: List<String>, num: Int) = input[num].split(", ")

	val testInput = readInput("Day01_test")
	check(part1(changes(testInput, 0)) == 3)
	check(part1(changes(testInput, 1)) == 3)
	check(part1(changes(testInput, 2)) == 0)
	check(part1(changes(testInput, 3)) == -6)

	check(part2(changes(testInput, 0)) == 2)
	check(part2(changes(testInput, 4)) == 0)
	check(part2(changes(testInput, 5)) == 10)
	check(part2(changes(testInput, 6)) == 5)
	check(part2(changes(testInput, 7)) == 14)

	val input = readInput("Day01")
	part1(input).println()
	part2(input).println()
}
