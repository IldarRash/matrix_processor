fun main() {
    // write your code here
    val s1 = readLine()!!
    val operand = readLine()!!
    val s2 = readLine()!!

    println(
        when(operand) {
            "equals" -> s1 == s2
            "plus" -> s1 + s2
            "endsWith" -> s1.endsWith(s2)
            else -> "Unknown operation"
        }
    )
}
