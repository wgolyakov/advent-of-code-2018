fun main() {
	val opcodes = listOf("addr", "addi", "mulr", "muli", "banr", "bani", "borr", "bori",
		"setr", "seti", "gtir", "gtri", "gtrr", "eqir", "eqri", "eqrr")

	fun exec(opcode: String, instruction: List<Int>, regsInit: List<Int>): List<Int> {
		val regs = regsInit.toMutableList()
		val (_, a, b, c) = instruction
		when (opcode) {
			"addr" -> regs[c] = regs[a] + regs[b]
			"addi" -> regs[c] = regs[a] + b
			"mulr" -> regs[c] = regs[a] * regs[b]
			"muli" -> regs[c] = regs[a] * b
			"banr" -> regs[c] = regs[a] and regs[b]
			"bani" -> regs[c] = regs[a] and b
			"borr" -> regs[c] = regs[a] or regs[b]
			"bori" -> regs[c] = regs[a] or b
			"setr" -> regs[c] = regs[a]
			"seti" -> regs[c] = a
			"gtir" -> regs[c] = if (a > regs[b]) 1 else 0
			"gtri" -> regs[c] = if (regs[a] > b) 1 else 0
			"gtrr" -> regs[c] = if (regs[a] > regs[b]) 1 else 0
			"eqir" -> regs[c] = if (a == regs[b]) 1 else 0
			"eqri" -> regs[c] = if (regs[a] == b) 1 else 0
			"eqrr" -> regs[c] = if (regs[a] == regs[b]) 1 else 0
			else -> error("Wrong opcode: $opcode")
		}
		return regs
	}

	fun part1(input: List<String>): Int {
		var i = 0
		var result = 0
		while (i + 3 <= input.size && input[i].startsWith("Before")) {
			val regsBefore = input[i++].substringAfter("Before: [").dropLast(1)
				.split(", ").map { it.toInt() }.toMutableList()
			val instruction = input[i++].split(' ').map { it.toInt() }
			val regsAfter = input[i++].substringAfter("After:  [").dropLast(1)
				.split(", ").map { it.toInt() }
			i++
			var goodOpcodes = 0
			for (opcode in opcodes) {
				if (exec(opcode, instruction, regsBefore) == regsAfter) goodOpcodes++
			}
			if (goodOpcodes >= 3) result++
		}
		return result
	}

	fun part2(input: List<String>): Int {
		var i = 0
		val goodOpcodes = Array(opcodes.size) { mutableSetOf<String>() }
		while (i + 3 <= input.size && input[i].startsWith("Before")) {
			val regsBefore = input[i++].substringAfter("Before: [").dropLast(1)
				.split(", ").map { it.toInt() }.toMutableList()
			val instruction = input[i++].split(' ').map { it.toInt() }
			val regsAfter = input[i++].substringAfter("After:  [").dropLast(1)
				.split(", ").map { it.toInt() }
			i++
			for (opcode in opcodes) {
				if (exec(opcode, instruction, regsBefore) == regsAfter) goodOpcodes[instruction[0]].add(opcode)
			}
		}
		val foundNumbers = mutableSetOf<Int>()
		while (foundNumbers.size < opcodes.size) {
			for ((n, candidates) in goodOpcodes.withIndex()) {
				if (n in foundNumbers) continue
				if (candidates.size == 1) {
					foundNumbers.add(n)
					for (c in goodOpcodes) if (c !== candidates) c.remove(candidates.single())
				}
			}
		}
		val opcodeByNum = goodOpcodes.map { it.single() }

		// Run test program
		i += 2
		var regs = listOf(0, 0, 0, 0)
		while (i < input.size) {
			val instruction = input[i++].split(' ').map { it.toInt() }
			regs = exec(opcodeByNum[instruction[0]], instruction, regs)
		}
		return regs[0]
	}

	val testInput = readInput("Day16_test")
	check(part1(testInput) == 1)

	val input = readInput("Day16")
	part1(input).println()
	part2(input).println()
}
