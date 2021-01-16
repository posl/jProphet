package jp.posl.jprophet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

import org.junit.Test;

import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;

public class PatchSwitcherTest {
    @Test public void test() {
        final String buildPath = "src/test/resources/binary/a";
        final Path buildDir = Paths.get(buildPath);
        BiPredicate<Path, BasicFileAttributes> matcher = (path, attr) -> {
            if (attr.isRegularFile() && path.getFileName().toString().endsWith(".class")) {
                return true;
            }
            return false;
        };

        final Map<Path, byte[]> pathToContent = new HashMap<Path, byte[]>();
        try {
            final List<Path> paths = Files.find(buildDir, Integer.MAX_VALUE, matcher)
                .collect(Collectors.toList());
            for (Path path : paths) {
                final byte[] content = Files.readAllBytes(path);
                pathToContent.put(path, content);
            }
            final PatchSwitcher switcher = new PatchSwitcher(pathToContent);
            final PatchCandidate candidate = new PatchCandidate(new OperationDiff(ModifyType.INSERT, null, null), null, null, AstOperation.class, 0);
            switcher.rewrite(candidate);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
