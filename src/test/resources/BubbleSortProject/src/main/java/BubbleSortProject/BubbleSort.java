package BubbleSortProject;

public class BubbleSort {
    public void sort(int[] value) {
        for (int i = 0; i < value.length - 1; i++) {
            for (int j = value.length - 1; i < j; j--) {
                if (value[j] < value[j - 1])
                    swap(value, j, j - 1);
            }
        }
    }

    public void swap(int[] value, int i, int j){
        int tmp = value[i];
        value[i] = value[j];
        value[j] = tmp;
    }
}
