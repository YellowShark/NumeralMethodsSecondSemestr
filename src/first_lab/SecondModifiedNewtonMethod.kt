package first_lab

import kotlin.math.*

class SecondModifiedNewtonMethod(
    private var x0: Float,
    private val eps: Float,
    private val k: Int,
    private val m: Int,
) {
    fun solve(callback: (Int, Int, Float, Float) -> Unit) {
        var e: Float
        var prevX = x0
        var currentX: Float
        var i = 0
        do {
            currentX = prevX - f(prevX) / f_(x0)
            if (abs(currentX) == Float.POSITIVE_INFINITY) {
                callback.invoke(3, 0, 0f, 0f)
                return
            }
            e = abs(currentX - prevX)
            prevX = currentX
            if (i % m == 0)
                x0 = currentX
            i++
        } while (e > eps && i <= k)
        val ier = if (e <= eps && i <= k) 0 else 1
        callback.invoke(ier, i, currentX, abs(f(currentX)))
    }

    private fun f(x: Float) = 2f

    private fun f_(x: Float) = 0f
}