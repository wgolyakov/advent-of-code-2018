fun main() {
	class Node(val value: Int) {
		var prev: Node = this
		var next: Node = this
		override fun toString() = value.toString()
	}

	class CircularLinkedList(val head: Node) {
		var size = 1

		fun insertAfter(value: Int, prev: Node): Node {
			val node = Node(value)
			val next = prev.next
			prev.next = node
			node.prev = prev
			node.next = next
			next.prev = node
			size++
			return node
		}

		fun remove(node: Node): Node {
			val prev = node.prev
			val next = node.next
			prev.next = next
			next.prev = prev
			size--
			return next
		}

		override fun toString(): String {
			val s = StringBuilder(head.toString())
			var node = head.next
			while (node !== head) {
				s.append(", ")
				s.append(node)
				node = node.next
			}
			return "[$s]"
		}
	}

	fun parse(line: String) = Regex("(\\d+) players; last marble is worth (\\d+) points")
			.matchEntire(line)!!.groupValues.takeLast(2).map { it.toInt() }

	fun part1(input: String): Int {
		val (playerCount, lastMarble) = parse(input)
		val scores = IntArray(playerCount)
		val circle = mutableListOf(0)
		var i = 0
		var player = 0
		for (marble in 1 .. lastMarble) {
			if (marble % 23 == 0) {
				scores[player] += marble
				i -= 7
				if (i < 0) i += circle.size
				scores[player] += circle[i]
				circle.removeAt(i)
			} else {
				i = (i + 2) % circle.size
				circle.add(i, marble)
			}
			player++
			if (player == playerCount) player = 0
		}
		return scores.max()
	}

	fun part2(input: String): Long {
		val (playerCount, lastMarble) = parse(input)
		val scores = LongArray(playerCount)
		var currNode = Node(0)
		val circle = CircularLinkedList(currNode)
		var player = 0
		for (marble in 1 .. lastMarble * 100) {
			if (marble % 23 == 0) {
				scores[player] += marble.toLong()
				repeat(7) { currNode = currNode.prev }
				scores[player] += currNode.value.toLong()
				currNode = circle.remove(currNode)
			} else {
				currNode = circle.insertAfter(marble, currNode.next)
			}
			player++
			if (player == playerCount) player = 0
		}
		return scores.max()
	}

	val testInput = readInput("Day09_test")
	check(part1(testInput[0]) == 32)
	check(part1(testInput[1]) == 8317)
	check(part1(testInput[2]) == 146373)
	check(part1(testInput[3]) == 2764)
	check(part1(testInput[4]) == 54718)
	check(part1(testInput[5]) == 37305)

	val input = readInput("Day09")
	part1(input[0]).println()
	part2(input[0]).println()
}
