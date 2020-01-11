/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package QuickSort;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;

public class QSortTest {
    QSort qSort = new QSort();
    @Test public void test1324() {
        int value[] = {1, 3, 2, 4};
        int collect[] = {1, 2, 3, 4};
        qSort.qsort(value, 0, 3);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void test1234() {
        int value[] = {1, 2, 3, 4};
        int collect[] = {1, 2, 3, 4};
        qSort.qsort(value, 0, 3);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void test4321() {
        int value[] = {4, 3, 2, 1};
        int collect[] = {1, 2, 3, 4};
        qSort.qsort(value, 0, 3);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void test1111() {
        int value[] = {1, 1, 1, 1};
        int collect[] = {1, 1, 1, 1};
        qSort.qsort(value, 0, 3);
        assertTrue(Arrays.equals(collect, value));
    }
}
