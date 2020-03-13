















package org.apache.commons.lang3.mutable;










public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number> {






    private static final long serialVersionUID = -1585823265L;

    
    private byte value;




    public MutableByte() {
        super();
    }






    public MutableByte(final byte value) {
        super();
        this.value = value;
    }







    public MutableByte(final Number value) {
        super();
        this.value = value.byteValue();
    }








    public MutableByte(final String value) throws NumberFormatException {
        super();
        this.value = Byte.parseByte(value);
    }

    





    @Override
    public Byte getValue() {
        return Byte.valueOf(this.value);
    }






    public void setValue(final byte value) {
        this.value = value;
    }







    @Override
    public void setValue(final Number value) {
        this.value = value.byteValue();
    }

    





    public void increment() {
        value++;
    }






    public void decrement() {
        value--;
    }

    






    public void add(final byte operand) {
        this.value += operand;
    }








    public void add(final Number operand) {
        this.value += operand.byteValue();
    }







    public void subtract(final byte operand) {
        this.value -= operand;
    }








    public void subtract(final Number operand) {
        this.value -= operand.byteValue();
    }

    
    





    @Override
    public byte byteValue() {
        return value;
    }






    @Override
    public int intValue() {
        return value;
    }






    @Override
    public long longValue() {
        return value;
    }






    @Override
    public float floatValue() {
        return value;
    }






    @Override
    public double doubleValue() {
        return value;
    }

    





    public Byte toByte() {
        return Byte.valueOf(byteValue());
    }

    








    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof MutableByte) {
            return value == ((MutableByte) obj).byteValue();
        }
        return false;
    }






    @Override
    public int hashCode() {
        return value;
    }

    






    @Override
    public int compareTo(final MutableByte other) {
        final byte anotherVal = other.value;
        return value < anotherVal ? -1 : (value == anotherVal ? 0 : 1);
    }

    





    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
