package QuickSort;

public class QSort {
    public void qsort(int[] value, int left, int right) {
        int i = left;
        int j = right;
        int pivot = value[(left + right) / 2];

        while (true) {
            while (value[i] < pivot)
                i++;
            while (pivot < value[j])
                j--;
            if (i >= j)
                break;
            swap(value, i, j);
            i++;
            j--;
        }

        if (left < i - 1)
            qsort(value, left, i - 1);
        if (j + 1 < right)
            qsort(value, j + 1, right);
    }

    public void swap(int[] value, int i, int j){
        int tmp = value[i];
        value[i] = value[j];
        value[j] = value[i];
    }
}
