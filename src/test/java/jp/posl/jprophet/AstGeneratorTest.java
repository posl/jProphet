package jp.posl.jprophet;

import jp.posl.jprophet.AstGenerator;
import org.junit.Test;                                                                                                                                                                  
import static org.assertj.core.api.Assertions.*;
import java.util.List;

public class AstGeneratorTest {
    private final String source = new StringBuilder().append("")
        .append("public class A {\n")
        .append("   public void a() {\n")
        .append("   }\n")
        .append("}\n")
        .toString();
    private List<RepairUnit> candidates = new AstGenerator().getAllRepairUnit(source);

    @Test public void testForGeneratedAst() {
        assertThat(this.candidates.size()).isEqualTo(6);
        assertThat(this.candidates.get(0).getNode().toString()).isEqualTo("public class A {\n\n    public void a() {\n    }\n}");
        assertThat(this.candidates.get(1).getNode().toString()).isEqualTo("A");
        assertThat(this.candidates.get(2).getNode().toString()).isEqualTo("public void a() {\n}");
        assertThat(this.candidates.get(3).getNode().toString()).isEqualTo("a");
        assertThat(this.candidates.get(4).getNode().toString()).isEqualTo("void");
        assertThat(this.candidates.get(5).getNode().toString()).isEqualTo("{\n}");
    }   
}