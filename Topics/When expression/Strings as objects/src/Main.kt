fun main() {
    val input = readLine()!!
    // write code here
    if (input.isEmpty()) {
        println()
        return
    }
    when(input.first()) {
        'i' -> println(input.drop(1).toInt() + 1)
        's' -> println(input.drop(1).reversed())
        else -> println(input)
    }
}
