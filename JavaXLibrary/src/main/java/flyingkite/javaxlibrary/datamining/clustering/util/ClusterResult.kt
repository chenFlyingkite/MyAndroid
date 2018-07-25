package flyingkite.javaxlibrary.datamining.clustering.util

class ClusterResult(ids: List<Int> = ArrayList(), sizes: List<Int> = ArrayList()) {
    val clusterIds = ids
    val clusterSize = sizes

    override fun toString(): String {
        return "size = $clusterSize, clusterIds = $clusterIds"
    }
}