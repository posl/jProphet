















package org.apache.commons.lang3.mutable;










public class MutableFloat extends Number implements Comparable<MutableFloat>, Mutable<Number> {






    private static final long serialVersionUID = 5787169186L;

    
    private float value;




    public MutableFloat() {
        super();
    }






    public MutableFloat(final float value) {
        super();
        this.value = value;
    }







    public MutableFloat(final Number value) {
        super();
        this.value = value.floatValue();
    }








    public MutableFloat(final String value) throws NumberFormatException {
        super();
        this.value = Float.parseFloat(value);
    }

    





    @Override
    public Float getValue() {
        return Float.valueOf(this.value);
    }






    public void setValue(final float value) {
        this.value = value;
    }







    @Override
    public void setValue(final Number value) {
        this.value = value.floatValue();
    }

    





    public boolean isNaN() {
        return Float.isNaN(value);
    }






    public boolean isInfinite() {
        return Float.isInfinite(value);
    }

    





    public void increment() {
        value++;
    }






    public void decrement() {
        value--;
    }

    






    public void add(final float operand) {
        this.value += operand;
    }








    public void add(final Number operand) {
        this.value += operand.floatValue();
    }







    public void subtract(final float operand) {
        this.value -= operand;
    }








    public void subtract(final Number operand) {
        this.value -= operand.floatValue();
    }

    
    





    @Override
    public int intValue() {
        return (int) value;
    }






    @Override
    public long longValue() {
        return (long) value;
    }






    @Override
    public float floatValue() {
        return value;
    }






    @Override
    public double doubleValue() {
        return value;
    }

    





    public Float toFloat() {
        return Float.valueOf(floatValue());
    }

    































    @Override
    public boolean equals(final Object obj) {
        return obj instanceof MutableFloat
            && Float.floatToIntBits(((MutableFloat) obj).value) == Float.floatToIntBits(value);
    }






    @Override
    public int hashCode() {
        return Float.floatToIntBits(value);
    }

    






    @Override
    public int compareTo(final MutableFloat other) {
        final float anotherVal = other.value;
        return Float.compare(value, anotherVal);
    }

    





    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
