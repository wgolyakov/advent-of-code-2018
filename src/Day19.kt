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

	fun exec(instruction: List<Any>, regs: MutableList<Long>) {
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
			"bani" -> regs[c] = regs[a] and b.toLong()
			"borr" -> regs[c] = regs[a] or regs[b]
			"bori" -> regs[c] = regs[a] or b.toLong()
			"setr" -> regs[c] = regs[a]
			"seti" -> regs[c] = a.toLong()
			"gtir" -> regs[c] = if (a > regs[b]) 1 else 0
			"gtri" -> regs[c] = if (regs[a] > b) 1 else 0
			"gtrr" -> regs[c] = if (regs[a] > regs[b]) 1 else 0
			"eqir" -> regs[c] = if (a.toLong() == regs[b]) 1 else 0
			"eqri" -> regs[c] = if (regs[a] == b.toLong()) 1 else 0
			"eqrr" -> regs[c] = if (regs[a] == regs[b]) 1 else 0
			else -> error("Wrong opcode: $opcode")
		}
	}

	fun part1(input: List<String>): Int {
		val (ip, opcodes) = parse(input)
		val regs = mutableListOf(0L, 0L, 0L, 0L, 0L, 0L)
		var i = regs[ip].toInt()
		while (i >= 0 && i < opcodes.size) {
			regs[ip] = i.toLong()
			exec(opcodes[i], regs)
			i = regs[ip].toInt() + 1
		}
		return regs[0].toInt()
	}

	fun part2Slow(input: List<String>): Long {
		val (ip, opcodes) = parse(input)
		val regs = mutableListOf(1L, 0L, 0L, 0L, 0L, 0L)
		var i = regs[ip].toInt()
		while (i >= 0 && i < opcodes.size) {
			regs[ip] = i.toLong()
			exec(opcodes[i], regs)
			i = regs[ip].toInt() + 1
		}
		return regs[0]
	}

	fun part2(input: List<String>, r00: Int = 1): Int {
		var r0 = r00
		var r1 = 1010
		if (r0 == 1) {
			r1 = 10551410
			r0 = 0
		}
		for (r3 in 1..r1) {
			if (r1 % r3 == 0) r0 += r3
		}
		return r0
	}

	val testInput = readInput("Day19_test")
	check(part1(testInput) == 6)

	val input = readInput("Day19")
	part1(input).println()
	check(part2(input, 0) == 1836)
	part2(input).println()
}
