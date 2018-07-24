package flyingkite.datamining.clustering.util

class IIPair(key: Int = 0, value: Int = 0) : Comparable<IIPair> {
    var k: Int = key
    var v: Int = value

    override fun compareTo(other: IIPair): Int {
        return when {
            v < other.v -> -1
            v > other.v -> 1
            k < other.k -> -1
            k > other.k -> 1
            else -> 0 // same
        }
    }

    override fun toString(): String {
        return "(%s, %s)".format(k, v)
    }
}