















package org.apache.commons.lang3.mutable;










public class MutableInt extends Number implements Comparable<MutableInt>, Mutable<Number> {






    private static final long serialVersionUID = 512176391864L;

    
    private int value;




    public MutableInt() {
        super();
    }






    public MutableInt(final int value) {
        super();
        this.value = value;
    }







    public MutableInt(final Number value) {
        super();
        this.value = value.intValue();
    }








    public MutableInt(final String value) throws NumberFormatException {
        super();
        this.value = Integer.parseInt(value);
    }

    





    @Override
    public Integer getValue() {
        return Integer.valueOf(this.value);
    }






    public void setValue(final int value) {
        this.value = value;
    }







    @Override
    public void setValue(final Number value) {
        this.value = value.intValue();
    }

    





    public void increment() {
        value++;
    }






    public void decrement() {
        value--;
    }

    






    public void add(final int operand) {
        this.value += operand;
    }








    public void add(final Number operand) {
        this.value += operand.intValue();
    }







    public void subtract(final int operand) {
        this.value -= operand;
    }








    public void subtract(final Number operand) {
        this.value -= operand.intValue();
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

    





    public Integer toInteger() {
        return Integer.valueOf(intValue());
    }

    








    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof MutableInt) {
            return value == ((MutableInt) obj).intValue();
        }
        return false;
    }






    @Override
    public int hashCode() {
        return value;
    }

    






    @Override
    public int compareTo(final MutableInt other) {
        final int anotherVal = other.value;
        return value < anotherVal ? -1 : (value == anotherVal ? 0 : 1);
    }

    





    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
