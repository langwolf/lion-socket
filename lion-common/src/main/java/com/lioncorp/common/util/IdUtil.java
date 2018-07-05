package com.lioncorp.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

public class IdUtil {

//    public static final String seed = PropertyUtil.get("cur_domain");
	 public static final String seed ="http://test.netease.com";
    private static Random rd = new Random();
    
    public static long generateHTTPRequestId(String url) {
        return genID(seed+"/"+url+"/" + Calendar.getInstance().getTimeInMillis()+"/"+rd.nextInt(100000000));
    }
     
    /**
     * Generate a id from a string value using MD5 digest algorithm
     * 
     * @param str
     * @return
     */
    public static long genID(String str) {
        byte[] data = encode(str);
        return halfDigest(data, 0, data.length);
    }

    /**
     * return a digest value which is half the length of a MD5 digest value. use
     * 53 length for js
     * 
     * @param data
     * @param start
     * @param len
     * @return
     */
    // public static long halfDigest(byte[] data, int start, int len) {
    // byte[] digest = digest(data, start, len);
    // return (((long) digest[0] << 56) | ((long) (digest[1] & 0xFF) << 48)
    // | ((long) (digest[2] & 0xFF) << 40)
    // | ((long) (digest[3] & 0xFF) << 32)
    // | ((long) (digest[4] & 0xFF) << 24)
    // | ((long) (digest[5] & 0xFF) << 16)
    // | ((long) (digest[6] & 0xFF) << 8) | ((long) digest[7] & 0xFF));
    // }
    public static long halfDigest(byte[] data, int start, int len) {
        byte[] digest = digest(data, start, len);
        long res = (((long) (digest[0] & 0x1F) << 48)
                | ((long) (digest[1] & 0xFF) << 40)
                | ((long) (digest[2] & 0xFF) << 32)
                | ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[4] & 0xFF) << 16)
                | ((long) (digest[5] & 0xFF) << 8) | (digest[6] & 0xFF));
        return Math.abs(res);
    }

    // test range
    public static void main(String[] args) {
//        long res = (((long) (255 & 0x1F) << 48) | ((long) (255 & 0xFF) << 40)
//                | ((long) (255 & 0xFF) << 32) | ((long) (255 & 0xFF) << 24)
//                | ((long) (255 & 0xFF) << 16) | ((long) (255 & 0xFF) << 8) | (255 & 0xFF));
//        log.debug(255 & 15);
//        log.debug(res);
//        log.debug(genID("aa"));
    }

    private static final ThreadLocal<MessageDigest> DIGESTER_CONTEXT = new ThreadLocal<MessageDigest>() {
        @Override
        protected synchronized MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * Construct a hash value for a byte array.
     */
    private static byte[] digest(byte[] data, int start, int len) {
        MessageDigest digester = DIGESTER_CONTEXT.get();
        digester.update(data, start, len);
        return digester.digest();
    }

    /**
     * Convert <code>s</code> to a UTF8 Sequence.
     * 
     * @param s
     * @return
     */
    public static byte[] encode(String s) {
        int strlen = s.length();
        int utflen = 0;
        char c;
        for (int i = 0; i < strlen; i++) {
            c = s.charAt(i);
            if ((c >= 0x0000) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }
        byte[] buf = new byte[utflen];
        encode(s, buf, 0);
        return buf;
    }

    /**
     * 将字符串转化成为UTF-8的表示，保存在bytes从offset开始的空间内. 需要注意的是，这个方法没有做range
     * check，所以希望给出的bytes留下 足够的空间.
     * 
     * @param s
     *            String to be encoded
     * @param bytes
     *            Buffer to store encoded result
     * @param offset
     *            Start of the buffer to store the result
     * @return 实际编码后的字节数，
     */
    private static int encode(String s, byte[] bytes, int offset) {
        int strlen = s.length();

        int i = 0;
        char c;
        int pos = offset;
        for (i = 0; i < strlen; i++) {
            c = s.charAt(i);
            if (!((c >= 0x0000) && (c <= 0x007F))) {
                break;
            }
            bytes[pos++] = (byte) c;
        }

        for (; i < strlen; i++) {
            c = s.charAt(i);
            if ((c >= 0x0000) && (c <= 0x007F)) {
                bytes[pos++] = (byte) c;
            } else if (c > 0x07FF) {
                bytes[pos++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytes[pos++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                bytes[pos++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } else {
                bytes[pos++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                bytes[pos++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }
        return pos - offset;
    }

    private static volatile SecureRandom numberGenerator = new SecureRandom();

    public static String genUserUrl(int count) {
        String url = RandomStringUtils.random(1, 0, 9, false, true, new char[] {
            '1', '2', '3', '4', '5', '6', '7', '8', '9'
        }, numberGenerator)
                + RandomStringUtils.random(count - 1, 0, 9, false, true,
                        new char[] {
                            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
                        }, numberGenerator);
        return url;
    }

    /**
     * 将指定byte数组转换成16进制字符串
     * 
     * @param b
     * @return
     */
    public static String byteToHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (byte element: b) {
            String hex = Integer.toHexString(element & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    /**
     * 将16进制字符串转换成字节数组
     * 
     * @param hex
     * @return
     */
    private static final String HEX_NUMS_STR = "0123456789ABCDEF";

    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR
                    .indexOf(hexChars[pos + 1]));
        }
        return result;
    }

    // public static String genOrderId(int count) {
    // String url = RandomStringUtils.random(count, 0, 35, true, true, new
    // char[] { '1', '2', '3', '4', '5', '6', '7',
    // '8', '9' ,'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
    // 'm', 'n', 'o', 'p', 'q', 'r', 's',
    // 't', 'u', 'v', 'w', 'x', 'y', 'z' }, new SecureRandom());
    // // + RandomStringUtils.random(count - 1, 0, 9, true, true, new char[] {
    // '0', '1', '2', '3', '4', '5',
    // // '6', '7', '8', '9' }, numberGenerator);
    // return url;
    // }

    /**
     * 随机生成14位订单号，订单格式为年月日+六位随机字符串，随机字符串由0-9和26个英文小写字母构成
     * 
     * @param length
     * @return
     */
    public static String generateOrderId(int length) {
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        char[] str = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        };
        StringBuffer sb = new StringBuffer("");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        sb.append(df.format(Calendar.getInstance().getTime()));
        Random r = new Random();
        while (count < length) {
            i = Math.abs(r.nextInt(str.length));

            if (i >= 0 && i < str.length) {
                sb.append(str[i]);
                count++;
            }
        }
        return sb.toString();
    }

    public static String generateCode(int length) {
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        char[] str = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', '2', '3', '4', '5',
            '6', '7', '8', '9'
        };
        StringBuffer sb = new StringBuffer("");
        Random r = new Random();
        while (count < length) {
            i = Math.abs(r.nextInt(str.length));

            if (i >= 0 && i < str.length) {
                sb.append(str[i]);
                count++;
            }
        }
        return sb.toString();
    }
}
