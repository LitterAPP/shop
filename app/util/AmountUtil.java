package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;

import jws.Logger;

public class AmountUtil {

    public static int y2f(String y) {
        if (StringUtils.isEmpty(y)) {
            return 0;
        }
        BigDecimal percent = new BigDecimal("100");
        BigDecimal totalAmount = new BigDecimal(y);
        BigDecimal selfAmount = totalAmount.multiply(percent);
        return selfAmount.intValue();
    }

    public static int y2f(double y) {
        BigDecimal percent = new BigDecimal("100");
        BigDecimal totalAmount = new BigDecimal(String.valueOf(y));
        BigDecimal selfAmount = totalAmount.multiply(percent);
        return selfAmount.intValue();
    }

    public static double f2y(int fen) {
        BigDecimal totalAmount = new BigDecimal(String.valueOf(fen));
        BigDecimal divisor = new BigDecimal("100");
        BigDecimal yb = totalAmount.divide(divisor, 2, RoundingMode.HALF_UP);
        return yb.doubleValue();
    }

    public static int percent(double y, String percentPoint) {
        //开始发分成
        BigDecimal percent = new BigDecimal(percentPoint);
        BigDecimal totalAmount = new BigDecimal(String.valueOf(y * 100));
        BigDecimal selfAmount = totalAmount.multiply(percent);
        int result = (selfAmount.setScale(0, BigDecimal.ROUND_HALF_UP)).intValue();
        Logger.info("分成计算，y=%s,percentPoint=%s,result=%s", y, percentPoint, result);
        return result;
    }

    public static int percent(int fen, String percentPoint) {
        //开始发分成
        BigDecimal percent = new BigDecimal(percentPoint);
        BigDecimal totalAmount = new BigDecimal(fen);
        BigDecimal selfAmount = totalAmount.multiply(percent);
        int result = (selfAmount.setScale(0, BigDecimal.ROUND_HALF_UP)).intValue();
        Logger.info("分成计算，fen=%s,percentPoint=%s,result=%s", fen, percentPoint, result);
        return result;
    }

    public static void main(String[] args) {
        //1000豆 = 1元  ===》 10豆 = 1分
        BigDecimal percent = new BigDecimal("10");
        BigDecimal totalAmount = new BigDecimal("100");//开心豆
        int fen = (totalAmount.divide(percent, 2, BigDecimal.ROUND_HALF_UP)).intValueExact();
        System.out.println(fen);
    }
}
