package com.volodya

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartFrame
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import javax.swing.JFrame
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

val rOut = 0.17
val rIn = 0.08
val r = rOut - rIn
val l = 0.25
val vx = 1.5 * 1e6
val q = 1.6 * 1e-19
val m = 9.1 * 1e-31
var dt = 1e-12
val approx = 300

fun U(h: Double): Double {
    return (2 * h * m * ln(rOut / rIn) * (rIn + (rOut - r) / 2) * (vx.pow(2))) / (q * (l.pow(2)))
}

fun trySimulate(u: Double): Boolean {
    var x = 0.0
    var y = r / 2
    val a = ((q * u) / (m * ln(rOut / rIn)))
    var vy = 0.0
    var t = 0.0
    while (y > 0 && x < l) {
        x += vx * dt
        vy += (a / (y + rIn)) * dt
        y -= vy * dt
        t += dt
    }
    return y <= 0 && x < l
}

fun findU(): Double {
    var uMin = 0.0
    var uMax = U(rIn + r / 2)
    var u = (uMax + uMin) / 2

    for (i in 0..approx) {
        if (trySimulate(u)) {
            uMax = u
            u -= ((u - uMin) / 2)
        } else {
            uMin = u
            u += ((uMax - u) / 2)
        }
    }

    return u
}

fun calcT(y: Double, u: Double): Double {
    return sqrt((2 * (y + rIn) * m * ln(rOut / rIn) * y) / (q * u))
}

fun graphSim() {
    val seriesYX = XYSeries("y(x)")
    val seriesVT = XYSeries("v(t)")
    val seriesAT = XYSeries("a(t)")
    val seriesYT = XYSeries("y(t)")

    val u = findU()

    var x = 0.0
    var y = r / 2
    val a = ((q * u) / (m * ln(rOut / rIn)))
    var vy = 0.0
    var t = 0.0

    seriesYX.add(x, y)
    seriesVT.add(t, vy)
    seriesAT.add(t, (a / (y + rIn)))
    seriesYT.add(t, y)

    while (y >= 0 && x < l) {
        if (y - (vy + (a / (y + rIn)) * dt) * dt < 0) {
            dt = calcT(y, u)
        }
        x += vx * dt
        vy += (a / (y + rIn)) * dt
        y -= vy * dt
        t += dt

        seriesYX.add(x, y)
        seriesVT.add(t, vy)
        seriesAT.add(t, (a / (y + rIn)))
        seriesYT.add(t, y)
    }

    println("u = $u В\nt = $t c\nv = $vy м/c")

    val yxCollection = XYSeriesCollection(seriesYX)
    val vtCollection = XYSeriesCollection(seriesVT)
    val atCollection = XYSeriesCollection(seriesAT)
    val ytCollection = XYSeriesCollection(seriesYT)

    val chart1 = ChartFactory.createXYLineChart("График y(x)", "x, м", "y, м", yxCollection)
    val frame1 = ChartFrame("График y(x)", chart1)
    frame1.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame1.setSize(666, 500)
    frame1.isVisible = true

    val chart2 = ChartFactory.createXYLineChart("График v_y(t)", "t, с", "v, м/с", vtCollection)
    val frame2 = ChartFrame("График v(t)", chart2)
    frame2.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame2.setSize(666, 500)
    frame2.isVisible = true

    val chart3 = ChartFactory.createXYLineChart("График a_y(t)", "t, с", "a, м/с²", atCollection)
    val frame3 = ChartFrame("График a(t)", chart3)
    frame3.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame3.setSize(666, 500)
    frame3.isVisible = true

    val chart4 = ChartFactory.createXYLineChart("График y(t)", "t, с", "y, м", ytCollection)
    val frame4 = ChartFrame("График y(t)", chart4)
    frame4.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame4.setSize(666, 500)
    frame4.isVisible = true
}

fun main() {
    graphSim()
}