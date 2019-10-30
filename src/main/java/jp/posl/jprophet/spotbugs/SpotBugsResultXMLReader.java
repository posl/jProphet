package jp.posl.jprophet.spotbugs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public List<BugInstance> readAllBugInstances(String filePath) {
        final List<BugInstance> bugList = new ArrayList<BugInstance>();
        final SAXReader reader = new SAXReader();
        try {
            final Document document = reader.read(filePath);
            final List bugs = document.selectNodes("//BugInstance");
            for (Iterator i = bugs.iterator(); i.hasNext();) {
                final Element bug = (Element) i.next();
                bugList.add(readBugInstance(bug));
            }
        }
        catch (DocumentException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return bugList;
    }


    private BugInstance readBugInstance(Element element) {
        final String type = element.attributeValue("type");
        final List<Node> sourceLines = new ArrayList<Node>();
        sourceLines.addAll(element.selectNodes("./SourceLine"));
        sourceLines.addAll(element.selectNodes("./Method/SourceLine"));
        sourceLines.addAll(element.selectNodes("./Class/SourceLine"));
        final Element targetLine = (Element) sourceLines.get(0);
        final String filePath = targetLine.attributeValue("sourcepath");
        final int start = Integer.parseInt(targetLine.attributeValue("start"));
        final int end = Integer.parseInt(targetLine.attributeValue("end"));
        return new BugInstance(type, filePath, start, end);
    }


}