private class Node(val parent: Node?) {
	val children = mutableListOf<Node>()
	val metadata = mutableListOf<Int>()

	fun metadataSum(): Int {
		return metadata.sum() + children.sumOf { it.metadataSum() }
	}

	fun value(): Int {
		if (children.isEmpty()) return metadata.sum()
		var result = 0
		for (childNum in metadata) {
			val i = childNum - 1
			if (i < 0 || i >= children.size) continue
			result += children[i].value()
		}
		return result
	}

	companion object {
		fun parse(numbers: List<Int>, parent: Node?, offset: Int): Int {
			val node = Node(parent)
			parent?.children?.add(node)
			var i = offset
			val childCount = numbers[i++]
			val metaCount = numbers[i++]
			repeat(childCount) {
				i = parse(numbers, node, i)
			}
			repeat(metaCount) {
				node.metadata.add(numbers[i++])
			}
			return i
		}
	}
}

fun main() {
	fun part1(input: List<String>): Int {
		val numbers = input[0].split(' ').map { it.toInt() }
		val preRoot = Node(null)
		Node.parse(numbers, preRoot, 0)
		return preRoot.metadataSum()
	}

	fun part2(input: List<String>): Int {
		val numbers = input[0].split(' ').map { it.toInt() }
		val preRoot = Node(null)
		Node.parse(numbers, preRoot, 0)
		val root = preRoot.children.single()
		return root.value()
	}

	val testInput = readInput("Day08_test")
	check(part1(testInput) == 138)
	check(part2(testInput) == 66)

	val input = readInput("Day08")
	part1(input).println()
	part2(input).println()
}
