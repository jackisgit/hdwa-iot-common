/**
 *
 */
package com.wanda.epc.util;


/**
 * 数字/模拟功率转换工具
 *
 * @author liaiguo
 *
 */
public class DAPCUtil {

    /**
     * Converts an unsigned byte to an integer.
     *
     * @param b
     *            the byte to be converted.
     * @return an integer containing the unsigned byte value.
     */
    public static final int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }// unsignedByteToInt

    public static final short registerToShort(byte[] bytes) {
        return (short) ((bytes[0] << 8) | (bytes[1] & 0xff));
    }// registerToShort

    public static final short registerToShort(byte[] bytes, int idx) {
        return (short) ((bytes[idx] << 8) | (bytes[idx + 1] & 0xff));
    }// registerToShort

    /**
     * Returns the given byte[] as hex encoded string.
     *
     * @param data
     *            a byte[] array.
     * @return a hex encoded String.
     */
    public static final String toHex(byte[] data) {
        return toHex(data, 0, data.length);
    }// toHex

    public static float formFloat(byte bHighest, byte bHigh, byte bLow,
                                  byte bLowest) {
        float result = 0;
        int iHighest, iHigh, iLow, iLowest, S, E;
        float F;
        // result=(-1)^S*(1+F)*2^(E-127)
        // here we make S, 1 or -1
        iHighest = bHighest;
        iHigh = bHigh;
        iLow = bLow;
        iLowest = bLowest;

        // Count S
        if (iHighest < 0)
            S = -1;
        else
            S = 1;

        // Count E
        if (iHighest < 0) {
            if (iHigh < 0)
                E = 1 + (byteToUnsigned(bHighest) - 128) * 2;
            else
                E = (byteToUnsigned(bHighest) - 128) * 2;
        } else {
            if (iHigh < 0)
                E = 1 + (byteToUnsigned(bHighest)) * 2;
            else
                E = (byteToUnsigned(bHighest)) * 2;
        }
        // Count F
        F = 0;
        for (int i = 23; i > 15; i--) {
            if (0 != (0x1 & (iLowest >>> (23 - i))))
                F = (float) (F + 1 / (Math.pow(2, i)));
        }
        for (int i = 15; i > 7; i--) {
            if (0 != (0x1 & (iLow >>> (15 - i))))
                F = (float) (F + 1 / (Math.pow(2, i)));
        }
        for (int i = 7; i > 0; i--) {
            if (0 != (0x1 & (iHigh >>> (7 - i))))
                F = (float) (F + 1 / (Math.pow(2, i)));
        }
        result = (float) (S * (1 + F) * Math.pow(2, E - 127));
        return result;
    }

    /**
     * Returns a <tt>String</tt> containing unsigned hexadecimal numbers as
     * digits. The <tt>String</tt> will coontain two hex digit characters for
     * each byte from the passed in <tt>byte[]</tt>.<br>
     * The bytes will be separated by a space character.
     * <p/>
     *
     * @param data
     *            the array of bytes to be converted into a hex-string.
     * @param off
     *            the offset to start converting from.
     * @param length
     *            the number of bytes to be converted.
     *
     * @return the generated hexadecimal representation as <code>String</code>.
     */
    public static final String toHex(byte[] data, int off, int length) {
        // double size, two bytes (hex range) for one byte
        StringBuffer buf = new StringBuffer(data.length * 2);
        for (int i = off; i < length; i++) {
            // don't forget the second hex digit
            if ((data[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(data[i] & 0xff, 16));
            if (i < data.length - 1) {
                buf.append(" ");
            }
        }
        return buf.toString();
    }// toHex

    public static final String toHexNoSpace(byte[] data, int off, int length) {
        // double size, two bytes (hex range) for one byte
        StringBuffer buf = new StringBuffer(data.length * 2);
        for (int i = off; i < length; i++) {
            // don't forget the second hex digit
            if ((data[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(data[i] & 0xff, 16));
        }
        return buf.toString();
    }// toHex

    /**
     * Returns a <tt>byte[]</tt> containing the given byte as unsigned
     * hexadecimal number digits.
     * <p/>
     *
     * @param i
     *            the int to be converted into a hex string.
     * @return the generated hexadecimal representation as <code>byte[]</code>.
     */
    public static final byte[] toHex(int i) {
        StringBuffer buf = new StringBuffer(2);
        // don't forget the second hex digit
        if ((i & 0xff) < 0x10) {
            buf.append("0");
        }
        buf.append(Long.toString(i & 0xff, 16).toUpperCase());
        return buf.toString().getBytes();
    }// toHex

    public static final int makeWord(int hibyte, int lowbyte) {
        int hi = 0xFF & hibyte;
        int low = 0xFF & lowbyte;
        return ((hi << 8) | low);
    }// makeWord

    public static final String parseDLT645ERR(int in) {
        String msg = new String();
        if ((byte) (in & 0x01) == (byte) 0x01) {
            msg = "�Ƿ����";
        }
        if ((byte) (in & 0x02) == (byte) 0x02) {
            msg += "��ݱ�ʶ��";
        }
        if ((byte) (in & 0x04) == (byte) 0x04) {
            msg += "�����";
        }
        if ((byte) (in & 0x10) == (byte) 0x10) {
            msg += "��ʱ����";
        }
        if ((byte) (in & 0x20) == (byte) 0x20) {
            msg += "��ʱ����";
        }
        if ((byte) (in & 0x40) == (byte) 0x40) {
            msg += "������";
        }
        return msg;
    }

    /**
     * 计算CRC效验
     *
     * @param data
     *            数据位
     * @param offset
     *            偏移量
     * @param len
     *            数据位减2
     * @return crc
     */
    public static final int[] calculateCRC(byte[] data, int offset, int len) {

        // OXFF=255 binary=1111 1111
        int[] crc = {0xFF, 0xFF};
        int nextByte = 0;
        int uIndex; /* will index into CRC lookup *//* table */
        /* pass through message buffer */
        for (int i = offset; i < len && i < data.length; i++) {
            // & 遇0得0
            nextByte = 0xFF & (data[i]);
            // 1111111 异或运算 只有一个为1才为1 否则为0 一般用于取反码
            uIndex = crc[0] ^ nextByte; // *puchMsg++; /* calculate the CRC */
            crc[0] = crc[1] ^ auchCRCHi[uIndex];
            crc[1] = auchCRCLo[uIndex];
        }
        return crc;
    }

    /**
     * CRC 16效验
     *
     * @param data
     * @return crc
     */
    public static int calculateCRC16(byte[] data) {
        // 定义一int类型的16进制变量
        int crc = 0xFFFF;
        int crctemp = 0;
        for (int i = 0; i < data.length; i++) {
            crctemp = (0xFF00) | ((int) (data[i] ^ (crc & 0x00FF))); // 保存低8位
            crc = crctemp & (crc | 0x00FF);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) == 1) {
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else if ((crc & 0x0001) == 0) {
                    crc = crc >> 1;
                }
            }
        }
        return crc;
    }

    /**
     * 效验表table
     */
    private final static int[] table = {0x0000, 0xC0C1, 0xC181, 0x0140,
            0xC301, 0x03C0, 0x0280, 0xC241, 0xC601, 0x06C0, 0x0780, 0xC741,
            0x0500, 0xC5C1, 0xC481, 0x0440, 0xCC01, 0x0CC0, 0x0D80, 0xCD41,
            0x0F00, 0xCFC1, 0xCE81, 0x0E40, 0x0A00, 0xCAC1, 0xCB81, 0x0B40,
            0xC901, 0x09C0, 0x0880, 0xC841, 0xD801, 0x18C0, 0x1980, 0xD941,
            0x1B00, 0xDBC1, 0xDA81, 0x1A40, 0x1E00, 0xDEC1, 0xDF81, 0x1F40,
            0xDD01, 0x1DC0, 0x1C80, 0xDC41, 0x1400, 0xD4C1, 0xD581, 0x1540,
            0xD701, 0x17C0, 0x1680, 0xD641, 0xD201, 0x12C0, 0x1380, 0xD341,
            0x1100, 0xD1C1, 0xD081, 0x1040, 0xF001, 0x30C0, 0x3180, 0xF141,
            0x3300, 0xF3C1, 0xF281, 0x3240, 0x3600, 0xF6C1, 0xF781, 0x3740,
            0xF501, 0x35C0, 0x3480, 0xF441, 0x3C00, 0xFCC1, 0xFD81, 0x3D40,
            0xFF01, 0x3FC0, 0x3E80, 0xFE41, 0xFA01, 0x3AC0, 0x3B80, 0xFB41,
            0x3900, 0xF9C1, 0xF881, 0x3840, 0x2800, 0xE8C1, 0xE981, 0x2940,
            0xEB01, 0x2BC0, 0x2A80, 0xEA41, 0xEE01, 0x2EC0, 0x2F80, 0xEF41,
            0x2D00, 0xEDC1, 0xEC81, 0x2C40, 0xE401, 0x24C0, 0x2580, 0xE541,
            0x2700, 0xE7C1, 0xE681, 0x2640, 0x2200, 0xE2C1, 0xE381, 0x2340,
            0xE101, 0x21C0, 0x2080, 0xE041, 0xA001, 0x60C0, 0x6180, 0xA141,
            0x6300, 0xA3C1, 0xA281, 0x6240, 0x6600, 0xA6C1, 0xA781, 0x6740,
            0xA501, 0x65C0, 0x6480, 0xA441, 0x6C00, 0xACC1, 0xAD81, 0x6D40,
            0xAF01, 0x6FC0, 0x6E80, 0xAE41, 0xAA01, 0x6AC0, 0x6B80, 0xAB41,
            0x6900, 0xA9C1, 0xA881, 0x6840, 0x7800, 0xB8C1, 0xB981, 0x7940,
            0xBB01, 0x7BC0, 0x7A80, 0xBA41, 0xBE01, 0x7EC0, 0x7F80, 0xBF41,
            0x7D00, 0xBDC1, 0xBC81, 0x7C40, 0xB401, 0x74C0, 0x7580, 0xB541,
            0x7700, 0xB7C1, 0xB681, 0x7640, 0x7200, 0xB2C1, 0xB381, 0x7340,
            0xB101, 0x71C0, 0x7080, 0xB041, 0x5000, 0x90C1, 0x9181, 0x5140,
            0x9301, 0x53C0, 0x5280, 0x9241, 0x9601, 0x56C0, 0x5780, 0x9741,
            0x5500, 0x95C1, 0x9481, 0x5440, 0x9C01, 0x5CC0, 0x5D80, 0x9D41,
            0x5F00, 0x9FC1, 0x9E81, 0x5E40, 0x5A00, 0x9AC1, 0x9B81, 0x5B40,
            0x9901, 0x59C0, 0x5880, 0x9841, 0x8801, 0x48C0, 0x4980, 0x8941,
            0x4B00, 0x8BC1, 0x8A81, 0x4A40, 0x4E00, 0x8EC1, 0x8F81, 0x4F40,
            0x8D01, 0x4DC0, 0x4C80, 0x8C41, 0x4400, 0x84C1, 0x8581, 0x4540,
            0x8701, 0x47C0, 0x4680, 0x8641, 0x8201, 0x42C0, 0x4380, 0x8341,
            0x4100, 0x81C1, 0x8081, 0x4040,};

    /**
     *
     * @param bytes
     * @param len
     * @return crc
     */
    public static int calc(byte[] bytes, int len) {
        int crc = 0xFFFF;
        for (int i = 0; i < len; i++) {
            byte b = bytes[i];
            crc = (crc >>> 8) ^ table[(crc ^ b) & 0xff];
        }
        return crc;

    }

    /**
     * Word类型无符号数据处理 param hign param low return byteToUnsigned
     */
    public static int formUnsignedWord(byte high, byte low) {
        return (byteToUnsigned(high) * 256 + byteToUnsigned(low));
    }

    /**
     * Word类型有符号数据处理 param hign param low return formUnsignedWord(hign,low)
     */
    public static int formSignedWord(byte high, byte low) {
        if (byteToUnsigned(high) >= 128) {
            int i = 32768 - (formUnsignedWord((byte) (high & 0x7F), low));
            return i * -1;
        } else
            return formUnsignedWord(high, low);
    }

    /**
     * 转换为无符号数
     *
     * @param noSubmark
     * @return i
     */
    public static int byteToUnsigned(byte noSubmark) {
        int i = noSubmark;
        // 判断如果是正数
        if (i >= 0)
            return i;
        else {
            // 如果是负数，0X7F二进制为0111 1111 与运算后 遇0得0 ，转为无符号正数 + 128 结果为正数。
            i = (noSubmark & 0x7F) + 128;
            return i;
        }
    }

    /**
     * DWord类型无符号数据处理 param b1 param b2 param b3 param b4 return
     * formUnsignedWord
     */
    public static int formUnsignedDword(byte b1, byte b2, byte b3, byte b4) {
        // 65535=2^16-1
        return (formUnsignedWord(b3, b4) * 65535 + formUnsignedWord(b1, b2));
    }

    /**
     * 通过modbus工具类解析得到msgBuff后通过配置信息对数据进行处理
     *
     * @param msgBuff
     *            通过modbus解析接收到的数据 示例：byte[]
     * @param sign
     *            无符号/有符号 示例： 0/1
     * @param endian
     *            字节序 示例： 0/1
     * @param formula
     *            计算公式 示例：/ 1000 * PT * CT
     * @return String.valueOf(result);
     */
    // BaseRegisterAdd:0001,OffSet:2,Endian:1,sign:0
    public static String parseModbusBase(byte[] msgBuff, String offset,
                                         String sign, String endian, String formula) {

        int result = 0;
        if (sign.equals("0")) {
            result = toUnsigned(msgBuff, offset, endian, formula);
        } else {
            result = toSigned(msgBuff, offset, endian, formula);
        }
        return String.valueOf(result);
    }

    /**
     * ModbusBase无符号处理
     *
     * @param buff
     * @param offset
     * @param endian
     * @param formula
     * @return
     */
    public static int toUnsigned(byte[] buff, String offset, String endian,
                                 String formula) {
        int result = 0;
        int len = buff.length;
        if (endian.equals("0")) {
            for (int i = 0; i < buff.length; i++) {
                // put(("a"+i),);
            }
        }
        return result;
    }

    /**
     * IEEE-754的32位类型的浮点数处理
     *
     * @param msgbuff
     *            需要进行IEEE754转换的数据包
     * @return
     */
    public static float formIEEE754Float(byte b1, byte b2, byte b3, byte b4) {
        byte[] msgbuff = {b1, b2, b3, b4};
        String tempStr = "";
        String hexVal = "";
        for (int i = 0; i < msgbuff.length; i++) {
            tempStr = Integer.toHexString(msgbuff[i]);
            if (tempStr.length() < 2)
                hexVal = hexVal + "0" + tempStr;
            else if (tempStr.length() > 2)
                hexVal = hexVal
                        + tempStr.substring(tempStr.length() - 2,
                        tempStr.length());
            else
                hexVal += tempStr;
        }
        return Float.intBitsToFloat(Integer.valueOf(hexVal, 16));
    }

    /**
     * 南通四建燕赵电表PZ200z
     *
     * @param a
     * @param b
     * @return
     */
    public static double formPZ200z(byte a, byte b) {
        int result = formSignedWord(a, b);
        String result1 = "0." + result;
        return Double.valueOf(result1);
    }

    /**
     * 将字符串逐位转换成二进制
     */
    public static String formToBinaryString(String a) {
        return null;
    }

    /**
     * ModbusBase有符号处理
     *
     * @param buff
     * @param offset
     * @param endian
     * @param formula
     * @return
     */
    public static int toSigned(byte[] buff, String offset, String endian,
                               String formula) {
        int result = 0;
        return result;
    }

    /**
     * CRC高位 Table of CRC values for high-order byte
     */
    private final static short[] auchCRCHi = {0x00, 0xC1, 0x81, 0x40, 0x01,
            0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
            0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81,
            0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00,
            0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
            0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00,
            0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
            0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
            0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
            0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00,
            0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
            0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
            0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
            0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00,
            0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80,
            0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01,
            0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
            0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
            0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40};

    /**
     * CRC低位 Table of CRC values for low-order byte
     */
    private final static short[] auchCRCLo = {0x00, 0xC0, 0xC1, 0x01, 0xC3,
            0x03, 0x02, 0xC2, 0xC6, 0x06, 0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04,
            0xCC, 0x0C, 0x0D, 0xCD, 0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB,
            0x0B, 0xC9, 0x09, 0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB,
            0xDA, 0x1A, 0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14,
            0xD4, 0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
            0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3, 0xF2,
            0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4, 0x3C, 0xFC,
            0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A, 0x3B, 0xFB, 0x39,
            0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29, 0xEB, 0x2B, 0x2A, 0xEA,
            0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED, 0xEC, 0x2C, 0xE4, 0x24, 0x25,
            0xE5, 0x27, 0xE7, 0xE6, 0x26, 0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21,
            0x20, 0xE0, 0xA0, 0x60, 0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66,
            0xA6, 0xA7, 0x67, 0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D,
            0xAF, 0x6F, 0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8,
            0x68, 0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E,
            0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5, 0x77,
            0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71, 0x70, 0xB0,
            0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92, 0x96, 0x56, 0x57,
            0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C, 0x5D, 0x9D, 0x5F, 0x9F,
            0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B, 0x99, 0x59, 0x58, 0x98, 0x88,
            0x48, 0x49, 0x89, 0x4B, 0x8B, 0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F,
            0x8D, 0x4D, 0x4C, 0x8C, 0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46,
            0x86, 0x82, 0x42, 0x43, 0x83, 0x41, 0x81, 0x80, 0x40};

    /**
     * 场景的 deviceTypeID 320~355 或 376 是 公共照明和夜景照明的场景，这些场景要特殊处理
     * @param param
     * @return
     */
    public static boolean isCjDeviceTypeID(String param) {
        try {
            String paramStr[] = param.replace(".", "@").split("@");
            Integer deviceTypeID = 0;
            if (paramStr.length == 5) {//老格式CP.gcID.deviceTypeID.deviceID.paramID
                deviceTypeID = Integer.parseInt(paramStr[2]);
            } else {//新格式CP.gcID.getWayID.deviceTypeID.deviceID.paramID
                deviceTypeID = Integer.parseInt(paramStr[3]);
            }


            if ((deviceTypeID >= 320 && deviceTypeID <= 355) || deviceTypeID == 376) {
                return true;
            }

        } catch (Exception e) {
            System.out.println("param:" + param + ",不是正规的 类型，无法搞定！");
        }
        return false;
    }


}
