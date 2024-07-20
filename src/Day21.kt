fun main() {
	fun parse(input: List<String>): Pair<Int, List<List<Any>>> {
		val ip = input[0].substringAfter("#ip ").toInt()
		val opcodes = mutableListOf<List<Any>>()
		for (line in input) {
			if (line[0] == '#') continue
			val parts = line.split(' ')
			val instruction = listOf(parts[0], parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
			opcodes.add(instruction)
		}
		return ip to opcodes
	}

	fun exec(instruction: List<Any>, regs: MutableList<Int>) {
		val opcode = instruction[0]
		val a = instruction[1] as Int
		val b = instruction[2] as Int
		val c = instruction[3] as Int
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
	}

	fun part1(input: List<String>): Int {
		val (ip, opcodes) = parse(input)
		val regs = mutableListOf(0, 0, 0, 0, 0, 0)
		var i = regs[ip]
		while (i >= 0 && i < opcodes.size) {
			regs[ip] = i
			val instr = opcodes[i]
			if (instr[0] == "eqrr") {
				// eqrr 1 0 5
				// r5 = if (r1 == r0) 1 else 0
				// Value of r1 is the right value of r0
				return regs[1]
			}
			exec(opcodes[i], regs)
			i = regs[ip] + 1
		}
		return -1
	}

	fun part2(input: List<String>): Int {
		val (ip, opcodes) = parse(input)
		val regs = mutableListOf(0, 0, 0, 0, 0, 0)
		var i = regs[ip]
		val results = mutableSetOf<Int>()
		while (i >= 0 && i < opcodes.size) {
			regs[ip] = i
			val instr = opcodes[i]
			if (instr[0] == "eqrr") {
				// eqrr 1 0 5
				// r5 = if (r1 == r0) 1 else 0
				// Value of r1 is the right value of r0
				val r1 = regs[1]
				if (r1 in results) {
					// When the values begin to repeat, there will be no others.
					// So the answer is the last of them.
					return results.last()
				}
				results.add(r1)
			}
			exec(instr, regs)
			i = regs[ip] + 1
		}
		return -1
	}

	val input = readInput("Day21")
	part1(input).println()
	part2(input).println()
}
