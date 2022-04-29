package second_lab

import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

private const val INPUT_FILE = "input.txt"
private const val OUTPUT_FILE = "output.txt"

fun main() {
    val y0: Double
    val x0: Double
    val c: Double
    var yc: Double
    var h0: RefObject<Double>
    val h_min: Double
    val h_max: Double
    val E_max: Double
    val E = RefObject(0.0)
    var y_temp = 0.0
    val h1: RefObject<Double> = RefObject(0.0)
    var Ch: Double
    val flag = RefObject(false)
    var count_h_min = 0
    var count_h_max = 0
    var count_y = 0
    var count_wrong = 0

    val scanner = Scanner(File(INPUT_FILE))

    x0 = scanner.nextDouble()
    y0 = scanner.nextDouble()
    c = scanner.nextDouble()
    yc = scanner.nextDouble()
    h_min = scanner.nextDouble()
    h_max = scanner.nextDouble()
    E_max = scanner.nextDouble()
    h0 = RefObject((y0 - x0) / 10)

    File(OUTPUT_FILE).bufferedWriter().use { out ->
        if (!validInput(x0, y0, c, h0)) {
            out.write("Icod=2! Ошибка входных данных!")
            return
        }

        out.write("x\t\t\t\ty\t\t\t\tE\t\t\t\th\n")
        out.write("$c\t\t\t\t$yc\n")

        var xi = c + h0.argValue
        E.argValue = epsN(xi, y_temp, h0.argValue)
        if (E.argValue >= E_max) {
            h0.argValue = makeNeededStep(xi, yc, h0, E, E_max)
        }
        y_temp = Y1(xi, yc, h0.argValue)

        out.write(
            "${xi.round()}\t\t\t\t${y_temp.round()}\t\t\t\t${E.argValue.round()}\t\t\t\t${
                h0.argValue.round()
            }\n"
        )

        yc = y_temp
        Ch = y0 - (xi + h0.argValue)
        if (h0.argValue < 0) {
            Ch = -1 * (y0 - y0 - (xi + h0.argValue))
        }
        while (Ch >= h_min) {
            E.argValue = epsN(xi, yc, h0.argValue)
            h0.argValue = makeNeededStep(xi, yc, h0, E, E_max)
            if (abs(h0.argValue) < h_min) {
                count_wrong++
            }
            if (h0.argValue < 0) {
                h0.argValue = -1 * checkH0(h_min, h_max, -h0.argValue)
            } else {
                h0.argValue = checkH0(h_min, h_max, h0.argValue)
            }
            if (h0.argValue == h_min) {
                count_h_min++
            }
            if (h0.argValue == h_max) {
                count_h_max++
            }
            y_temp = Y1(xi, yc, h0.argValue)
            count_y++
            E.argValue = epsN(xi, y_temp, h0.argValue)
            xi += h0.argValue
            yc = y_temp
            out.write(
                "${xi.round()}\t\t\t\t${y_temp.round()}\t\t\t\t${E.argValue.round()}\t\t\t\t${
                    h0.argValue.round()
                }\n"
            )
            Ch -= abs(h0.argValue)
        }
        xi = if (h0.argValue < 0) {
            -1 * checkOnMax(x0, h_min, -1 * xi, h1, flag)
        } else {
            checkOnMax(y0, h_min, xi, h1, flag)
        }
        if (flag.argValue) {
            y_temp = Y1(xi, yc, h0.argValue)
            count_y++
            E.argValue = epsN(xi, yc, h0.argValue)
            out.write(
                "${xi.round()}\t\t\t\t${y_temp.round()}\t\t\t\t${E.argValue.round()}\t\t\t\t${
                    h0.argValue.round()
                }\n"
            )
            if (h0.argValue == h_min) {
                count_h_min++
            }
            if (h0.argValue == h_max) {
                count_h_max++
            }
            if (E.argValue >= E_max || abs(h1.argValue) < h_min) {
                count_wrong++
            }
            h0 = h1
            yc = y_temp
            xi += h0.argValue
            y_temp = Y1(xi, yc, h0.argValue)
            count_y++
            E.argValue = epsN(xi, y_temp, h0.argValue)
            out.write(
                "${xi.round()}\t\t\t\t${y_temp.round()}\t\t\t\t${E.argValue.round()}\t\t\t\t${
                    h0.argValue.round()
                }\n"
            )
            if (h0.argValue == h_min) {
                count_h_min++
            }
            if (h0.argValue == h_max) {
                count_h_max++
            }
            if (E.argValue >= E_max || Math.abs(h0.argValue) < h_min) {
                count_wrong++
            }
        } else {
            y_temp = Y1(xi, yc, h0.argValue)
            count_y++
            E.argValue = epsN(xi, y_temp, h0.argValue)
            out.write(
                "${xi.round()}\t\t\t\t${y_temp.round()}\t\t\t\t${E.argValue.round()}\t\t\t\t${
                    h0.argValue.round()
                }\n"
            )
            if (h0.argValue == h_min) {
                count_h_min++
            }
            if (h0.argValue == h_max) {
                count_h_max++
            }
            if (E.argValue >= E_max || abs(h0.argValue) < h_min) {
                count_wrong++
            }
        }
        if (count_wrong == count_y) {
            out.write("Icod=1! Требуемая точность не достигнута!\n")
        } else {
            out.write("Icod=0! Ошибок нет!\n")
        }

        out.write("\nЧисло точек интегрирования: $count_y\n")
        out.write("Число точек, в которых не достигается заданная точность: $count_wrong\n")
        out.write("Общее количество минимальных шагов интегрирования: $count_h_min\n")
        out.write("Общее количество максимальных шагов интегрирования: $count_h_max\n")
    }
}

private fun f(x: Double, y: Double): Double {
    return 32 * x.pow(3)
}

private fun Y1(x0: Double, y0: Double, h: Double): Double {
    val K1 = f(x0, y0)
    val K2 = f(x0 + 0.33 * h, y0 + 0.33 * K1)
    val K3 = f(x0 + 0.66 * h, y0 + 0.66 * K2)
    val res = y0 + 1.0 / 4.0 * (K1 + 3 * K3)
    return res
}

private fun epsN(x0: Double, y0: Double, h: Double): Double {
    val K1 = f(x0, y0)
    val K2 = f(x0 + 0.33 * h, y0 + 0.33 * K1)
    val K3 = f(x0 + 0.66 * h, y0 - 0.33 * K1 + K2)
    val K4 = f(x0 + h, y0 + K1 - K2 + K3)
    val res = abs((K1 + 2 * K3 + K4 - 4 * K2) / 6)
    return res
}

private fun checkH0(h_min: Double, h_max: Double, h0: Double): Double {
    var h0 = h0
    if (h0 <= h_min) {
        h0 = h_min
    } else {
        if (h0 > h_max) {
            h0 = h_max
        }
    }
    return h0
}

private fun checkOnMax(
    B: Double,
    h_min: Double,
    xn: Double,
    h0: RefObject<Double>,
    flag: RefObject<Boolean>
): Double {
    var h_min = h_min
    var xn = xn
    val a: Int = (2 * h_min).toInt()
    val b: Int = (B - xn).toInt()
    val c: Int = (1.5 * h_min).toInt()
    if (b >= a) {
        xn = B - h_min
        if (xn < 0) {
            h_min = -h_min
        }
        h0.argValue = h_min
        flag.argValue = true
    } else {
        if (b <= c) {
            xn = B
        } else {
            if (c < b && b < a) {
                xn += b / 2
                h0.argValue = b / 2.0
                flag.argValue = true
            }
        }
    }
    return xn
}

private fun makeNeededStep(
    xn: Double,
    yn: Double,
    h0: RefObject<Double>,
    E: RefObject<Double>,
    E_max: Double
): Double {
    var yn = yn
    var A: Double
    if (E.argValue == 0.0) {
        A = 2.0
        h0.argValue *= A
    } else {
        if (E.argValue > E_max) {
            while (E.argValue > E_max) {
                A = (E_max / E.argValue).pow(1 / 3.0)
                h0.argValue *= A
                yn = Y1(xn, yn, h0.argValue)
                E.argValue = epsN(xn, yn, h0.argValue)
                if (A == 1.0) {
                    return h0.argValue
                }
            }
        } else {
            while (E.argValue < E_max) {
                A = (E_max / E.argValue).pow(1 / 3.0)
                h0.argValue *= A
                yn = Y1(xn, yn, h0.argValue)
                E.argValue = epsN(xn, yn, h0.argValue)
                if (A == 1.0) {
                    return h0.argValue
                }
            }
        }
        if (E.argValue == E_max) {
            A = (E_max / E.argValue).pow(1 / 3.0)
            h0.argValue *= A
        }
    }
    return h0.argValue
}

private fun validInput(a: Double, b: Double, c: Double, h: RefObject<Double>): Boolean {
    var flag = true
    if (a >= b) {
        flag = false
    }
    if (b == c) {
        h.argValue = -h.argValue
    }
    return flag
}

internal class RefObject<T>(var argValue: T)

fun Double.round(): Double {
    var multiplier = 1.0
    repeat(3) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}