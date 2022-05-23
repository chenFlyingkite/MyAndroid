package flyingkite.library.javax.clustering.algo

import flyingkite.library.java.log.LF
import flyingkite.library.java.math.MathUtil
import flyingkite.library.java.tool.TicTac2
import flyingkite.library.javax.clustering.util.Clusterable
import flyingkite.library.javax.clustering.util.PngMaker
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ClusteringBySynchronization<T: Clusterable<T>> : Runnable {
    private val originalSet: MutableList<T> = ArrayList<T>()
    private var dataset: MutableList<T> = ArrayList() //mutableListOf()?
    private var newDataset: MutableList<T> = ArrayList() //mutableListOf()?
    private val folder = "datamining/clustering/sync"
    private val log = LF(folder)

    // The Kuramoto Model consists of a population of N coupled phase oscillators
    // where the phase of i-th unit, denoted by θ_i, evolves in time according to
    // the following dynamics :
    //  dθ_i           S    N
    // ------ = ω_i + ---   Σ  sin(θ_j - θ_i)
    //   dt            N   j=1
    // For i = 1, ..., N
    // ω_i := natural frequency
    // S := coupling strength between units
    // S = 0 -> independent
    // Global Order Parameter :=
    //           1    N
    // r^(iψ) = ---   Σ  e^(i*θ_j)
    //           N   j=1

    private var kNearNeighbor = 2
    private var epsilon = 2.0
    private var minimum = 0.0
    private var allRange = 1.0


    fun setDataSet(set: List<T>): ClusteringBySynchronization<T> {
        val x = ArrayList<T>()
        for (i in 0 until set.size) {
            x.add(set[i].copy())
        }
        copyData(originalSet, x)
        copyData(dataset, x)
        range()

        return this
    }

    private fun copyData(dst: MutableList<T>, src: MutableList<T>) {
        dst.clear()
        for (i in 0 until src.size) {
            dst.add(src[i].copy())
        }
    }

    private fun range() {
        var min = dataset[0].getXi(0)
        var max = dataset[0].getXi(0)
        for (i in 1 until dataset.size) {
            val x = dataset[i].getXi(0)
            if (x < min) {
                min = x
            }
            if (x > max) {
                max = x
            }
        }
        allRange = max - min
        minimum = min
    }

    override fun run() {
        log.file.open(false)
        //mClock.setLog(false)
        log.log("----------")
        //(min, current)
        var MDL = 0.0
        var KNN = 0
        var EPS = 0.0
        var info: ClusterInfo? = null
        val min = 2
        val max = MathUtil.maxs(dataset.size, min + 5, Math.floor(Math.log(dataset.size * 1.0)).toInt())
        for (k in min until max) {
            copyData(dataset, originalSet)
            val ek = getAvgKNNDistance(k)
            kNearNeighbor = k
            log.log("#$k: epsilon = $ek")
            epsilon = ek
            val c = dynamicalClustering()
            val mdl = MinimunDescriptionLength(c)

            val better = k == min || mdl < MDL

            if (better) {
                KNN = k
                MDL = mdl
                EPS = ek
                info = c
            }

            log.log("-----------")
            log.log("k = $k, eps = $ek, MDL = $mdl")
            if (info != null && info.clusterSize.size == 1) {
                break
            }
        }
        log.log("-----------")
        log.log("   Good")
        log.log("k = $KNN, eps = $EPS, MDL = $MDL")
        log.log("-----------")

        log.file.close()
    }

    // This is SynC's core algorithm
    private fun dynamicalClustering(): ClusterInfo {
        var done = false
        //log.log("t, cluster order = ${clusterOrderParameter()}")
        asPng("t", dataset, 0)
        var epo = 0
        mClock.tic()
        while (!done) {
            //log.log(" --- Epoch = $epo ---")
            mClock.tic()
            for (i in 0 until dataset.size) {
                val x = dataset[i].copy()
                val m = x.getDimension()
                for (j in 0 until m) {
                    updateX(x, j)
                }
                dataset[i] = x
            }
            mClock.tac(" Epoch = $epo")

            //log.log("old = $dataset")
            //log.log("new = $newDataset")

            dataset.clear()
            dataset.addAll(newDataset)
            newDataset.clear()
            val order = clusterOrderParameter();
            //log.log("t$epo, cluster order = $order")
            asPng("t$epo", dataset, 0)

            done = order > 0.999
            if (done) {
                break
            }
            epo++
        }
        mClock.tac("Clustering is done")
        val c = getClusters()
        log.log("cluster result -> $c");
        return getClusters()
    }

    private fun MinimunDescriptionLength(cinfo: ClusterInfo): Double {
        // Summation of L(D, M) = L(M) + L(D|M)
        // where, d-dimension data as
        // K clusters, C_i = i-th cluster in dataset, i=1..K
        //         K   |C_i|        N       K  p_i
        // L(M) =  Σ     Σ   log_2(---)  +  Σ  --- * log_2(|C_i|)
        //        i=1   j=1        C_i     i=1  2
        //
        //            K
        // L(D|M) = - Σ     Σ  log_2(pdf(x))
        //           i=1  x∈C_i
        //
        //            f^^(x)                1           d   1       x_i - y_i
        // pdf(x) = ----------- , f^^(x) = ---  *  Σ    Π  --- * K(-----------)
        //           Σ  f^^(y)              N     y∈D  i=1 h_i         h_i
        //          y∈D
        // p_i = dimension, h = 0.8*N^(-1/(d+4)), K(x) = (2π)^1/2 exp( -(x^2) /2)

        var LM = 0.0
        val K = cinfo.clusterSize.size
        val N = dataset.size
        for (i in 0 until K) {
            var LM_i = 0.0
            val si = cinfo.clusterSize[i]
            // L(M)_i -> p_i/2 * ln(C_i)
            LM_i += 0.5 * Math.log(si * 1.0)

            // L(M)_i -> ln(N / C_i) + ln(pdf(x))
            var s = 0.0
            var t = 0.0 // =  sum f^^(x)
            for (j in 0 until N) {
                val cid = cinfo.clusterIds[j]
                if (cid == i) {
                    // ln(N) - ln(C_i)
                    s += Math.log(N * 1.0) - Math.log(si * 1.0)
                    // + ln(f^^(x))
                    val fi = evalF_head(dataset[j])
                    t += fi
                    s += fi
                }
            }
            // - ln(sum f^^(x))
            s -= si * Math.log(t)
            LM_i += s
            //
            LM += LM_i
        }
        log.log("LM = $LM")
        return LM
    }

    // f^^(x)
    private fun evalF_head(x: T): Double {
        val N = dataset.size
        val d = x.getDimension()

        var fi = 0.0
        for (i in 0 until N) {
            val y = dataset[i]
            // Projection
            var H = 1.0
            for (k in 0 until d) {
                H *= evalKDE(x, y, k)
            }
            fi += H
        }
        return fi / N
    }

    private fun evalKDE(x: T, y: T, di: Int): Double {
        val N = dataset.size
        val d = x.getDimension()
        val hi = 0.9 * Math.pow(N * 1.0, -1.0/(d+4))
        val z = (x.getXi(di) - y.getXi(di)) / hi
        val one_sqtwoPi = 1.0 / Math.sqrt(2 * Math.PI)
        return one_sqtwoPi * Math.exp(- z * z / 2.0)
    }

    private fun getClusters(): ClusterInfo {
        val NO_ID = -1
        val clusterIds = ArrayList<Int>()
        val clusterSizes = ArrayList<Int>()
        val n = dataset.size
        for (i in 0 until n) {
            clusterIds.add(NO_ID)
        }

        var clusCount = -1
        for (i in 0 until n) {
            val x = dataset[i]
            val xic = clusterIds[i] // The cluster id of x
            if (xic == NO_ID) { // New cluster is found
                clusCount++

                // Set itself and its neighbors to be group clusCount
                val neighbor = epsilonNeighborhood(x)
                clusterSizes.add(neighbor.size)
                for (ni in neighbor) {
                    clusterIds[ni] = clusCount
                }
            }
        }

        return ClusterInfo(clusterIds, clusterSizes)
    }

    class ClusterInfo(cId: List<Int> = ArrayList(), cSize: List<Int> = ArrayList()) {
        val clusterIds = cId
        val clusterSize = cSize

        override fun toString(): String {
            return "size = $clusterSize, clusterIds = $clusterIds"
        }
    }

    //Eq. 6
    // Dimension = k
    private fun updateX(x: T, k: Int) {
        val neighbors = epsilonNeighborhood(x)
        val xt = x.getXi(k)
        var sum = 0.0
        for (ni in neighbors) {
            val yt = dataset[ni].getXi(k)
            sum += Math.sin(yt - xt)
        }

        val n = neighbors.size
        val xt1 = xt + sum / n
        // Add to new set
        val y = x.copy()
        y.setXi(k, xt1)
        newDataset.add(y)
        //x.setXi(k, xt1) // This is overwrite existing, may make clustering changes
    }

    // Eq. 7
    private fun clusterOrderParameter(): Double {
        var n = 0
        var sum = 0.0
        for (i in 0 until dataset.size) {
            val x = dataset[i]
            val neighbors = epsilonNeighborhood(x)
            n += neighbors.size

            // Sum up the global synchronization
            var s = 0.0
            for (ni in neighbors) {
                s += Math.exp(-x.distance(dataset[ni]))
            }
            sum += s
        }
        return sum / n
    }

    /**
     * As Definition 1: ε-neighborhood of an object x, Eq. 3.
     *
     * Returns the indices of dataset that in ε-neighborhood of x, thus
     *
     * For all i in neighbor, <code>dataset(i)</code> is in ε-neighborhood of x
     *
     * Time Complexity = Theta(N), x in ε := [Clusterable.distance] &le; ε
     *
     * http://mathworld.wolfram.com/Neighborhood.html
     * @param x The object x
     * @param noAdd if noAdd >= 0, noAdd will be in neighbor if x in ε
     *              if noAdd < 0, noAdd will not be in neighbor even if x in ε
     * @return neighbor
     */
    private fun epsilonNeighborhood(x: T, noAdd: Int = -1): List<Int> {
        val neighbor = ArrayList<Int>()
        for (i in 0 until dataset.size) {
            val y = dataset[i]
            if (x.distance(y) <= epsilon) {
                var add = true
                if (0 <= noAdd && noAdd == i) {
                    add = false // Omit index
                }
                if (add) {
                    neighbor.add(i)
                }
            }
        }
        return neighbor
    }

    private fun getAvgKNNDistance(k: Int): Double {
        var n = 0
        var sum = 0.0
        for (i in 0 until dataset.size) {
            val x = dataset[i]
            val knnData = KNearestNeighborhood(x, i, k)
            n += knnData.size
            for (dk in knnData) {
                sum += dk.v
            }
        }
        return sum / n
    }

    // xIndex : The index of object x, we will exclude it in result list
    private fun KNearestNeighborhood(x: T, xIndex: Int, k: Int = 3): List<DataK> {
        val knnIndices = ArrayList<DataK>()
        for (i in 0 until dataset.size) {
            if (i != xIndex) {
                //knnIndices.add(null)
                val y = dataset[i]
                val dist = x.distance(y)
                val p = DataK(i, dist)
                //val n = Collections.binarySearch(knnIndices, p, compareBy({it.v}, {it.k}))
                val n = Collections.binarySearch(knnIndices, p)

                if (n < 0) {
                    knnIndices.add(-n-1, p)
                }
                if (knnIndices.size > k) {
                    knnIndices.removeAt(k)
                }
            }
        }
        return knnIndices
    }

    class DataK(i: Int = 0, dist: Double = 0.0): Comparable<DataK> {
        var k: Int = i
        var v: Double = dist

        override fun compareTo(other: DataK): Int {
            return when {
                v < other.v -> -1
                v > other.v -> 1
                k < other.k -> -1
                k > other.k -> 1
                else -> 0 // same
            }
        }

        override fun toString(): String {
            return "(%s, %.3f)".format(k, v)
        }
    }

    private val mClock = TicTac2()

    // Dimension k
    private fun asPng(name: String, dataset: List<T>, k: Int) {
        val png = PngMaker._png(name)
        val w = 1200
        val h = 300
        val dstImg = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g = dstImg.graphics
        // Step : Copy image
        //mClock.tic()
        for (i in 0 until dataset.size) {
            g.color = PngMaker.getColorModed(i)
            val x = dataset[i].getXi(k)
            val dx = 1000 * (x - minimum) / allRange
            val wi = 100 + Math.round(dx).toInt()
            val hi = 100
            g.fillRect(wi, hi, 10, 100)
            g.drawString(x.toString().substring(0, 3), wi, hi)
        }
        //mClock.tac("$allRange draw $png")
        PngMaker.into(dstImg, File("$folder/img_${kNearNeighbor}", png))
    }
}