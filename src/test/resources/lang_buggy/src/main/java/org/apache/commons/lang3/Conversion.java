

















package org.apache.commons.lang3;

import java.util.UUID;











































public class Conversion {












    public static int hexDigitToInt(final char hexDigit) {
        final int digit = Character.digit(hexDigit, 16);
        if (digit < 0) {
            throw new IllegalArgumentException("Cannot interpret '"
                + hexDigit
                + "' as a hexadecimal digit");
        }
        return digit;
    }













    public static int hexDigitMsb0ToInt(final char hexDigit) {
        switch (hexDigit) {
        case '0':
            return 0x0;
        case '1':
            return 0x8;
        case '2':
            return 0x4;
        case '3':
            return 0xC;
        case '4':
            return 0x2;
        case '5':
            return 0xA;
        case '6':
            return 0x6;
        case '7':
            return 0xE;
        case '8':
            return 0x1;
        case '9':
            return 0x9;
        case 'a':
        case 'A':
            return 0x5;
        case 'b':
        case 'B':
            return 0xD;
        case 'c':
        case 'C':
            return 0x3;
        case 'd':
        case 'D':
            return 0xB;
        case 'e':
        case 'E':
            return 0x7;
        case 'f':
        case 'F':
            return 0xF;
        default:
            throw new IllegalArgumentException("Cannot interpret '"
                + hexDigit
                + "' as a hexadecimal digit");
        }
    }














    public static boolean[] hexDigitToBinary(final char hexDigit) {
        switch (hexDigit) {
        case '0':
            return new boolean[]{false, false, false, false};
        case '1':
            return new boolean[]{true, false, false, false};
        case '2':
            return new boolean[]{false, true, false, false};
        case '3':
            return new boolean[]{true, true, false, false};
        case '4':
            return new boolean[]{false, false, true, false};
        case '5':
            return new boolean[]{true, false, true, false};
        case '6':
            return new boolean[]{false, true, true, false};
        case '7':
            return new boolean[]{true, true, true, false};
        case '8':
            return new boolean[]{false, false, false, true};
        case '9':
            return new boolean[]{true, false, false, true};
        case 'a':
        case 'A':
            return new boolean[]{false, true, false, true};
        case 'b':
        case 'B':
            return new boolean[]{true, true, false, true};
        case 'c':
        case 'C':
            return new boolean[]{false, false, true, true};
        case 'd':
        case 'D':
            return new boolean[]{true, false, true, true};
        case 'e':
        case 'E':
            return new boolean[]{false, true, true, true};
        case 'f':
        case 'F':
            return new boolean[]{true, true, true, true};
        default:
            throw new IllegalArgumentException("Cannot interpret '"
                + hexDigit
                + "' as a hexadecimal digit");
        }
    }














    public static boolean[] hexDigitMsb0ToBinary(final char hexDigit) {
        switch (hexDigit) {
        case '0':
            return new boolean[]{false, false, false, false};
        case '1':
            return new boolean[]{false, false, false, true};
        case '2':
            return new boolean[]{false, false, true, false};
        case '3':
            return new boolean[]{false, false, true, true};
        case '4':
            return new boolean[]{false, true, false, false};
        case '5':
            return new boolean[]{false, true, false, true};
        case '6':
            return new boolean[]{false, true, true, false};
        case '7':
            return new boolean[]{false, true, true, true};
        case '8':
            return new boolean[]{true, false, false, false};
        case '9':
            return new boolean[]{true, false, false, true};
        case 'a':
        case 'A':
            return new boolean[]{true, false, true, false};
        case 'b':
        case 'B':
            return new boolean[]{true, false, true, true};
        case 'c':
        case 'C':
            return new boolean[]{true, true, false, false};
        case 'd':
        case 'D':
            return new boolean[]{true, true, false, true};
        case 'e':
        case 'E':
            return new boolean[]{true, true, true, false};
        case 'f':
        case 'F':
            return new boolean[]{true, true, true, true};
        default:
            throw new IllegalArgumentException("Cannot interpret '"
                + hexDigit
                + "' as a hexadecimal digit");
        }
    }















    public static char binaryToHexDigit(final boolean[] src) {
        return binaryToHexDigit(src, 0);
    }
















    public static char binaryToHexDigit(final boolean[] src, final int srcPos) {
        if (src.length == 0) {
            throw new IllegalArgumentException("Cannot convert an empty array.");
        }
        if (src.length > srcPos + 3 && src[srcPos + 3]) {
            if (src.length > srcPos + 2 && src[srcPos + 2]) {
                if (src.length > srcPos + 1 && src[srcPos + 1]) {
                    if (src[srcPos]) {
                        return 'f';
                    } else {
                        return 'e';
                    }
                } else {
                    if (src[srcPos]) {
                        return 'd';
                    } else {
                        return 'c';
                    }
                }
            } else {
                if (src.length > srcPos + 1 && src[srcPos + 1]) {
                    if (src[srcPos]) {
                        return 'b';
                    } else {
                        return 'a';
                    }
                } else {
                    if (src[srcPos]) {
                        return '9';
                    } else {
                        return '8';
                    }
                }
            }
        } else {
            if (src.length > srcPos + 2 && src[srcPos + 2]) {
                if (src.length > srcPos + 1 && src[srcPos + 1]) {
                    if (src[srcPos]) {
                        return '7';
                    } else {
                        return '6';
                    }
                } else {
                    if (src[srcPos]) {
                        return '5';
                    } else {
                        return '4';
                    }
                }
            } else {
                if (src.length > srcPos + 1 && src[srcPos + 1]) {
                    if (src[srcPos]) {
                        return '3';
                    } else {
                        return '2';
                    }
                } else {
                    if (src[srcPos]) {
                        return '1';
                    } else {
                        return '0';
                    }
                }
            }
        }
    }
















    public static char binaryToHexDigitMsb0_4bits(final boolean[] src) {
        return binaryToHexDigitMsb0_4bits(src, 0);
    }


















    public static char binaryToHexDigitMsb0_4bits(final boolean[] src, final int srcPos) {
        if (src.length > 8) {
            throw new IllegalArgumentException("src.length>8: src.length=" + src.length);
        }
        if (src.length - srcPos < 4) {
            throw new IllegalArgumentException("src.length-srcPos<4: src.length="
                + src.length
                + ", srcPos="
                + srcPos);
        }
        if (src[srcPos + 3]) {
            if (src[srcPos + 2]) {
                if (src[srcPos + 1]) {
                    if (src[srcPos]) {
                        return 'f';
                    } else {
                        return '7';
                    }
                } else {
                    if (src[srcPos]) {
                        return 'b';
                    } else {
                        return '3';
                    }
                }
            } else {
                if (src[srcPos + 1]) {
                    if (src[srcPos]) {
                        return 'd';
                    } else {
                        return '5';
                    }
                } else {
                    if (src[srcPos]) {
                        return '9';
                    } else {
                        return '1';
                    }
                }
            }
        } else {
            if (src[srcPos + 2]) {
                if (src[srcPos + 1]) {
                    if (src[srcPos]) {
                        return 'e';
                    } else {
                        return '6';
                    }
                } else {
                    if (src[srcPos]) {
                        return 'a';
                    } else {
                        return '2';
                    }
                }
            } else {
                if (src[srcPos + 1]) {
                    if (src[srcPos]) {
                        return 'c';
                    } else {
                        return '4';
                    }
                } else {
                    if (src[srcPos]) {
                        return '8';
                    } else {
                        return '0';
                    }
                }
            }
        }
    }
















    public static char binaryBeMsb0ToHexDigit(final boolean[] src) {
        return binaryBeMsb0ToHexDigit(src, 0);
    }

















    public static char binaryBeMsb0ToHexDigit(boolean[] src, int srcPos) {
        if (src.length == 0) {
            throw new IllegalArgumentException("Cannot convert an empty array.");
        }
        final int beSrcPos = src.length - 1 - srcPos;
        final int srcLen = Math.min(4, beSrcPos + 1);
        final boolean[] paddedSrc = new boolean[4];
        System.arraycopy(src, beSrcPos + 1 - srcLen, paddedSrc, 4 - srcLen, srcLen);
        src = paddedSrc;
        srcPos = 0;
        if (src[srcPos]) {
            if (src.length > srcPos + 1 && src[srcPos + 1]) {
                if (src.length > srcPos + 2 && src[srcPos + 2]) {
                    if (src.length > srcPos + 3 && src[srcPos + 3]) {
                        return 'f';
                    } else {
                        return 'e';
                    }
                } else {
                    if (src.length > srcPos + 3 && src[srcPos + 3]) {
                        return 'd';
                    } else {
                        return 'c';
                    }
                }
            } else {
                if (src.length > srcPos + 2 && src[srcPos + 2]) {
                    if (src.length > srcPos + 3 && src[srcPos + 3]) {
                        return 'b';
                    } else {
                        return 'a';
                    }
                } else {
                    if (src.length > srcPos + 3 && src[srcPos + 3]) {
                        return '9';
                    } else {
                        return '8';
                    }
                }
            }
        } else {
            if (src.length > srcPos + 1 && src[srcPos + 1]) {
                if (src.length > srcPos + 2 && src[srcPos + 2]) {
                    if (src.length > srcPos + 3 && src[srcPos + 3]) {
                        return '7';
                    } else {
                        return '6';
                    }
                } else {
                    if (src.length > srcPos + 3 && src[srcPos + 3]) {
                        return '5';
                    } else {
                        return '4';
                    }
                }
            } else {
                if (src.length > srcPos + 2 && src[srcPos + 2]) {
                    if (src.length > srcPos + 3 && src[srcPos + 3]) {
                        return '3';
                    } else {
                        return '2';
                    }
                } else {
                    if (src.length > srcPos + 3 && src[srcPos + 3]) {
                        return '1';
                    } else {
                        return '0';
                    }
                }
            }
        }
    }



















    public static char intToHexDigit(final int nibble) {
        final char c = Character.forDigit(nibble, 16);
        if (c == Character.MIN_VALUE) {
            throw new IllegalArgumentException("nibble value not between 0 and 15: " + nibble);
        }
        return c;
    }



















    public static char intToHexDigitMsb0(final int nibble) {
        switch (nibble) {
        case 0x0:
            return '0';
        case 0x1:
            return '8';
        case 0x2:
            return '4';
        case 0x3:
            return 'c';
        case 0x4:
            return '2';
        case 0x5:
            return 'a';
        case 0x6:
            return '6';
        case 0x7:
            return 'e';
        case 0x8:
            return '1';
        case 0x9:
            return '9';
        case 0xA:
            return '5';
        case 0xB:
            return 'd';
        case 0xC:
            return '3';
        case 0xD:
            return 'b';
        case 0xE:
            return '7';
        case 0xF:
            return 'f';
        default:
            throw new IllegalArgumentException("nibble value not between 0 and 15: " + nibble);
        }
    }


















    public static long intArrayToLong(final int[] src, final int srcPos, final long dstInit, final int dstPos, final int nInts) {
        if ((src.length == 0 && srcPos == 0) || 0 == nInts) {
            return dstInit;
        }
        if ((nInts - 1) * 32 + dstPos >= 64) {
            throw new IllegalArgumentException(
                "(nInts-1)*32+dstPos is greather or equal to than 64");
        }
        long out = dstInit;
        int shift = 0;
        for (int i = 0; i < nInts; i++ ) {
            shift = i * 32 + dstPos;
            final long bits = ((0xffffffffL & src[i + srcPos]) << shift);
            final long mask = 0xffffffffL << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }


















    public static long shortArrayToLong(final short[] src, final int srcPos, final long dstInit, final int dstPos,
        final int nShorts) {
        if ((src.length == 0 && srcPos == 0) || 0 == nShorts) {
            return dstInit;
        }
        if ((nShorts - 1) * 16 + dstPos >= 64) {
            throw new IllegalArgumentException(
                "(nShorts-1)*16+dstPos is greather or equal to than 64");
        }
        long out = dstInit;
        int shift = 0;
        for (int i = 0; i < nShorts; i++ ) {
            shift = i * 16 + dstPos;
            final long bits = (0xffffL & src[i + srcPos]) << shift;
            final long mask = 0xffffL << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }


















    public static int shortArrayToInt(final short[] src, final int srcPos, final int dstInit, final int dstPos,
        final int nShorts) {
        if ((src.length == 0 && srcPos == 0) || 0 == nShorts) {
            return dstInit;
        }
        if ((nShorts - 1) * 16 + dstPos >= 32) {
            throw new IllegalArgumentException(
                "(nShorts-1)*16+dstPos is greather or equal to than 32");
        }
        int out = dstInit;
        int shift = 0;
        for (int i = 0; i < nShorts; i++ ) {
            shift = i * 16 + dstPos;
            final int bits = (0xffff & src[i + srcPos]) << shift;
            final int mask = 0xffff << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }


















    public static long byteArrayToLong(final byte[] src, final int srcPos, final long dstInit, final int dstPos,
        final int nBytes) {
        if ((src.length == 0 && srcPos == 0) || 0 == nBytes) {
            return dstInit;
        }
        if ((nBytes - 1) * 8 + dstPos >= 64) {
            throw new IllegalArgumentException(
                "(nBytes-1)*8+dstPos is greather or equal to than 64");
        }
        long out = dstInit;
        int shift = 0;
        for (int i = 0; i < nBytes; i++ ) {
            shift = i * 8 + dstPos;
            final long bits = (0xffL & src[i + srcPos]) << shift;
            final long mask = 0xffL << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }


















    public static int byteArrayToInt(final byte[] src, final int srcPos, final int dstInit, final int dstPos, final int nBytes) {
        if ((src.length == 0 && srcPos == 0) || 0 == nBytes) {
            return dstInit;
        }
        if ((nBytes - 1) * 8 + dstPos >= 32) {
            throw new IllegalArgumentException(
                "(nBytes-1)*8+dstPos is greather or equal to than 32");
        }
        int out = dstInit;
        int shift = 0;
        for (int i = 0; i < nBytes; i++ ) {
            shift = i * 8 + dstPos;
            final int bits = (0xff & src[i + srcPos]) << shift;
            final int mask = 0xff << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }


















    public static short byteArrayToShort(final byte[] src, final int srcPos, final short dstInit, final int dstPos,
        final int nBytes) {
        if ((src.length == 0 && srcPos == 0) || 0 == nBytes) {
            return dstInit;
        }
        if ((nBytes - 1) * 8 + dstPos >= 16) {
            throw new IllegalArgumentException(
                "(nBytes-1)*8+dstPos is greather or equal to than 16");
        }
        short out = dstInit;
        int shift = 0;
        for (int i = 0; i < nBytes; i++ ) {
            shift = i * 8 + dstPos;
            final int bits = (0xff & src[i + srcPos]) << shift;
            final int mask = 0xff << shift;
            out = (short)((out & ~mask) | bits);
        }
        return out;
    }
















    public static long hexToLong(final String src, final int srcPos, final long dstInit, final int dstPos, final int nHex) {
        if (0 == nHex) {
            return dstInit;
        }
        if ((nHex - 1) * 4 + dstPos >= 64) {
            throw new IllegalArgumentException(
                "(nHexs-1)*4+dstPos is greather or equal to than 64");
        }
        long out = dstInit;
        int shift = 0;
        for (int i = 0; i < nHex; i++ ) {
            shift = i * 4 + dstPos;
            final long bits = (0xfL & hexDigitToInt(src.charAt(i + srcPos))) << shift;
            final long mask = 0xfL << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }
















    public static int hexToInt(final String src, final int srcPos, final int dstInit, final int dstPos, final int nHex) {
        if (0 == nHex) {
            return dstInit;
        }
        if ((nHex - 1) * 4 + dstPos >= 32) {
            throw new IllegalArgumentException(
                "(nHexs-1)*4+dstPos is greather or equal to than 32");
        }
        int out = dstInit;
        int shift = 0;
        for (int i = 0; i < nHex; i++ ) {
            shift = i * 4 + dstPos;
            final int bits = (0xf & hexDigitToInt(src.charAt(i + srcPos))) << shift;
            final int mask = 0xf << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }
















    public static short hexToShort(final String src, final int srcPos, final short dstInit, final int dstPos, final int nHex) {
        if (0 == nHex) {
            return dstInit;
        }
        if ((nHex - 1) * 4 + dstPos >= 16) {
            throw new IllegalArgumentException(
                "(nHexs-1)*4+dstPos is greather or equal to than 16");
        }
        short out = dstInit;
        int shift = 0;
        for (int i = 0; i < nHex; i++ ) {
            shift = i * 4 + dstPos;
            final int bits = (0xf & hexDigitToInt(src.charAt(i + srcPos))) << shift;
            final int mask = 0xf << shift;
            out = (short)((out & ~mask) | bits);
        }
        return out;
    }
















    public static byte hexToByte(final String src, final int srcPos, final byte dstInit, final int dstPos, final int nHex) {
        if (0 == nHex) {
            return dstInit;
        }
        if ((nHex - 1) * 4 + dstPos >= 8) {
            throw new IllegalArgumentException(
                "(nHexs-1)*4+dstPos is greather or equal to than 8");
        }
        byte out = dstInit;
        int shift = 0;
        for (int i = 0; i < nHex; i++ ) {
            shift = i * 4 + dstPos;
            final int bits = (0xf & hexDigitToInt(src.charAt(i + srcPos))) << shift;
            final int mask = 0xf << shift;
            out = (byte)((out & ~mask) | bits);
        }
        return out;
    }


















    public static long binaryToLong(final boolean[] src, final int srcPos, final long dstInit, final int dstPos,
        final int nBools) {
        if ((src.length == 0 && srcPos == 0) || 0 == nBools) {
            return dstInit;
        }
        if (nBools - 1 + dstPos >= 64) {
            throw new IllegalArgumentException(
                "nBools-1+dstPos is greather or equal to than 64");
        }
        long out = dstInit;
        int shift = 0;
        for (int i = 0; i < nBools; i++ ) {
            shift = i * 1 + dstPos;
            final long bits = (src[i + srcPos] ? 1L : 0) << shift;
            final long mask = 0x1L << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }


















    public static int binaryToInt(final boolean[] src, final int srcPos, final int dstInit, final int dstPos, final int nBools) {
        if ((src.length == 0 && srcPos == 0) || 0 == nBools) {
            return dstInit;
        }
        if (nBools - 1 + dstPos >= 32) {
            throw new IllegalArgumentException(
                "nBools-1+dstPos is greather or equal to than 32");
        }
        int out = dstInit;
        int shift = 0;
        for (int i = 0; i < nBools; i++ ) {
            shift = i * 1 + dstPos;
            final int bits = (src[i + srcPos] ? 1 : 0) << shift;
            final int mask = 0x1 << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }


















    public static short binaryToShort(final boolean[] src, final int srcPos, final short dstInit, final int dstPos,
        final int nBools) {
        if ((src.length == 0 && srcPos == 0) || 0 == nBools) {
            return dstInit;
        }
        if (nBools - 1 + dstPos >= 16) {
            throw new IllegalArgumentException(
                "nBools-1+dstPos is greather or equal to than 16");
        }
        short out = dstInit;
        int shift = 0;
        for (int i = 0; i < nBools; i++ ) {
            shift = i * 1 + dstPos;
            final int bits = (src[i + srcPos] ? 1 : 0) << shift;
            final int mask = 0x1 << shift;
            out = (short)((out & ~mask) | bits);
        }
        return out;
    }


















    public static byte binaryToByte(final boolean[] src, final int srcPos, final byte dstInit, final int dstPos,
        final int nBools) {
        if ((src.length == 0 && srcPos == 0) || 0 == nBools) {
            return dstInit;
        }
        if (nBools - 1 + dstPos >= 8) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 8");
        }
        byte out = dstInit;
        int shift = 0;
        for (int i = 0; i < nBools; i++ ) {
            shift = i * 1 + dstPos;
            final int bits = (src[i + srcPos] ? 1 : 0) << shift;
            final int mask = 0x1 << shift;
            out = (byte)((out & ~mask) | bits);
        }
        return out;
    }


















    public static int[] longToIntArray(final long src, final int srcPos, final int[] dst, final int dstPos, final int nInts) {
        if (0 == nInts) {
            return dst;
        }
        if ((nInts - 1) * 32 + srcPos >= 64) {
            throw new IllegalArgumentException(
                "(nInts-1)*32+srcPos is greather or equal to than 64");
        }
        int shift = 0;
        for (int i = 0; i < nInts; i++ ) {
            shift = i * 32 + srcPos;
            dst[dstPos + i] = (int)(0xffffffff & (src >> shift));
        }
        return dst;
    }


















    public static short[] longToShortArray(final long src, final int srcPos, final short[] dst, final int dstPos,
        final int nShorts) {
        if (0 == nShorts) {
            return dst;
        }
        if ((nShorts - 1) * 16 + srcPos >= 64) {
            throw new IllegalArgumentException(
                "(nShorts-1)*16+srcPos is greather or equal to than 64");
        }
        int shift = 0;
        for (int i = 0; i < nShorts; i++ ) {
            shift = i * 16 + srcPos;
            dst[dstPos + i] = (short)(0xffff & (src >> shift));
        }
        return dst;
    }


















    public static short[] intToShortArray(final int src, final int srcPos, final short[] dst, final int dstPos,
        final int nShorts) {
        if (0 == nShorts) {
            return dst;
        }
        if ((nShorts - 1) * 16 + srcPos >= 32) {
            throw new IllegalArgumentException(
                "(nShorts-1)*16+srcPos is greather or equal to than 32");
        }
        int shift = 0;
        for (int i = 0; i < nShorts; i++ ) {
            shift = i * 16 + srcPos;
            dst[dstPos + i] = (short)(0xffff & (src >> shift));
        }
        return dst;
    }


















    public static byte[] longToByteArray(final long src, final int srcPos, final byte[] dst, final int dstPos,
        final int nBytes) {
        if (0 == nBytes) {
            return dst;
        }
        if ((nBytes - 1) * 8 + srcPos >= 64) {
            throw new IllegalArgumentException(
                "(nBytes-1)*8+srcPos is greather or equal to than 64");
        }
        int shift = 0;
        for (int i = 0; i < nBytes; i++ ) {
            shift = i * 8 + srcPos;
            dst[dstPos + i] = (byte)(0xff & (src >> shift));
        }
        return dst;
    }


















    public static byte[] intToByteArray(final int src, final int srcPos, final byte[] dst, final int dstPos, final int nBytes) {
        if (0 == nBytes) {
            return dst;
        }
        if ((nBytes - 1) * 8 + srcPos >= 32) {
            throw new IllegalArgumentException(
                "(nBytes-1)*8+srcPos is greather or equal to than 32");
        }
        int shift = 0;
        for (int i = 0; i < nBytes; i++ ) {
            shift = i * 8 + srcPos;
            dst[dstPos + i] = (byte)(0xff & (src >> shift));
        }
        return dst;
    }


















    public static byte[] shortToByteArray(final short src, final int srcPos, final byte[] dst, final int dstPos,
        final int nBytes) {
        if (0 == nBytes) {
            return dst;
        }
        if ((nBytes - 1) * 8 + srcPos >= 16) {
            throw new IllegalArgumentException(
                "(nBytes-1)*8+srcPos is greather or equal to than 16");
        }
        int shift = 0;
        for (int i = 0; i < nBytes; i++ ) {
            shift = i * 8 + srcPos;
            dst[dstPos + i] = (byte)(0xff & (src >> shift));
        }
        return dst;
    }

















    public static String longToHex(final long src, final int srcPos, final String dstInit, final int dstPos, final int nHexs) {
        if (0 == nHexs) {
            return dstInit;
        }
        if ((nHexs - 1) * 4 + srcPos >= 64) {
            throw new IllegalArgumentException(
                "(nHexs-1)*4+srcPos is greather or equal to than 64");
        }
        final StringBuilder sb = new StringBuilder(dstInit);
        int shift = 0;
        int append = sb.length();
        for (int i = 0; i < nHexs; i++ ) {
            shift = i * 4 + srcPos;
            final int bits = (int)(0xF & (src >> shift));
            if (dstPos + i == append) {
                ++append;
                sb.append(intToHexDigit(bits));
            } else {
                sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
        }
        return sb.toString();
    }

















    public static String intToHex(final int src, final int srcPos, final String dstInit, final int dstPos, final int nHexs) {
        if (0 == nHexs) {
            return dstInit;
        }
        if ((nHexs - 1) * 4 + srcPos >= 32) {
            throw new IllegalArgumentException(
                "(nHexs-1)*4+srcPos is greather or equal to than 32");
        }
        final StringBuilder sb = new StringBuilder(dstInit);
        int shift = 0;
        int append = sb.length();
        for (int i = 0; i < nHexs; i++ ) {
            shift = i * 4 + srcPos;
            final int bits = 0xF & (src >> shift);
            if (dstPos + i == append) {
                ++append;
                sb.append(intToHexDigit(bits));
            } else {
                sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
        }
        return sb.toString();
    }

















    public static String shortToHex(final short src, final int srcPos, final String dstInit, final int dstPos, final int nHexs) {
        if (0 == nHexs) {
            return dstInit;
        }
        if ((nHexs - 1) * 4 + srcPos >= 16) {
            throw new IllegalArgumentException(
                "(nHexs-1)*4+srcPos is greather or equal to than 16");
        }
        final StringBuilder sb = new StringBuilder(dstInit);
        int shift = 0;
        int append = sb.length();
        for (int i = 0; i < nHexs; i++ ) {
            shift = i * 4 + srcPos;
            final int bits = 0xF & (src >> shift);
            if (dstPos + i == append) {
                ++append;
                sb.append(intToHexDigit(bits));
            } else {
                sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
        }
        return sb.toString();
    }

















    public static String byteToHex(final byte src, final int srcPos, final String dstInit, final int dstPos, final int nHexs) {
        if (0 == nHexs) {
            return dstInit;
        }
        if ((nHexs - 1) * 4 + srcPos >= 8) {
            throw new IllegalArgumentException(
                "(nHexs-1)*4+srcPos is greather or equal to than 8");
        }
        final StringBuilder sb = new StringBuilder(dstInit);
        int shift = 0;
        int append = sb.length();
        for (int i = 0; i < nHexs; i++ ) {
            shift = i * 4 + srcPos;
            final int bits = 0xF & (src >> shift);
            if (dstPos + i == append) {
                ++append;
                sb.append(intToHexDigit(bits));
            } else {
                sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
        }
        return sb.toString();
    }


















    public static boolean[] longToBinary(final long src, final int srcPos, final boolean[] dst, final int dstPos,
        final int nBools) {
        if (0 == nBools) {
            return dst;
        }
        if (nBools - 1 + srcPos >= 64) {
            throw new IllegalArgumentException(
                "nBools-1+srcPos is greather or equal to than 64");
        }
        int shift = 0;
        for (int i = 0; i < nBools; i++ ) {
            shift = i * 1 + srcPos;
            dst[dstPos + i] = ((0x1 & (src >> shift)) != 0);
        }
        return dst;
    }


















    public static boolean[] intToBinary(final int src, final int srcPos, final boolean[] dst, final int dstPos,
        final int nBools) {
        if (0 == nBools) {
            return dst;
        }
        if (nBools - 1 + srcPos >= 32) {
            throw new IllegalArgumentException(
                "nBools-1+srcPos is greather or equal to than 32");
        }
        int shift = 0;
        for (int i = 0; i < nBools; i++ ) {
            shift = i * 1 + srcPos;
            dst[dstPos + i] = ((0x1 & (src >> shift)) != 0);
        }
        return dst;
    }


















    public static boolean[] shortToBinary(final short src, final int srcPos, final boolean[] dst, final int dstPos,
        final int nBools) {
        if (0 == nBools) {
            return dst;
        }
        if (nBools - 1 + srcPos >= 16) {
            throw new IllegalArgumentException(
                "nBools-1+srcPos is greather or equal to than 16");
        }
        int shift = 0;
        assert ((nBools - 1) * 1 < 16 - srcPos);
        for (int i = 0; i < nBools; i++ ) {
            shift = i * 1 + srcPos;
            dst[dstPos + i] = ((0x1 & (src >> shift)) != 0);
        }
        return dst;
    }


















    public static boolean[] byteToBinary(final byte src, final int srcPos, final boolean[] dst, final int dstPos,
        final int nBools) {
        if (0 == nBools) {
            return dst;
        }
        if (nBools - 1 + srcPos >= 8) {
            throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 8");
        }
        int shift = 0;
        for (int i = 0; i < nBools; i++ ) {
            shift = i * 1 + srcPos;
            dst[dstPos + i] = ((0x1 & (src >> shift)) != 0);
        }
        return dst;
    }

















    public static byte[] uuidToByteArray(final UUID src, final byte[] dst, final int dstPos, final int nBytes) {
        if (0 == nBytes) {
            return dst;
        }
        if (nBytes > 16) {
            throw new IllegalArgumentException("nBytes is greather than 16");
        }
        longToByteArray(src.getMostSignificantBits(), 0, dst, dstPos, nBytes > 8 ? 8 : nBytes);
        if (nBytes >= 8) {
            longToByteArray(src.getLeastSignificantBits(), 0, dst, dstPos + 8, nBytes - 8);
        }
        return dst;
    }














    public static UUID byteArrayToUuid(final byte[] src, final int srcPos) {
        if (src.length - srcPos < 16) {
            throw new IllegalArgumentException("Need at least 16 bytes for UUID");
        }
        return new UUID(byteArrayToLong(src, srcPos, 0, 0, 8), byteArrayToLong(
            src, srcPos + 8, 0, 0, 8));
    }
}
