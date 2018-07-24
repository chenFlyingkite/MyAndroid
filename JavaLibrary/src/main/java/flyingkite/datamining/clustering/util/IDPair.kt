package flyingkite.datamining.clustering.util

class IDPair(key: Int = 0, value: Double = 0.0) : Comparable<IDPair> {
    var k: Int = key
    var v: Double = value

    override fun compareTo(other: IDPair): Int {
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
    //https://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/types.html
//    Table 3-2 Java VM Type Signatures
//    Type Signature                  Java Type
//    Z                               boolean
//    B                               byte
//    C                               char
//    S                               short
//    I                               int
//    J                               long
//    F                               float
//    D                               double
//    L fully-qualified-class ;       fully-qualified-class
//    [ type                          type[]
//    ( arg-types ) ret-type          method type

}
