package MavenResourcesProject;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        ReadFile rf = new ReadFile("/test.txt");
        ReadFile rfd = new ReadFile("/dir/dir.txt");
        ReadFile rfd2 = new ReadFile("/dir/dir2/dir2.txt");
    }
}
