package jp.posl.jprophet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class TestExecutor {
	public TestExecutor() {

	}

	public boolean test(ProjectConfiguration projectConfiguration) {


        Runtime runtime = Runtime.getRuntime();

        String[] Command = { "python", "./TestProject/test.py" };

        Process p = null;
        File dir = new File("./");

        String line = "";

        try {
        	p = runtime.exec(Command, null, dir);
            p.waitFor();
            BufferedReader br =
              new BufferedReader(new InputStreamReader(p.getInputStream()));
            line = br.readLine();
          } catch (IOException e) {
             System.out.print(e);
          } catch (InterruptedException e) {
             System.out.print(e);
          }

        if(line.equals("TRUE")) return true;
        else return false;


	}
}
