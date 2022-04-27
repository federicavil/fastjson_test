import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.TypeReference;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.StringReader;
import java.util.*;

@RunWith(value= Parameterized.class)
public class TestListStringField_Stream {

    private TestType type;
    private String value;
    private List<Object> expected;
    private JSONReader reader;


    @Parameterized.Parameters
    public static Collection<Object> getTestParameters(){
        return Arrays.asList(new Object[][]{
                {TestType.TEST_LIST, "{\"values\":[\"a\",null,\"b\",\"ab\\\\c\\\"\"]}", Arrays.asList(4, "a", null, "b", "ab\\c\"")}, //type, value, expected
                {TestType.TEST_NULL,"{\"values\":null}", null},
                {TestType.TEST_NULL,"{\"value\":[]}",null},
                {TestType.TEST_EMPTY,"{\"values\":[]}",Arrays.asList(0)},
                {TestType.TEST_MAP_EPTY,"{\"model\":{\"values\":[]}}", Arrays.asList(0)},
                {TestType.TEST_ERROR, "{\"values\":[1",null},
                {TestType.TEST_ERROR, "{\"values\":[\"b\"[",null},
                {TestType.TEST_ERROR, "{\"values\":[n",null},
                {TestType.TEST_ERROR, "{\"values\":[nu",null},
                {TestType.TEST_ERROR, "{\"values\":[nul",null},
                {TestType.TEST_ERROR, "{\"values\":[null",null},
                {TestType.TEST_MAP_ERROR, "{\"model\":{\"values\":[][",null},
                {TestType.TEST_MAP_ERROR, "{\"model\":{\"values\":[]}[",null},
                {TestType.TEST_MAP_ERROR, "{\"model\":{\"values\":[\"aaa]}[",null}
        });

    }

    public TestListStringField_Stream(TestType type, String value, List<Object> expected){
        this.type = type;
        this.value = value;
        this.expected =expected;
        this.reader = new JSONReader(new StringReader(this.value));
    }

    @Test
    public void test_list(){
        Assume.assumeTrue(type == TestType.TEST_LIST);
        Model model = this.reader.readObject(Model.class);
        Assert.assertEquals(this.expected.get(0),model.values.size());
        Assert.assertEquals(this.expected.get(1),model.values.get(0));
        Assert.assertEquals(this.expected.get(2),model.values.get(1));
        Assert.assertEquals(this.expected.get(3),model.values.get(2));
        Assert.assertEquals(this.expected.get(4),model.values.get(3));
    }

    @Test
    public void test_null(){
        Assume.assumeTrue(type == TestType.TEST_NULL);
        Model model = this.reader.readObject(Model.class);
        Assert.assertNull(model.values);
    }

    @Test
    public void test_empty(){
        Assume.assumeTrue(type == TestType.TEST_EMPTY);
        Model model = this.reader.readObject(Model.class);
        Assert.assertEquals(this.expected.get(0), model.values.size());
    }

    @Test
    public void test_map_empty(){
        Assume.assumeTrue(type == TestType.TEST_MAP_EPTY);
        Map<String, Model> map = reader.readObject(new TypeReference<>() {
        });
        Model model = map.get("model");
        Assert.assertEquals(this.expected.get(0), model.values.size());
    }

    @Test
    public void test_error() {
        Assume.assumeTrue(type == TestType.TEST_ERROR);
        Exception error = null;
        try {
            this.reader.readObject(Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    @Test
    public void test_map_error() {
        Assume.assumeTrue(type == TestType.TEST_MAP_ERROR);
        Exception error = null;
        try {
            this.reader.readObject(new TypeReference<Map<String, Model>>() {
            });
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    enum TestType {TEST_LIST,TEST_NULL,TEST_EMPTY,TEST_MAP_EPTY,TEST_MAP_ERROR,TEST_ERROR}

    public static class Model {

        private List<String> values;

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

    }
}
