















package org.apache.commons.lang3.tuple;












public class MutableTriple<L, M, R> extends Triple<L, M, R> {

    
    private static final long serialVersionUID = 1L;

    
    public L left;
    
    public M middle;
    
    public R right;















    public static <L, M, R> MutableTriple<L, M, R> of(final L left, final M middle, final R right) {
        return new MutableTriple<L, M, R>(left, middle, right);
    }




    public MutableTriple() {
        super();
    }








    public MutableTriple(final L left, final M middle, final R right) {
        super();
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    



    @Override
    public L getLeft() {
        return left;
    }






    public void setLeft(final L left) {
        this.left = left;
    }




    @Override
    public M getMiddle() {
        return middle;
    }






    public void setMiddle(final M middle) {
        this.middle = middle;
    }




    @Override
    public R getRight() {
        return right;
    }






    public void setRight(final R right) {
        this.right = right;
    }
}

