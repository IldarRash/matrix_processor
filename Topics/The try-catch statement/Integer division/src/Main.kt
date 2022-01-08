import java.lang.ArithmeticException
import java.lang.NumberFormatException

fun intDivision(x: String, y: String): Int {
    try {
        return x.toInt() / y.toInt()
    } catch (ex: NumberFormatException) {
        println("Read values are not integers.")
    }
    catch (ex: ArithmeticException) {
        println("Exception: division by zero!")
    }
    return 0
}

fun main() {
    val x = readLine()!!
    val y = readLine()!!
    println(intDivision(x, y))

}
