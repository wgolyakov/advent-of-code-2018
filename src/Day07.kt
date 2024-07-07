fun main() {
	data class Step(val name: Char) {
		val prevSteps = mutableSetOf<Step>()
		val nextSteps = mutableSetOf<Step>()
		override fun toString() = name.toString()
	}

	class Worker(val num: Int) {
		private var step: Step? = null
		private var freeTime = 0
		override fun toString() = "Worker(num=$num, step=$step, freeTime=$freeTime)"
		fun isReady() = step == null

		fun start(step: Step, time: Int, amount: Int) {
			this.step = step
			freeTime = time + amount + (step.name - 'A' + 1)
		}

		fun isFinish(time: Int) = freeTime == time && step != null

		fun finish(): Step {
			val s = step!!
			step = null
			return s
		}
	}

	fun parse(input: List<String>): Map<Char, Step> {
		val result = mutableMapOf<Char, Step>()
		for (line in input) {
			val (c1, c2) = Regex("Step (\\w) must be finished before step (\\w) can begin.")
				.matchEntire(line)!!.groupValues.takeLast(2).map { it[0] }
			val s1 = result.getOrPut(c1) { Step(c1) }
			val s2 = result.getOrPut(c2) { Step(c2) }
			s1.nextSteps.add(s2)
			s2.prevSteps.add(s1)
		}
		return result
	}

	fun part1(input: List<String>): String {
		val steps = parse(input)
		val result = StringBuilder()
		val finished = mutableSetOf<Step>()
		var available = steps.values.filter { it.prevSteps.isEmpty() }.sortedBy { it.name }
		while (available.isNotEmpty()) {
			val step = available.first()
			result.append(step.name)
			finished.add(step)
			val ready = step.nextSteps.filter { finished.containsAll(it.prevSteps) }
			available = (available.drop(1) + ready).toSet().sortedBy { it.name }
		}
		return result.toString()
	}

	fun part2(input: List<String>, workersCount: Int = 5, amount: Int = 60): Int {
		val steps = parse(input)
		var time = 0
		val finished = mutableSetOf<Step>()
		val workers = Array(workersCount) { Worker(it) }
		val available = steps.values.filter { it.prevSteps.isEmpty() }.toMutableSet()
		val working = mutableSetOf<Step>()
		while (available.isNotEmpty() || working.isNotEmpty()) {
			for (worker in workers) {
				if (worker.isFinish(time)) {
					val step = worker.finish()
					finished.add(step)
					working.remove(step)
					val ready = step.nextSteps.filter { finished.containsAll(it.prevSteps) }
					available.addAll(ready)
				}
			}
			if (available.isNotEmpty()) {
				val sortedAvailable = available.sortedBy { it.name }.toMutableList()
				for (worker in workers) {
					if (!worker.isReady()) continue
					if (sortedAvailable.isEmpty()) break
					val step = sortedAvailable.removeFirst()
					worker.start(step, time, amount)
					available.remove(step)
					working.add(step)
				}
			}
			time++
		}
		return time - 1
	}

	val testInput = readInput("Day07_test")
	check(part1(testInput) == "CABDFE")
	check(part2(testInput, 2, 0) == 15)

	val input = readInput("Day07")
	part1(input).println()
	part2(input).println()
}
