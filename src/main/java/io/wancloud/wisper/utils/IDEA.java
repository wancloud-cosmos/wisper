package io.wancloud.wisper.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class IDEA {

    private static final Logger logger = LoggerFactory.getLogger(IDEA.class);

    private IDEA() {
    }

    /**
     * 获取密钥因子
     *
     * @param seed
     * @return
     */
    public static byte[] getKey(String seed) {
        int len = seed.length();
        if (len >= 16) {
            seed = seed.substring(0, 16);
        } else {
            for (int i = 0; i < 16 - len; i++) {
                seed = seed.concat("0");
            }
        }

        return seed.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 加密
     *
     * @param plaintext
     * @return
     */
    public static String encrypt(String seed, String plaintext) {
        try {
            return bytes2hex(ideaEncrypt(getKey(seed), plaintext.getBytes(StandardCharsets.UTF_8), true));
        } catch (Exception e) {
            logger.error("IDEA encrypt failed, plaintext: " + plaintext, e);
            return plaintext;
        }
    }

    /**
     * 解密
     *
     * @param ciphertext
     * @return
     */
    public static String decrypt(String seed, String ciphertext) {
        try {
            String plaintexts = new String(ideaEncrypt(getKey(seed), hex2bytes(ciphertext.getBytes(StandardCharsets.UTF_8)), false), StandardCharsets.UTF_8);
            int len = plaintexts.length();
            return plaintexts.substring(0, len - 6);
        } catch (Exception e) {
            logger.error("IDEA decrypt failed, ciphertext: " + ciphertext, e);
            return ciphertext;
        }
    }

    private static byte[] doEncrypt(byte[] bytekey, byte[] inputBytes, boolean flag) {
        byte[] encryptCode = new byte[8];
        // 分解子密钥
        int[] key = getSubKey(flag, bytekey);
        // 进行加密操作
        encrypt(key, inputBytes, encryptCode);
        // 返回加密数据
        return encryptCode;
    }

    private static int bytes2int(byte[] inBytes, int startPos) {
        return ((inBytes[startPos] << 8) & 0xff00) +
                (inBytes[startPos + 1] & 0xff);
    }

    private static void int2bytes(int inputInt, byte[] outBytes, int startPos) {
        outBytes[startPos] = (byte) (inputInt >>> 8);
        outBytes[startPos + 1] = (byte) inputInt;
    }

    private static int multiplyXY(int x, int y) {
        if (x == 0) {
            x = 0x10001 - y;
        } else if (y == 0) {
            x = 0x10001 - x;
        } else {
            int tmp = x * y;
            y = tmp & 0xffff;
            x = tmp >>> 16;
            x = (y - x) + ((y < x) ? 1 : 0);
        }
        return x & 0xffff;
    }

    private static void encrypt(int[] key, byte[] inbytes, byte[] outbytes) {
        int k = 0;
        int a = bytes2int(inbytes, 0);
        int b = bytes2int(inbytes, 2);
        int c = bytes2int(inbytes, 4);
        int d = bytes2int(inbytes, 6);
        for (int i = 0; i < 8; i++) {
            a = multiplyXY(a, key[k++]);
            b += key[k++];
            b &= 0xffff;
            c += key[k++];
            c &= 0xffff;
            d = multiplyXY(d, key[k++]);
            int tmp1 = b;
            int tmp2 = c;
            c ^= a;
            b ^= d;
            c = multiplyXY(c, key[k++]);
            b += c;
            b &= 0xffff;
            b = multiplyXY(b, key[k++]);
            c += b;
            c &= 0xffff;
            a ^= b;
            d ^= c;
            b ^= tmp2;
            c ^= tmp1;
        }
        int2bytes(multiplyXY(a, key[k++]), outbytes, 0);
        int2bytes(c + key[k++], outbytes, 2);
        int2bytes(b + key[k++], outbytes, 4);
        int2bytes(multiplyXY(d, key[k]), outbytes, 6);
    }

    private static int[] encryptSubkey(byte[] byteKey) {
        int[] key = new int[52];
        if (byteKey.length < 16) {
            byte[] tmpkey = new byte[16];
            System.arraycopy(byteKey, 0, tmpkey,
                    tmpkey.length - byteKey.length, byteKey.length);
            byteKey = tmpkey;
        }
        for (int i = 0; i < 8; i++) {
            key[i] = bytes2int(byteKey, i * 2);
        }
        for (int j = 8; j < 52; j++) {
            if ((j & 0x7) < 6) {
                key[j] = (((key[j - 7] & 0x7f) << 9) | (key[j - 6] >> 7)) &
                        0xffff;
            } else if ((j & 0x7) == 6) {
                key[j] = (((key[j - 7] & 0x7f) << 9) | (key[j - 14] >> 7)) &
                        0xffff;
            } else {
                key[j] = (((key[j - 15] & 0x7f) << 9) | (key[j - 14] >> 7)) &
                        0xffff;
            }
        }
        return key;
    }

    private static int funcA(int a) {
        if (a < 2) {
            return a;
        }
        int b = 1;
        int c = 0x10001 / a;
        for (int i = 0x10001 % a; i != 1; ) {
            int d = a / i;
            a %= i;
            b = (b + (c * d)) & 0xffff;
            if (a == 1) {
                return b;
            }
            d = i / a;
            i %= a;
            c = (c + (b * d)) & 0xffff;
        }
        return (1 - c) & 0xffff;
    }

    private static int funcB(int b) {
        return (0 - b) & 0xffff;
    }

    private static int[] uncryptSubkey(int[] key) {
        int dec = 52;
        int asc = 0;
        int[] unkey = new int[52];
        int aa = funcA(key[asc++]);
        int bb = funcB(key[asc++]);
        int cc = funcB(key[asc++]);
        int dd = funcA(key[asc++]);
        unkey[--dec] = dd;
        unkey[--dec] = cc;
        unkey[--dec] = bb;
        unkey[--dec] = aa;
        for (int k1 = 1; k1 < 8; k1++) {
            aa = key[asc++];
            bb = key[asc++];
            unkey[--dec] = bb;
            unkey[--dec] = aa;
            aa = funcA(key[asc++]);
            bb = funcB(key[asc++]);
            cc = funcB(key[asc++]);
            dd = funcA(key[asc++]);
            unkey[--dec] = dd;
            unkey[--dec] = bb;
            unkey[--dec] = cc;
            unkey[--dec] = aa;
        }
        aa = key[asc++];
        bb = key[asc++];
        unkey[--dec] = bb;
        unkey[--dec] = aa;
        aa = funcA(key[asc++]);
        bb = funcB(key[asc++]);
        cc = funcB(key[asc++]);
        dd = funcA(key[asc]);
        unkey[--dec] = dd;
        unkey[--dec] = cc;
        unkey[--dec] = bb;
        unkey[--dec] = aa;
        return unkey;
    }

    private static int[] getSubKey(boolean flag, byte[] bytekey) {
        if (flag) {
            return encryptSubkey(bytekey);
        } else {
            return uncryptSubkey(encryptSubkey(bytekey));
        }
    }

    private static byte[] byteDataFormat(byte[] data, int unit) {
        int len = data.length;
        int padlen = unit - (len % unit);
        int newlen = len + padlen;
        byte[] newdata = new byte[newlen];
        System.arraycopy(data, 0, newdata, 0, len);
        for (int i = len; i < newlen; i++)
            newdata[i] = (byte) padlen;
        return newdata;
    }

    private static byte[] ideaEncrypt(byte[] ideaKey, byte[] ideaData, boolean flag) {
        byte[] formatKey = byteDataFormat(ideaKey, 16);
        byte[] formatData = byteDataFormat(ideaData, 8);
        int datalen = formatData.length;
        int unitcount = datalen / 8;
        byte[] resultData = new byte[datalen];
        for (int i = 0; i < unitcount; i++) {
            byte[] tmpkey = new byte[16];
            byte[] tmpdata = new byte[8];
            System.arraycopy(formatKey, 0, tmpkey, 0, 16);
            System.arraycopy(formatData, i * 8, tmpdata, 0, 8);
            byte[] tmpresult = doEncrypt(tmpkey, tmpdata, flag);
            System.arraycopy(tmpresult, 0, resultData, i * 8, 8);
        }
        return resultData;
    }

    /**
     * 二行制转字符串
     *
     * @param bytes
     * @return
     */
    private static String bytes2hex(byte[] bytes) { //一个字节的数，
        // 转成16进制字符串
        StringBuilder sb = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < bytes.length; n++) {
            //整数转成十六进制表示
            stmp = (java.lang.Integer.toHexString(bytes[n] & 0XFF));
            if (stmp.length() == 1) {
                sb.append("0").append(stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString().toUpperCase(); //转成大写
    }

    private static byte[] hex2bytes(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2, StandardCharsets.UTF_8);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

}
