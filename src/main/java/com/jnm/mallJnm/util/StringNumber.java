package com.jnm.mallJnm.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class StringNumber {
    private static final MathContext MATH_CONTEXT = new MathContext(20, RoundingMode.HALF_UP);

    public static String add(String number1, String number2) {
        number1 = StringUtil.isNullOrEmpty(number1)?"0":number1;
        number2 = StringUtil.isNullOrEmpty(number2)?"0":number2;
        BigDecimal decimal = new BigDecimal(number1);
        decimal = decimal.add(new BigDecimal(number2));
        return decimal.toString();
    }

    public static String sub(String number1, String number2) {
        number1 = StringUtil.isNullOrEmpty(number1)?"0":number1;
        number2 = StringUtil.isNullOrEmpty(number2)?"0":number2;
        BigDecimal decimal = new BigDecimal(number1);
        decimal = decimal.subtract(new BigDecimal(number2));
        return decimal.toString();
    }

    public static String mul(String number1, String number2) {
        number1 = StringUtil.isNullOrEmpty(number1)?"0":number1;
        number2 = StringUtil.isNullOrEmpty(number2)?"0":number2;
        BigDecimal decimal = new BigDecimal(number1);
        decimal = decimal.multiply(new BigDecimal(number2), MATH_CONTEXT);
        return decimal.toString();
    }

    public static String mulPrecisionOfTwo(String number1, String number2) {
        number1 = StringUtil.isNullOrEmpty(number1)?"0":number1;
        number2 = StringUtil.isNullOrEmpty(number2)?"0":number2;
        BigDecimal decimal = new BigDecimal(number1);
        decimal = decimal.multiply(new BigDecimal(number2), MATH_CONTEXT);
        return decimal.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public static String div(String number1, String number2) {
        number1 = StringUtil.isNullOrEmpty(number1)?"0":number1;
        number2 = StringUtil.isNullOrEmpty(number2)?"0":number2;
        BigDecimal decimal = new BigDecimal(number1);
        decimal = decimal.divide(new BigDecimal(number2), MATH_CONTEXT);
        return decimal.toString();
    }

    public static String divPrecisionOfZeroUp(String number1, String number2) {
        BigDecimal decimal = new BigDecimal(number1);
        return decimal.divide(new BigDecimal(number2), RoundingMode.UP).toString();
    }

    public static String divPrecisionOfZeroDown(String number1, String number2) {
        BigDecimal decimal = new BigDecimal(number1);
        return decimal.divide(new BigDecimal(number2), RoundingMode.DOWN).toString();
    }

    public static String divPrecisionOfTwo(String number1, String number2) {
        number1 = StringUtil.isNullOrEmpty(number1)?"0":number1;
        number2 = StringUtil.isNullOrEmpty(number2)?"0":number2;
        BigDecimal decimal = new BigDecimal(number1);
        decimal = decimal.divide(new BigDecimal(number2), MATH_CONTEXT);
        return decimal.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public static String abs(String number) {
        if (number == null || number.trim().isEmpty()) return "0";
        return new BigDecimal(number).abs(MATH_CONTEXT).toString();
    }

    public static int compareTo(String number1, String number2) {
        number1 = StringUtil.isNullOrEmpty(number1)?"0":number1;
        number2 = StringUtil.isNullOrEmpty(number2)?"0":number2;
        return new BigDecimal(number1).compareTo(new BigDecimal(number2));
    }

    public static String remainder(String number1, String number2) {
        BigDecimal decimal = new BigDecimal(number1);
        decimal = decimal.remainder(new BigDecimal(number2));
        return decimal.toString();
    }

    public static boolean hasRemainder(String number1, String number2) {
        BigDecimal decimal = new BigDecimal(number1);
        decimal = decimal.remainder(new BigDecimal(number2));
        return decimal.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 保留两位小数， 四舍五入
     *
     * @param number 要转换的数字字符串
     * @return 转换完成的字符串
     */
    public static String formatPrecisionOfTwo(String number) {
        return new BigDecimal(number).setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * 去除小数位， 四舍五入
     *
     * @param number 要转换的数字字符串
     * @return 转换完成的字符串
     */
    public static String formatPrecisionOfZero(String number) {
        return new BigDecimal(number).setScale(0, RoundingMode.HALF_UP).toString();
    }

    /**
     * 小数点后直接抹除
     *
     * @param number 要转换的数字字符串
     * @return 转换完成的字符串
     */
    public static String formatIgnoreOfZero(String number) {
        return new BigDecimal(number).setScale(0, RoundingMode.DOWN).toString();
    }

    public static int stringToIntOfMoney(String money) {
        BigDecimal decimal = new BigDecimal(money);
        return decimal.multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN).intValue();
    }

    /**
     * String 类型的金额转为Long型，并只保存原有金额的小数点后两位有效数字（四舍五入），保存时无小数
     *
     * @param money 要转换的数字字符串
     * @return 转换完成的值
     */
    public static long stringToLongOfMoney(String money) {
        return Long.parseLong(formatPrecisionOfZero(mul(money, "100")));
    }

    public static String longToStringOfMoney(long money) {
        return div(String.valueOf(money), "100");
    }
}
