package ru.deos.scrambler.arcane;

import org.apache.commons.lang3.ArrayUtils;
import ru.deos.scrambler.Settings;

/**
 * Created with IntelliJ IDEA.
 * User: Deos
 * Date: 4/4/14
 * Time: 6:27 PM
 */
public class Scrambler {
    byte[] scrambler;
    byte[] key;
    int length;

    public Scrambler() {
    }

    public Scrambler(byte[] scrambler, byte[] key, int length) {
        this.scrambler = scrambler.clone();
        this.key = key.clone();
        this.length = length;
    }

    public int step() {
        if(Settings.LOG_SCRAMBLER_KEY)
            return _logStep();
        else
            return _step();
    }

    private int _step() {
        byte[] value = andByteArrays(scrambler, key);
        int xor = 0;

        while(!isZero(value)) {
            xor ^= value[0] & 1;
            shiftRight(value);
        }

        int res = key[0] & 1;
        shiftRight(key);
        key[key.length - 1] |= xor * (1 << (length % 8 == 0 ? 7 : length % 8) - 1);

        return res;
    }

    private int _logStep() {
        byte[] value = andByteArrays(scrambler, key);
        int xor = 0;

        while(!isZero(value)) {
            xor ^= value[0] & 1;
            shiftRight(value);
        }

        int res = key[0] & 1;
        shiftRight(key);
        key[key.length - 1] |= xor * (1 << (length % 8 == 0 ? 7 : length % 8) - 1);

        System.out.print(byteArrayToBinaryString(key));
        System.out.print(" -> ");
        System.out.println(res);

        return res;
    }

    public byte getNextScrambledByte() {
        byte res = 0;
        for(int i = 7; i >= 0; i--) {
            res |= step() * (1 << i);
        }

        return res;
    }

    public byte[] getNextScrambledBytes(int n) {
        byte[] res = new byte[n];
        for(int i = n - 1; i >= 0; i--) {
            res[i] = getNextScrambledByte();
        }

        return res;
    }

    public boolean isZero(byte[] array) {
        for (byte b : array) {
            if(b != 0)
                return false;
        }

        return true;
    }

    public byte[] xorByteArrays(byte[] array1, byte[] array2) {
        byte[] xor = new byte[array1.length];
        for(int i = 0; i < array1.length; i++)
            xor[i] = (byte) (array1[i] ^ array2[i]);

        return xor;
    }

    public byte[] andByteArrays(byte[] array1, byte[] array2) {
        byte[] and = new byte[array1.length];
        for(int i = 0; i < array1.length; i++)
            and[i] = (byte) (array1[i] & array2[i]);

        return and;
    }

    public void shiftRight(byte[] array) {
        for(int i = 0; i < array.length; i++) {
            array[i] = (byte) ((array[i] >> 1) & 0b0111_1111);
            array[i] |= i + 1 >= array.length ? 0 : (array[i + 1] & 1) * -128;
        }
    }

    public String byteArrayToBinaryString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for(int i = array.length - 1; i >= 0; i--)
            sb.append(String.format("%8s", Integer.toBinaryString(array[i] & 0xFF)).replace(' ', '0'));

        return sb.toString();
    }

    public byte[] getScrambler() {
        return scrambler;
    }

    public void setScrambler(byte[] scrambler) {
        this.scrambler = scrambler;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
