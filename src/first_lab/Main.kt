package first_lab

import java.io.File
import java.util.*

const val INPUT_FILE = "in.txt"
const val OUTPUT_FILE = "out.txt"

fun main() {
    val scanner = Scanner(File(INPUT_FILE))
    val x0 = scanner.nextFloat()
    val eps = scanner.nextFloat()
    val k = scanner.nextInt()
    SecondModifiedNewtonMethod(x0 = x0, eps = eps, k = k, m = 10).run {
        solve { ier, l, xx, fxx ->
            if (ier == 0) {
                File(OUTPUT_FILE).bufferedWriter().use { out ->
                    out.write("IER = $ier.\n")
                    out.write("Число итераций = $l.\n")
                    out.write("Найденное решение = $xx.\n")
                    out.write("f(xx) = $fxx.\n")
                }
                print("IER = $ier.\n")
                print("Число итераций = $l.\n")
                print("Найденное решение xx = $xx.\n")
                print("|f(xx)| = $fxx.\n")
            } else {
                File(OUTPUT_FILE).bufferedWriter().use { out ->
                    out.write("IER = $ier.\n")
                }
                print("IER = $ier.\n")
            }
        }
    }
}