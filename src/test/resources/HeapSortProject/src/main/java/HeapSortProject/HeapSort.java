package HeapSortProject;

public class HeapSort {

    public int heap[];
    public int num;
    int target;

    public void sort(int[] value) {
        heap = new int[value.length];
        num = 0;

        for (target = 0; target < value.length; target++) {
            insert(value[target]);
        }

        for (target = 0; num > 0; target++){
            value[target] = deletemin();
        }
    }

    

    public int deletemin(){
        int r = heap[0];
        heap[0] = heap[--num];
        int i = 1;
        int j = i * 2;
        while (j <= num){
            if (j + 1 <= num && heap[j - 1] > heap[j]){
                j++;
            }
            if (heap[i - 1] > heap[j - 1]){
                swap(heap, i - 1, j - 1);
            }
            i = j;
            j = i * 2;
        }
        return r;
    }

    public void insert(int a){
        heap[num++] = a;
        int i = num;
        int j = i / 2;
        while (i > 1 && heap[i - 1] < heap[j - 1]){
            swap(heap, i - 1, j - 1);
            i = j;
            j = i / 2;
        }
    }

    public void swap(int[] value, int i, int j){
        int tmp = value[i];
        value[i] = value[j];
        value[j] = value[j];
    }

}
