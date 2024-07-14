fun main() {
	fun part1(input: List<String>): String {
		val numberOfRecipes = input[0].toInt()
		val board = mutableListOf(3, 7)
		var a = 0
		var b = 1
		while (board.size < numberOfRecipes + 10) {
			val r = board[a] + board[b]
			if (r < 10) {
				board.add(r)
			} else {
				board.add(r / 10)
				board.add(r % 10)
			}
			val da = 1 + board[a]
			val db = 1 + board[b]
			a = (a + da) % board.size
			b = (b + db) % board.size
		}
		return board.subList(numberOfRecipes, numberOfRecipes + 10).joinToString("")
	}

	fun part2(input: List<String>): Int {
		val scores = input[0]
		val board = StringBuilder("37")
		var a = 0
		var b = 1
		while (true) {
			val r = board[a].digitToInt() + board[b].digitToInt()
			board.append(r)
			if (board.endsWith(scores) || (board.length > scores.length &&
						board.substring(board.length - scores.length - 1, board.length - 1) == scores)) {
				return board.toString().substringBefore(scores).length
			}
			val da = 1 + board[a].digitToInt()
			val db = 1 + board[b].digitToInt()
			a = (a + da) % board.length
			b = (b + db) % board.length
		}
	}

	check(part1(listOf("9")) == "5158916779")
	check(part1(listOf("5")) == "0124515891")
	check(part1(listOf("18")) == "9251071085")
	check(part1(listOf("2018")) == "5941429882")

	check(part2(listOf("51589")) == 9)
	check(part2(listOf("01245")) == 5)
	check(part2(listOf("92510")) == 18)
	check(part2(listOf("59414")) == 2018)

	val input = readInput("Day14")
	part1(input).println()
	part2(input).println()
}
