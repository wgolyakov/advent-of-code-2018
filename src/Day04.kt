import java.text.SimpleDateFormat
import java.util.Date

fun main() {
	class Event(val time: Date, val guard: Int?, val asleep: Boolean)

	fun parse(input: List<String>): List<Event> {
		val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
		val events = input.map { line ->
			val (time, record) = line.drop(1).split("] ")
			val t = format.parse(time)
			val guard = if (record.startsWith("Guard"))
				record.substringAfter("Guard #").substringBefore(" begins").toInt() else null
			val asleep = record == "falls asleep"
			Event(t, guard, asleep)
		}
		return events.sortedBy { it.time }
	}

	fun mostSleepGuard(events: List<Event>): Int {
		val sleeps = mutableMapOf<Int, Int>()
		var guard = -1
		var sleepBegin = Date()
		for (event in events) {
			if (event.guard != null) {
				guard = event.guard
			} else if (event.asleep)  {
				sleepBegin = event.time
			} else {
				val sleep = event.time.minutes - sleepBegin.minutes
				sleeps[guard] = (sleeps[guard] ?: 0) + sleep
			}
		}
		return sleeps.maxBy { it.value }.key
	}

	fun mostSleepMinute(events: List<Event>, sleepGuard: Int): Int {
		val sleepMinutes = mutableMapOf<Int, Int>()
		var guard = -1
		var sleepBegin = Date()
		for (event in events) {
			if (event.guard != null) {
				guard = event.guard
			} else if (event.asleep)  {
				sleepBegin = event.time
			} else {
				if (guard != sleepGuard) continue
				for (minute in sleepBegin.minutes until event.time.minutes) {
					sleepMinutes[minute] = (sleepMinutes[minute] ?: 0) + 1
				}
			}
		}
		return sleepMinutes.maxBy { it.value }.key
	}

	fun allGuardSleepMinutes(events: List<Event>): Array<MutableMap<Int, Int>> {
		val sleeps = Array(60) { mutableMapOf<Int, Int>() }
		var guard = -1
		var sleepBegin = Date()
		for (event in events) {
			if (event.guard != null) {
				guard = event.guard
			} else if (event.asleep)  {
				sleepBegin = event.time
			} else {
				for (minute in sleepBegin.minutes until event.time.minutes) {
					val sleepGuards = sleeps[minute]
					sleepGuards[guard] = (sleepGuards[guard] ?: 0) + 1
				}
			}
		}
		return sleeps
	}

	fun part1(input: List<String>): Int {
		val events = parse(input)
		val guard = mostSleepGuard(events)
		val minute = mostSleepMinute(events, guard)
		return guard * minute
	}

	fun part2(input: List<String>): Int {
		val events = parse(input)
		val sleeps = allGuardSleepMinutes(events)
		val minute = sleeps.withIndex().maxBy { (_, v) -> v.maxOfOrNull { it.value } ?: 0 }.index
		val guard = sleeps[minute].maxBy { it.value }.key
		return guard * minute
	}

	val testInput = readInput("Day04_test")
	check(part1(testInput) == 240)
	check(part2(testInput) == 4455)

	val input = readInput("Day04")
	part1(input).println()
	part2(input).println()
}
