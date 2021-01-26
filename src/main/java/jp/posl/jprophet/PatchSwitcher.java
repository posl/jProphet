package jp.posl.jprophet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;

public class PatchSwitcher {
    final Map<Path, byte[]> pathToContent;

    public PatchSwitcher(Map<Path, byte[]> pathToContent) {
        this.pathToContent = pathToContent;
    }

    public Optional<Path> rewrite(PatchCandidate candidate) {
        try {
            boolean finishTargetSearch = false;
            boolean finishPatchSearch = false;
            for (Map.Entry<Path, byte[]> entry : this.pathToContent.entrySet()) {
                final byte[] content = entry.getValue();
                for (int j = 0; j < content.length - 2; j++) {
                    final Byte prefix1 = (byte)0x29;
                    final Byte prefix2 = (byte)0x87;
                    final Byte targetValueSuffix = (byte)(0x4a + candidate.getId());
                    final Byte patchValueSuffix = (byte)(0x40 + candidate.getId());
                    if (content[j] == prefix1) {
                        if (content[j + 1] == prefix2) {
                            if (candidate.getOperationDiff().getModifyType() == ModifyType.INSERT) {
                                if (content[j + 2] == targetValueSuffix) {
                                    content[j] = (byte)0x00;
                                    content[j + 1] = (byte)0x72;
                                    content[j + 2] = (byte)0x6f;
                                    finishTargetSearch = true;
                                }
                            }
                            else {
                                finishTargetSearch = true;
                            }
                            if (content[j + 2] == patchValueSuffix) {
                                content[j] = (byte)0x04;
                                content[j + 1] = (byte)0x78;
                                content[j + 2] = (byte)0x3d;
                                finishPatchSearch = true;
                            }
                        }
                    }
                    if (finishPatchSearch && finishTargetSearch) {
                        break;
                    }
                }
                if (finishPatchSearch && finishTargetSearch) {
                    Files.write(entry.getKey(), content);
                    return Optional.of(entry.getKey());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return Optional.empty();
    }
}
