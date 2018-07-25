package flyingkite.datamining.clustering.algo

import flyingkite.datamining.clustering.util.CLClus
import flyingkite.datamining.clustering.util.IDPair
import flyingkite.log.L
import flyingkite.math.Statistics
import java.util.*

class CLHistogram<T: CLClus<T>> : Runnable {
    private val dataset = ArrayList<T>()

    private val folder = "datamining/clustering/CLhis"
    private val log = L.getImpl()//LF(folder)

    private val NO_ID = -1

    fun setDataSet(set: List<T>): CLHistogram<T> {
        copy(set, dataset)

        return this
    }

    private fun copy(set: List<T>, list: MutableList<T>): List<T> {
        list.clear()
        for (i in 0 until set.size) {
            list.add(set[i].copy())
        }
        return list
    }
    //private val result = ClusterResult()

    private fun setData() {

    }

    override fun run() {
        //log.file.open(false)
        //log.setLogToL(false)
        val groupIds = ArrayList<Int>()
        var groupCount = NO_ID
        var groupI: Int
        var subdata: List<T>
        val preKGroups = ArrayList<Int>()
        for (i in 0 until dataset.size) {
            subdata = dataset.subList(0, i)
            preKGroups.clear()
            val x = dataset[i]
            val neighbor = getKNN(x, i, 20, subdata)
            log.log("--> #$i : x = $x")

            if (neighbor.size == 0) {
                groupCount++
                groupI = groupCount
            } else {
                // Take the list's ids out
                val ids = ArrayList<Int>()
                for (id in neighbor) {
                    ids.add(groupIds[id.k])
                }
                val modes = Statistics.modeIndices(ids)
                log.log("    ids = $ids, modeIndices = $modes")
                groupI = ids[modes[0]] // First modeIndices's group number
            }
            groupIds.add(groupI)
            log.log("  in g# = $groupI, NN = $neighbor")
            log.log("  => groupIds = $groupIds")
        }

        //log.setLogToL(true)
        // init
        val grouping = ArrayList<ArrayList<T>>()
        for (i in 0..groupCount) {
            grouping.add(ArrayList())
        }
        for (i in 0 until dataset.size) {
            val gi = grouping[groupIds[i]]
            gi.add(dataset[i])
        }
        log.log("--- Group done with ----")
        for (i in 0..groupCount) {
            // Statistic
            val list = ArrayList<Double>()
            for (d in grouping[i]) {
                list.add(d.getXi(0))
            }
            // Common logging
            val mM = "(m, M) = (%02.1f, %2.1f)".format(Statistics.min(list), Statistics.max(list))
            val us = "(u, s) = (%2.2f, %2.2f)".format(Statistics.mean(list), Statistics.deviation(list))
            log.log("#$i => $mM, $us, ${grouping[i]}")
        }
        log.log("--- End ----")

        //log.file.close()
    }

    /**
     * The incremental grouping method
     * @param x New data
     * @param sublist Previous dataset that x will join (so x is not yet in sublist)
     * @param groupIds Previous dataset's groupIds, with data_i is in group of groupIds(i)
     */
    fun findGroup(x: T, sublist: List<T>, groupIds: List<Int>, algo_K: Int): Int {
        val i = sublist.size // regard as last new item

        if (sublist.isEmpty() && groupIds.isEmpty()) return 0

        // Find the k-nearest-neighbor of x in dataset[0:i]
        val neighbor = getKNN(x, i, algo_K, sublist)
        //log("--> #%s : x = %s", i, x)

        val groupI: Int
        if (neighbor.isEmpty()) {
            // No one is nearby to x, -> create the new group
            groupI = 1 + Statistics.max(groupIds) as Int
        } else {
            // Take the list's ids out
            val ids = ArrayList<Int>()
            for (id in neighbor) {
                ids.add(groupIds[id.k])
            }
            // Get the mode of group number
            val modes = Statistics.modeIndices(ids)
            //log("    ids = %s, mode = %s", ids, modes)
            groupI = ids[modes[0]] // Assign to 1st mode group number
        }
        return groupI
    }

    private fun getKNN(x: T, xIndex: Int, k: Int, set: List<T>): List<IDPair> {
        val indices = ArrayList<IDPair>()
        for (i in 0 until set.size) {
            // Omit index of x itself
            if (i == xIndex) continue

            val y = set[i]
            if (x.isSimilarTo(y)) {
                val dist = x.distance(y)
                val p = IDPair(i, dist)
                //val n = Collections.binarySearch(knnIndices, p, compareBy({it.v}, {it.k}))
                val n = Collections.binarySearch(indices, p)

                if (n < 0) {
                    indices.add(-n-1, p)
                }
                if (indices.size > k) {
                    indices.removeAt(k)
                }
            }
        }
        return indices
    }


}