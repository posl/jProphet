package BynarySearchProject;

public class BynarySearch {

    public int search(int[] ar, int target) {
		int l = 0;
		int r = ar.length;
        int index = -1;
		
		while (l < r) {
			int m = (l + r)/2;
			if (ar[m] == target) {
                index = l;
				break;
			} else if (ar[m] > target) {
                r = m;
            } else if (ar[m] < target) {
                l = m + 1;
            }
        }
        
        return index;
	}
}
