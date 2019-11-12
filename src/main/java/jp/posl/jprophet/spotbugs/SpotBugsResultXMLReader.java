package jp.posl.jprophet.spotbugs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class SpotBugsResultXMLReader {


    /**
     * SpotBugsの出力結果ファイルから全てのワーニングの情報を抜き取る
     * @param filePath 対象のXMLファイル
     * @return ワーニング情報クラスのリスト
     */
    public List<SpotBugsWarning> readAllSpotBugsWarnings(String filePath) {
        final SAXReader reader = new SAXReader();
        List<SpotBugsWarning> warnings = new ArrayList<SpotBugsWarning>();
        try {
            final Document document = reader.read(filePath);
            final List<Node> bugInstances = document.selectNodes("//BugInstance");
            warnings = bugInstances.stream().map(bug -> readSpotBugsWarning((Element) bug)).collect(Collectors.toList());
        }
        catch (DocumentException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return warnings;
    }


    /**
     * XML中のBugInstance要素から必要な情報を抜き取る
     * @param bugInstance XML中のBugInstance要素
     * @return ワーニング情報クラス
     */
    private SpotBugsWarning readSpotBugsWarning(Element bugInstance) {
        final String type = bugInstance.attributeValue("type");
        final List<Node> sourceLines = new ArrayList<Node>();
        sourceLines.addAll(bugInstance.selectNodes("./SourceLine"));
        sourceLines.addAll(bugInstance.selectNodes("./Method/SourceLine"));
        sourceLines.addAll(bugInstance.selectNodes("./Class/SourceLine"));
        final Element targetLine = (Element) sourceLines.get(0);
        final String filePath = targetLine.attributeValue("sourcepath");
        final int start = Integer.parseInt(targetLine.attributeValue("start"));
        final int end = Integer.parseInt(targetLine.attributeValue("end"));
        return new SpotBugsWarning(type, filePath, start, end);
    }


}