package flyingkite.library.javax.clustering.util

interface Clusterable<T> {
    fun distance(y: T): Double
    fun copy(): T
    fun getDimension(): Int
    fun getXi(i: Int): Double
    fun setXi(i: Int, xi: Double)
}