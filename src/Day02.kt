fun main() {
	fun countLetters(s: String): Map<Char, Int> {
		val counters = mutableMapOf<Char, Int>()
		for (c in s) {
			counters[c] = (counters[c] ?: 0) + 1
		}
		return counters
	}

	fun difOne(s1: String, s2: String): Boolean {
		var difCount = 0
		for ((i, c1) in s1.withIndex()) {
			val c2 = s2[i]
			if (c1 != c2) {
				difCount++
				if (difCount > 1) return false
			}
		}
		return difCount == 1
	}

	fun sameLetters(s1: String, s2: String) = s1.filterIndexed { i, c -> c == s2[i] }

	fun part1(input: List<String>): Int {
		val counters = input.map { countLetters(it) }
		val two = counters.count { 2 in it.values }
		val three = counters.count { 3 in it.values }
		return two * three
	}

	fun part2(input: List<String>): String {
		for ((i, s1) in input.withIndex()) {
			for (s2 in input.subList(i + 1, input.size)) {
				if (difOne(s1, s2)) return sameLetters(s1, s2)
			}
		}
		return ""
	}

	val testInput = readInput("Day02_test")
	check(part1(testInput) == 12)
	val testInput2 = readInput("Day02_test2")
	check(part2(testInput2) == "fgij")

	val input = readInput("Day02")
	part1(input).println()
	part2(input).println()
}
