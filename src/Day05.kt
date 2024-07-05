fun main() {
	fun react(polymer: String): String {
		val s = StringBuilder(polymer)
		var len = -1
		while (len != s.length) {
			len = s.length
			if (len <= 1) break
			for (i in s.length - 2 downTo 0) {
				if (i > s.length - 2) continue
				if (s[i].isLowerCase() != s[i + 1].isLowerCase() && s[i].lowercaseChar() == s[i + 1].lowercaseChar()) {
					s.delete(i, i + 2)
				}
			}
		}
		return s.toString()
	}

	fun part1(input: List<String>): Int {
		return react(input[0]).length
	}

	fun part2(input: List<String>): Int {
		var minLen = Int.MAX_VALUE
		for (c in 'a' .. 'z') {
			val u = c.uppercaseChar()
			val polymer = input[0].filter { it != c && it != u }
			val len = react(polymer).length
			if (len < minLen) minLen = len
		}
		return minLen
	}

	val testInput = readInput("Day05_test")
	check(part1(testInput) == 10)
	check(part2(testInput) == 4)

	val input = readInput("Day05")
	part1(input).println()
	part2(input).println()
}
