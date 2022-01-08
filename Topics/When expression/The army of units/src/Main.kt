fun main() {
    // write your code here
    val n = readLine()!!.toInt()
    println(
        when {
            n < 1 -> "no army"
            n in 1..4 -> "few"
            n in 5..9 -> "several"
            n in 10..19 -> "pack"
            n in 20..49 -> "lots"
            n in 50..99 -> "horde"
            n in 100..249 -> "throng"
            n in 250..499 -> "swarm"
            n in 500..999 -> "zounds"
            else -> "legion"
        }
    )
}
