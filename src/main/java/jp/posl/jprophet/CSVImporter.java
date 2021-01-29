package jp.posl.jprophet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.fl.manualspecification.strategy.*;

public class CSVImporter {
    private String filePath;

    public CSVImporter(String filePath) {
        this.filePath = filePath;
    }

    public List<SpecificationStrategy> getSpecifications() {
        List<SpecificationStrategy> specifications = new ArrayList<SpecificationStrategy>();
        BufferedReader br = null;
        try {
            File file = new File(this.filePath);
            br = new BufferedReader(new FileReader(file));
            String line;
            String[] data;
            while ((line = br.readLine()) != null) {
                data = line.split(",");
                specifications.add(new SpecificOneLineBug(data[0], Integer.parseInt(data[1]), Double.parseDouble(data[2])));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return specifications;
    }
    
}
