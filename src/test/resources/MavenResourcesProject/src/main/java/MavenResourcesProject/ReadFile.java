package MavenResourcesProject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
 
public class ReadFile {

    public ReadFile(String str) {
        try {
            InputStream is = this.getClass().getResourceAsStream(str);
            Properties props = new Properties();
            props.load(is);
            is.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        
    }
 
    public static void main() {
        System.out.println("");
    }
 
}