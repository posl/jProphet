package jp.posl.jprophet.trainingcase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;

import jp.posl.jprophet.trainingcase.TrainingCaseGenerator.TrainingCase;


/**
 * トレーニングケースの出力を行うクラス
 */
public class TrainingCaseExporter {
    /**
     * Gsonを用いてJson形式に変換するためのTrainingCaseの表現クラス
     */
    static class TrainingCaseForJson {
        final private List<Integer> correct;
        final private List<List<Integer>> generated;

        public TrainingCaseForJson(List<Integer> correct, List<List<Integer>> generated) {
            this.correct = correct;
            this.generated = generated;
        }
    }

    /**
     * トレーニングケースをjsonファイルとして出力する 
     * @param exportPathName 出力先パス
     * @param cases トレーニングケースのリスト
     */
    public void export(String exportPathName, List<TrainingCase> cases) {
        final List<TrainingCaseForJson> trainingCasesForJson = new ArrayList<>();
        for (TrainingCase trainingCase : cases) {
            final List<Integer> collectVector = trainingCase.vectorOfCorrectPatch.asIntegerList();
            final List<List<Integer>> generatedVector = trainingCase.vectorsOfGeneratedPatch.stream()
                    .map(vector -> vector.asIntegerList())
                    .collect(Collectors.toList());
            trainingCasesForJson.add(new TrainingCaseForJson(collectVector, generatedVector));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(trainingCasesForJson);
        try {
            FileUtils.write(new File(exportPathName), json, "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
} 