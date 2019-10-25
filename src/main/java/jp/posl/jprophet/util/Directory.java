package jp.posl.jprophet.util;

import java.io.File;

public class Directory {

    /**
     * ディレクトリをディレクトリの中のファイルごと再帰的に削除する 
     * @param dir 削除対象ディレクトリ
     */
    public static void delete(File dir){
        if(dir.listFiles() != null){
            for(File file : dir.listFiles()){
                if(file.isFile())
                    file.delete();
                else
                    delete(file);
            }
        }
        dir.delete();
    }
}