package flyingkite.library.java.math;

import java.math.BigInteger;

public class Math2 {

    public static BigInteger factorial(int n) {
        if (n < 0) return BigInteger.ZERO;

        BigInteger b = BigInteger.ONE;
        BigInteger x = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            x = x.add(BigInteger.ONE);
            b = b.multiply(x);
        }
        return b;
    }
}
