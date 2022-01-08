package processor

import processor.Matrix.Companion.initFromConsole
import java.text.DecimalFormat
import kotlin.math.pow

/*
                        2.65 3.54 3.88 8.99
                        3.12 5.45 7.77 5.56
                        5.31 2.23 2.33 9.81
                        1.67 1.67 1.01 9.99


                        0.396796 -0.214938 0.276735 -0.5092,
                        5.19655 -2.06983 -0.388886 -3.14252,
                        -3.3797 1.50219 0.159794 2.04842,
                        -0.593332 0.230065 0.00259267 0.50345
 */

data class Matrix(val rowsCount: Int = 0, val columnCount: Int = 0, val matrix: Array<Array<Double>>) {

    fun print() {
        val format = DecimalFormat("#.#####")
        format.isDecimalSeparatorAlwaysShown = false
        matrix
            .map {
                it
                    .map(format::format)
            }
            .map {
                it.joinToString(separator = " ")
            }
            .forEach { println(it) }
        println()
    }

    companion object {
        fun initFromConsole(n: Int, m: Int, elems: Array<List<String>>): Matrix {
            val matrix = Array(n) { Array(m) { 0.0 } }

            for (i in 0 until n) {
                for (j in 0 until m) {
                    matrix[i][j] = elems[i][j].toDouble()
                }
            }

            return Matrix(n, m, matrix)
        }
    }
}

fun Matrix.multi(other: Matrix): Result {
    if (columnCount != other.rowsCount) {

        return Result(true, null, Matrix(matrix = arrayOf(arrayOf())))
    }
    val resultMat = Array(rowsCount) { Array(other.columnCount) { 0.0 } }
    for (i in 0 until rowsCount) {
        for (j in 0 until other.columnCount) {
            for (k in 0 until columnCount) {
                resultMat[i][j] += matrix[i][k] * other.matrix[k][j]
            }
        }
    }

    return Result(false, null, Matrix(rowsCount, other.columnCount, resultMat))
}

fun Matrix.multiConstant(constant: Double) =
    Result(
        false, null, Matrix(
            rowsCount,
            columnCount,
            matrix.map {
                it.map { elem -> elem * constant }.toTypedArray()
            }.toTypedArray()
        )
    )

operator fun Matrix.plus(other: Matrix): Result {
    if (other.rowsCount != this.rowsCount || other.columnCount != this.columnCount)
        return Result(true, null, null)

    val resultMat = Array(this.rowsCount) { Array(this.columnCount) { it.toDouble() } }
    for (i in 0 until rowsCount) {
        for (j in 0 until columnCount) {
            resultMat[i][j] = matrix.get(i)[j] + other.matrix[i][j]
        }
    }

    return Result(false, null, Matrix(rowsCount, columnCount, resultMat))
}

fun Matrix.transposMain(): Result {
    val resultMat = Array(this.columnCount) { Array(this.rowsCount) { it.toDouble() } }
    for (x in 0 until columnCount) {
        for (y in 0 until rowsCount) {
            resultMat[x][y] = matrix[y][x]
        }
    }

    return Result(false, null, Matrix(columnCount, rowsCount, resultMat))
}

fun Matrix.transposVertical(): Result {
    val resultMat = Array(this.rowsCount) { Array(this.columnCount) { it.toDouble() } }
    for (x in 0 until rowsCount) {
        for (y in 0 until columnCount) {
            resultMat[x][y] = matrix[x][columnCount - 1 - y]
        }
    }
    return Result(false, null, Matrix(rowsCount, columnCount, resultMat))
}

fun Matrix.transposHorizontal(): Result {
    val resultMat = Array(this.rowsCount) { Array(this.columnCount) { it.toDouble() } }
    for (x in 0 until rowsCount) {
        resultMat[x] = matrix[rowsCount - 1 - x]
    }
    return Result(false, null, Matrix(rowsCount, columnCount, resultMat))
}

fun Matrix.determinant(): Double {
    fun calculate(matrix: Array<Array<Double>>): Double {
        if (matrix.size == 2) return matrix[1][1] * matrix[0][0] - matrix[1][0] * matrix[0][1]
        return matrix.first().mapIndexed { i, value ->
            value * (-1.0).pow(i) * calculate(getMinorMatrix(matrix, i))
        }.sum()
    }

    return if (matrix.size == 1) matrix.first().first() else calculate(matrix)
}

fun getMinorMatrix(matrix: Array<Array<Double>>, i: Int): Array<Array<Double>> {
    return matrix.drop(1).map { row -> row.filterIndexed { index, _ -> index != i }.toTypedArray() }.toTypedArray()
}

fun getSubMatrix(matrix: Array<Array<Double>>, i: Int, j: Int) = matrix.filterIndexed { index, _ -> index != i }
    .map { row -> row.filterIndexed { index, _ -> index != j }.toTypedArray() }.toTypedArray()


fun Matrix.inverse(): Result {
    fun cofactor(indexRow: Int, indexColumn: Int) : Int {
        if ((indexRow % 2 == 0 && indexColumn % 2 != 0) || (indexRow % 2 != 0 && indexColumn % 2 == 0)) {
            return -1
        }
        return 1;
    }




    fun getMinorMatrix(): Matrix {
        val temp = Array(this.rowsCount) { Array(this.columnCount) { it.toDouble() } }
        for (i in 0 until rowsCount) {
            for (j in 0 until columnCount) {
                temp[i][j] = cofactor(i, j) * Matrix(rowsCount - 1, columnCount - 1, getSubMatrix(this.matrix, i, j)).determinant()
            }
        }
        return Matrix(rowsCount, columnCount, temp)
    }

    val det = this.determinant()
    if (det == 0.0)
        return Result(true, "This matrix doesn't have an inverse.", null)

    val minorMat = getMinorMatrix()
    return Result(false, null, minorMat.transposMain().matrix!!.multiConstant(1 / det).matrix!!)
}


fun Matrix.transposSide(): Result {
    val resultMat = Array(this.columnCount) { Array(this.rowsCount) { it.toDouble() } }
    for (x in 0 until columnCount) {
        for (y in 0 until rowsCount) {
            resultMat[x][y] = matrix[rowsCount - 1 - y][columnCount - 1 - x];
        }
    }

    return Result(false, null, Matrix(columnCount, rowsCount, resultMat))
}

data class Result(val error: Boolean, val operationError: String?, val matrix: Matrix?)

object Console {
    fun getMatrix(matrixNumber: String): Result {
        print("Enter size of $matrixNumber matrix: ")
        val sizes = readLine()!!.trim().split(" ")

        val n = sizes[0].toInt()
        val m = sizes[1].toInt()

        println("Enter $matrixNumber matrix: ")
        val list: Array<List<String>> = Array(n) { mutableListOf() }
        for (i in 0 until n) {
            val row = readLine()!!.split(" ")
            list[i] = row
        }

        return Result(false, null, initFromConsole(n, m, list))
    }

    fun getConstant(): Double {
        print("Enter constant: ")
        return readLine()!!.toDouble()
    }

    fun printResult(result: Result) =
        when {
            !result.error -> {
                println("The result is:")
                result.matrix!!.print()
            }
            result.operationError != null -> println(result.operationError)
            else -> println("The operation cannot be performed.")
        }

    fun printConstant(result: Double) = println(result)
}

fun main() {

    while (true) {
        println(
            "1. Add matrices\n" +
                    "2. Multiply matrix by a constant\n" +
                    "3. Multiply matrices\n" +
                    "4. Transpose matrix\n" +
                    "5. Calculate a determinant\n" +
                    "6. Inverse matrix\n" +
                    "0. Exit"
        )
        print("Your choice: ")
        when (readLine()!!.trim().toInt()) {
            1 -> addMatrix()
            2 -> multiplyByConstant()
            3 -> multiplyMatrix()
            4 -> transpose()
            5 -> determinat()
            6 -> inverse()
            0 -> System.exit(0)
            else -> println("Try another number")
        }
        println()
    }
}

fun transpose() {
    println(
        "1. Main diagonal\n" +
                "2. Side diagonal\n" +
                "3. Vertical line\n" +
                "4. Horizontal line"
    )
    print("Your choice: ")

    when (readLine()!!.trim().toInt()) {
        1 -> transposMain()
        2 -> transposSide()
        3 -> transposVertical()
        4 -> transposHorizontal()
        0 -> return
    }
}

fun inverse() {
    val readMatrix = Console.getMatrix("").matrix!!
    Console.printResult(readMatrix.inverse())
}

fun determinat() {
    val readMatrix = Console.getMatrix("").matrix!!
    Console.printConstant(readMatrix.determinant())
}

fun transposHorizontal() {
    val readMatrix = Console.getMatrix("").matrix!!
    Console.printResult(readMatrix.transposHorizontal())
}

fun transposVertical() {
    val readMatrix = Console.getMatrix("").matrix!!
    Console.printResult(readMatrix.transposVertical())
}

fun transposSide() {
    val readMatrix = Console.getMatrix("").matrix!!
    Console.printResult(readMatrix.transposSide())
}

fun transposMain() {
    val readMatrix = Console.getMatrix("").matrix!!
    Console.printResult(readMatrix.transposMain())
}

fun multiplyByConstant() {
    val readMatrix = Console.getMatrix("").matrix!!
    val constant = Console.getConstant()

    Console.printResult(readMatrix.multiConstant(constant))
}

fun addMatrix() {
    val readMatrix1 = Console.getMatrix("first").matrix!!
    val readMatrix2 = Console.getMatrix("second").matrix!!

    Console.printResult(readMatrix1 + readMatrix2)
}

fun multiplyMatrix() {
    val readMatrix1 = Console.getMatrix("first").matrix!!
    val readMatrix2 = Console.getMatrix("second").matrix!!

    Console.printResult(readMatrix1.multi(readMatrix2))
}

