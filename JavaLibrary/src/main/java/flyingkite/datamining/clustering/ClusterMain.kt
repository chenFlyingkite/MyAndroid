package flyingkite.datamining.clustering

import flyingkite.datamining.clustering.algo.CLHistogram
import flyingkite.datamining.clustering.algo.ClusteringBySynchronization
import flyingkite.datamining.clustering.util.CLClus
import flyingkite.datamining.clustering.util.Clusterable
import flyingkite.log.L
import java.util.*

object ClusterMain {

    fun main(args : Array<String>) {
        var d = get_1D()
        d = get_1D()
        //L.log("set = $d")

        d = get_D2()
        L.log("set2 = $d")
        //runHist(d)
        L.log("set2 = $d")
        L.log("------")
        Collections.sort(d)
        //runHist(d)
        L.log("set2 = $d")

        L.log("--- Random ---")
        d = get_DR(50, 0, 10)
        runHist(d)
        L.log("orig set = $d")
        Collections.sort(d)
        L.log("sort set = $d")
    }

    private fun runHist(dataset: List<Data>) {
        val cs = CLHistogram<Data>()
        cs.setDataSet(dataset)
        cs.run()
    }

    private fun runSync(dataset: List<Data>) {
        val cs = ClusteringBySynchronization<Data>()
        cs.setDataSet(dataset)
        cs.run()
    }

    private fun get_1D(): List<Data> {
        val list = ArrayList<Data>()
        val s = arrayOf(1, 5, 3, 4, 2, 9, 10)
        for (i in s) {
        //for (i in 1..10) {
            val d = Data()
            d.x = i * 1.0
            list.add(d)
        }
        return list
    }

    private fun get_D2(): List<Data> {
        val list = ArrayList<Data>()
        val s = doubleArrayOf(1.05, 0.0, 1.1, 1.0, 1.2, 2.0, 1.3, 3.0, 1.4, 4.0, 1.5, 5.0, 1.6, 6.0)
        for (i in s) {
            //for (i in 1..10) {
            val d = Data(i)
            d.x = i
            list.add(d)
        }
        return list
    }


    private fun get_DR(size: Int, min: Int = 0, max: Int = 20): List<Data> {
        val list = ArrayList<Data>()
        val rm = Random()
        val rng = max - min
        for (i in 0 until size) {
            val r = min + rm.nextInt(rng)
            list.add(Data(r * 1.0))
        }
        return list
    }

    class Data(value: Double = 0.0) : Clusterable<Data>, CLClus<Data>, Comparable<Data> {
        var x = value
        override fun distance(y: Data): Double {
            return Math.abs(x - y.x)
        }

        override fun isSimilarTo(y: Data): Boolean {
            return distance(y) < 1
        }

        override fun toString(): String {
            return "$x"
        }

        override fun copy(): Data {
            return Data(x)
        }

        override fun getDimension(): Int {
            return 1
        }

        override fun getXi(i: Int): Double {
            return x
        }

        override fun setXi(i: Int, xi: Double) {
            x = xi
        }

        override fun compareTo(other: Data): Int {
            return when {
                x > other.x -> 1
                x < other.x -> -1
                else -> 0
            }
        }

    }

}