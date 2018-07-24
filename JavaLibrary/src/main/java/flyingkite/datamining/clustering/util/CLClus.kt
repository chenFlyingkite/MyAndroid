package flyingkite.datamining.clustering.util

interface CLClus<T> {
    fun distance(y: T): Double
    fun copy(): T
    fun isSimilarTo(y: T): Boolean
    fun getXi(i: Int): Double
}