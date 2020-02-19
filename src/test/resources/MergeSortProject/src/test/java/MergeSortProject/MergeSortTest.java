/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package MergeSortProject;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;

public class MergeSortTest {
    MergeSort qSort = new MergeSort();

    
    @Test public void testAppHasAGreeting() {
        int value[] = {1, 2, 3, 4};
        int collect[] = {1, 2, 3, 4};
        qSort.sort(value);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void testAppHasAGreeting2() {
        int value[] = {1, 1, 2, 1};
        int collect[] = {1, 1, 1, 2};
        qSort.sort(value);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void testAppHasAGreeting3() {
        int value[] = {1, 1, 2};
        int collect[] = {1, 1, 2};
        qSort.sort(value);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void testAppHasAGreeting4() {
        int value[] = {1};
        int collect[] = {1};
        qSort.sort(value);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void testAppHasAGreeting5() {
        int value[] = {2, 1};
        int collect[] = {1, 2};
        qSort.sort(value);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void testAppHasAGreeting6() {
        int value[] = {3, 2, 1};
        int collect[] = {1, 2, 3};
        qSort.sort(value);
        assertTrue(Arrays.equals(collect, value));
    }
    

    @Test public void testAppHasAGreeting7() {
        int value[] = {4, 3, 2, 1};
        int collect[] = {1, 2, 3, 4};
        qSort.sort(value);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void testAppHasAGreeting8() {
        int value[] = {1, 3, 2, 4};
        int collect[] = {1, 2, 3, 4};
        qSort.sort(value);
        assertTrue(Arrays.equals(collect, value));
    }

    @Test public void testAppHasAGreeting9() {
        int value[] = {5, 4, 3, 2, 1};
        int collect[] = {1, 2, 3, 4, 5};
        qSort.sort(value);
        assertTrue(Arrays.equals(collect, value));
    }
}
