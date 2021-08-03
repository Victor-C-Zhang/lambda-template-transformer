package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.utils.GenerateTemplateSchemaFromDefaults;
import org.junit.jupiter.api.Test;

public class GeneratorTest {
    @Test
    void test() throws NoSuchFieldException, JsonProcessingException, IllegalAccessException {
        GenerateTemplateSchemaFromDefaults g = new GenerateTemplateSchemaFromDefaults();
        System.out.println(g.generateTemplateSchemaFromDefaults());
    }
}
