















package org.apache.commons.lang3.tuple;

import java.io.Serializable;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

















public abstract class Triple<L, M, R> implements Comparable<Triple<L, M, R>>, Serializable {

    
    private static final long serialVersionUID = 1L;















    public static <L, M, R> Triple<L, M, R> of(final L left, final M middle, final R right) {
        return new ImmutableTriple<L, M, R>(left, middle, right);
    }

    





    public abstract L getLeft();






    public abstract M getMiddle();






    public abstract R getRight();

    








    @Override
    public int compareTo(final Triple<L, M, R> other) {
      return new CompareToBuilder().append(getLeft(), other.getLeft())
          .append(getMiddle(), other.getMiddle())
          .append(getRight(), other.getRight()).toComparison();
    }







    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Triple<?, ?, ?>) {
            final Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
            return ObjectUtils.equals(getLeft(), other.getLeft())
                && ObjectUtils.equals(getMiddle(), other.getMiddle())
                && ObjectUtils.equals(getRight(), other.getRight());
        }
        return false;
    }






    @Override
    public int hashCode() {
        return (getLeft() == null ? 0 : getLeft().hashCode()) ^
            (getMiddle() == null ? 0 : getMiddle().hashCode()) ^
            (getRight() == null ? 0 : getRight().hashCode());
    }






    @Override
    public String toString() {
        return new StringBuilder().append('(').append(getLeft()).append(',').append(getMiddle()).append(',')
            .append(getRight()).append(')').toString();
    }












    public String toString(final String format) {
        return String.format(format, getLeft(), getMiddle(), getRight());
    }

}

