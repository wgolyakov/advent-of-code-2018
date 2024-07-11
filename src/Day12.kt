fun main() {
	data class State(val plants: String, val shift: Long) {
		fun sum() = plants.withIndex().filter { (_, c) -> c == '#' }.sumOf { (i, _) -> i + shift }
	}

	fun generation(state: State, pattern: Map<String, Char>): State {
		var plants = "....${state.plants}...."
		var shift = state.shift - 4
		val next = StringBuilder()
		for (part in plants.windowed(5)) {
			val c = pattern[part] ?: '.'
			next.append(c)
		}
		plants = next.toString()
		shift += 2
		while (plants[0] == '.') {
			plants = plants.drop(1)
			shift++
		}
		while (plants.last() == '.') {
			plants = plants.dropLast(1)
		}
		return State(plants, shift)
	}

	fun part1(input: List<String>): Int {
		val plants = input[0].substringAfter("initial state: ")
		val pattern = mutableMapOf<String, Char>()
		for (line in input.drop(2)) {
			val (key, value) = line.split(" => ")
			pattern[key] = value[0]
		}
		var state = State(plants, 0)
		for (g in 1 .. 20) {
			state = generation(state, pattern)
		}
		return state.sum().toInt()
	}

	fun part2(input: List<String>): Long {
		val plants = input[0].substringAfter("initial state: ")
		val pattern = mutableMapOf<String, Char>()
		for (line in input.drop(2)) {
			val (key, value) = line.split(" => ")
			pattern[key] = value[0]
		}
		var state = State(plants, 0)
		val history = mutableSetOf<String>()
		val stateHistory = mutableListOf<State>()
		var g = 0
		while (state.plants !in history) {
			history.add(state.plants)
			stateHistory.add(state)
			state = generation(state, pattern)
			g++
		}
		val repeatGen = stateHistory.indexOfFirst { it.plants == state.plants }
		val repeatState = stateHistory[repeatGen]
		val period = g - repeatGen
		if (period != 1) error("This solution only for period 1")
		val shiftDelta = state.shift - repeatState.shift
		state = State(state.plants, state.shift + shiftDelta * (50000000000 - g))
		return state.sum()
	}

	val testInput = readInput("Day12_test")
	check(part1(testInput) == 325)

	val input = readInput("Day12")
	part1(input).println()
	part2(input).println()
}
