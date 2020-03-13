
















package org.apache.commons.lang3.mutable;

import java.io.Serializable;








public class MutableObject<T> implements Mutable<T>, Serializable {






    private static final long serialVersionUID = 86241875189L;

    
    private T value;




    public MutableObject() {
        super();
    }






    public MutableObject(final T value) {
        super();
        this.value = value;
    }

    





    @Override
    public T getValue() {
        return this.value;
    }






    @Override
    public void setValue(final T value) {
        this.value = value;
    }

    












    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() == obj.getClass()) {
            final MutableObject<?> that = (MutableObject<?>) obj;
            return this.value.equals(that.value);
        } else {
            return false;
        }
    }






    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }

    





    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }

}
