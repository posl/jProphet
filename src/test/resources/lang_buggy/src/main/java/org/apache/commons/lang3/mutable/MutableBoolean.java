
















package org.apache.commons.lang3.mutable;

import java.io.Serializable;










public class MutableBoolean implements Mutable<Boolean>, Serializable, Comparable<MutableBoolean> {






    private static final long serialVersionUID = -4830728138360036487L;

    
    private boolean value;




    public MutableBoolean() {
        super();
    }






    public MutableBoolean(final boolean value) {
        super();
        this.value = value;
    }







    public MutableBoolean(final Boolean value) {
        super();
        this.value = value.booleanValue();
    }

    





    @Override
    public Boolean getValue() {
        return Boolean.valueOf(this.value);
    }






    public void setValue(final boolean value) {
        this.value = value;
    }







    @Override
    public void setValue(final Boolean value) {
        this.value = value.booleanValue();
    }

    






    public boolean isTrue() {
        return value == true;
    }







    public boolean isFalse() {
        return value == false;
    }

    





    public boolean booleanValue() {
        return value;
    }

    






    public Boolean toBoolean() {
        return Boolean.valueOf(booleanValue());
    }

    








    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof MutableBoolean) {
            return value == ((MutableBoolean) obj).booleanValue();
        }
        return false;
    }






    @Override
    public int hashCode() {
        return value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
    }

    







    @Override
    public int compareTo(final MutableBoolean other) {
        final boolean anotherVal = other.value;
        return value == anotherVal ? 0 : (value ? 1 : -1);
    }

    





    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
