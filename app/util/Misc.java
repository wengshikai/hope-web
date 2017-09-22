package util;

import java.math.BigDecimal;

/**
 * Created by shanmao on 15-12-26.
 */
public class Misc {
    public static double formatDoubleForMoney(double d){
        BigDecimal b   =   new BigDecimal(d);
        return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
