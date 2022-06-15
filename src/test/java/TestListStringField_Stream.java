import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.TypeReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.runners.Suite;

import java.io.StringReader;
import java.util.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestListStringField_Stream.TestList.class, TestListStringField_Stream.TestNull.class,
                      TestListStringField_Stream.TestEmpty.class, TestListStringField_Stream.TestMapEmpty.class,
                      TestListStringField_Stream.TestNotMatch.class, TestListStringField_Stream.TestError.class,
                      TestListStringField_Stream.TestMapError.class})
public class TestListStringField_Stream {

    @RunWith(value = Parameterized.class)
    public static class TestList{

        private String value;

        public TestList(String value){
            this.configure(value);
        }

        private void configure(String value){
            this.value = value;
        }

        @Parameterized.Parameters
        public static Collection<Object> getTestParameters(){
            return Arrays.asList(new Object[][]{
                    {"{\"values\":[\"a\",null,\"b\",\"ab\\\\c\\\"\"]}"}, //value
            });
        }

        @Test
        public void test_list(){
            JSONReader reader = new JSONReader(new StringReader(this.value));
            Model model = reader.readObject(Model.class);

            List<Object> expected = new Oracle(this.value).parsingValues();
            Assert.assertEquals(expected.size(),model.getValues().size());
            for(int i = 0; i < expected.size(); i++){
                Assert.assertEquals(expected.get(i),model.getValues().get(i));
            }
        }
    }

    @RunWith(value = Parameterized.class)
    public static class TestNull{

        private String value;

        public TestNull(String value){
            this.configure(value);
        }

        private void configure(String value){
            this.value = value;
        }

        @Parameterized.Parameters
        public static Collection<Object> getTestParameters(){
            return Arrays.asList(new Object[][]{
                    {"{\"values\":null}"}, //value
            });
        }

        @Test
        public void test_null(){
            JSONReader reader = new JSONReader(new StringReader(this.value));
            Model model = reader.readObject(Model.class);
            Assert.assertNull(model.getValues());
        }
    }

    @RunWith(value = Parameterized.class)
    public static class TestEmpty{

        private String value;

        public TestEmpty(String value){
            this.configure(value);
        }

        private void configure(String value){
            this.value = value;
        }

        @Parameterized.Parameters
        public static Collection<Object> getTestParameters(){
            return Arrays.asList(new Object[][]{
                    {"{\"values\":[]}"}, //value
            });
        }

        @Test
        public void test_empty(){
            JSONReader reader = new JSONReader(new StringReader(this.value));
            Model model = reader.readObject(Model.class);
            Assert.assertEquals(0, model.values.size());
        }
    }

    @RunWith(value = Parameterized.class)
    public static class TestMapEmpty{

        private String value;

        public TestMapEmpty(String value){
            this.configure(value);
        }

        private void configure(String value){
            this.value = value;
        }

        @Parameterized.Parameters
        public static Collection<Object> getTestParameters(){
            return Arrays.asList(new Object[][]{
                    {"{\"model\":{\"values\":[]}}"}, //value
            });
        }

        @Test
        public void test_map_empty(){
            JSONReader reader = new JSONReader(new StringReader(this.value));
            Map<String, Model> map = reader.readObject(new TypeReference<>() {
            });
            Model model = map.get("model");
            Assert.assertEquals(0, model.values.size());
        }
    }

    @RunWith(value = Parameterized.class)
    public static class TestNotMatch{

        private String value;

        public TestNotMatch(String value){
            this.configure(value);
        }

        private void configure(String value){
            this.value = value;
        }

        @Parameterized.Parameters
        public static Collection<Object> getTestParameters(){
            return Arrays.asList(new Object[][]{
                    {"{\"value\":[]}"}, //value
            });
        }

        @Test
        public void test_notMatch(){
            JSONReader reader = new JSONReader(new StringReader(this.value));
            Model model = reader.readObject(Model.class);
            Assert.assertNull(model.getValues());
        }
    }

    @RunWith(value = Parameterized.class)
    public static class TestError{

        private String value;

        public TestError(String value){
            this.configure(value);
        }

        private void configure(String value){
            this.value = value;
        }

        @Parameterized.Parameters
        public static Collection<Object> getTestParameters(){
            return Arrays.asList(new Object[][]{
                    {"{\"values\":[1"},//value
                    {"{\"values\":[\"b\"["},
                    {"{\"values\":[n"},
                    {"{\"values\":[nu"},
                    {"{\"values\":[nul"},
                    {"{\"values\":[null"},
                    {"{\"values\":[null,]"}
            });
        }

        @Test
        public void test_error(){
            Exception error = null;
            try {
                JSONReader reader = new JSONReader(new StringReader(this.value));
                reader.readObject(Model.class);
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    @RunWith(value = Parameterized.class)
    public static class TestMapError{

        private String value;

        public TestMapError(String value){
            this.configure(value);
        }

        private void configure(String value){
            this.value = value;
        }

        @Parameterized.Parameters
        public static Collection<Object> getTestParameters(){
            return Arrays.asList(new Object[][]{
                    {"{\"model\":{\"values\":[]["},//value
                    {"{\"model\":{\"values\":[]}["},
                    {"{\"model\":{\"values\":[\"aaa]}["}
            });
        }

        @Test
        public void test_map_error() {
            Exception error = null;
            try {
                JSONReader reader = new JSONReader(new StringReader(this.value));
                reader.readObject(new TypeReference<Map<String, Model>>() {
                });
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public static class Oracle{

        private String jsonString;

        public Oracle(){}

        public Oracle(String jsonString){
            this.jsonString = jsonString;
        }

        public List<Object> parsingValues(){
            JSONObject obj = new JSONObject(jsonString);
            JSONArray array = obj.getJSONArray("values");
            List<Object> values = new ArrayList<>();
            for(Object value: array){
                values.add(value);
            }
            return values;
        }
    }

    public static class Model {

        private List<String> values;

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

    }

    /*
    package com.alibaba.json.bvt.parser.deser.list;

    import java.io.StringReader;
    import java.util.List;
    import java.util.Map;

    import org.junit.Assert;

    import com.alibaba.fastjson.JSONException;
    import com.alibaba.fastjson.JSONReader;
    import com.alibaba.fastjson.TypeReference;

    import junit.framework.TestCase;

    public class ListStringFieldTest_stream extends TestCase {

        public void test_list() throws Exception {
            String text = "{\"values\":[\"a\",null,\"b\",\"ab\\\\c\\\"\"]}";

            JSONReader reader = new JSONReader(new StringReader(text));
            Model model = reader.readObject(Model.class);
            Assert.assertEquals(4, model.values.size());
            Assert.assertEquals("a", model.values.get(0));
            Assert.assertEquals(null, model.values.get(1));
            Assert.assertEquals("b", model.values.get(2));
            Assert.assertEquals("ab\\c\"", model.values.get(3));
        }

        public void test_null() throws Exception {
            String text = "{\"values\":null}";
            JSONReader reader = new JSONReader(new StringReader(text));
            Model model = reader.readObject(Model.class);
            Assert.assertNull(model.values);
        }

        public void test_empty() throws Exception {
            String text = "{\"values\":[]}";
            JSONReader reader = new JSONReader(new StringReader(text));
            Model model = reader.readObject(Model.class);
            Assert.assertEquals(0, model.values.size());
        }

        public void test_map_empty() throws Exception {
            String text = "{\"model\":{\"values\":[]}}";
            JSONReader reader = new JSONReader(new StringReader(text));
            Map<String, Model> map = reader.readObject(new TypeReference<Map<String, Model>>() {
            });
            Model model = (Model) map.get("model");
            Assert.assertEquals(0, model.values.size());
        }

        public void test_notMatch() throws Exception {
            String text = "{\"value\":[]}";
            JSONReader reader = new JSONReader(new StringReader(text));
            Model model = reader.readObject(Model.class);
            Assert.assertNull(model.values);
        }

        public void test_error() throws Exception {
            String text = "{\"values\":[1";
            JSONReader reader = new JSONReader(new StringReader(text));

            Exception error = null;
            try {
                reader.readObject(Model.class);
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        public void test_error_1() throws Exception {
            String text = "{\"values\":[\"b\"[";
            JSONReader reader = new JSONReader(new StringReader(text));

            Exception error = null;
            try {
                reader.readObject(Model.class);
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        public void test_error_2() throws Exception {
            String text = "{\"model\":{\"values\":[][";
            JSONReader reader = new JSONReader(new StringReader(text));


            Exception error = null;
            try {
                reader.readObject(new TypeReference<Map<String, Model>>() {
                });
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        public void test_error_3() throws Exception {
            String text = "{\"model\":{\"values\":[]}[";
            JSONReader reader = new JSONReader(new StringReader(text));


            Exception error = null;
            try {
                reader.readObject(new TypeReference<Map<String, Model>>() {
                });
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        public void test_error_4() throws Exception {
            String text = "{\"model\":{\"values\":[\"aaa]}[";
            JSONReader reader = new JSONReader(new StringReader(text));


            Exception error = null;
            try {
                reader.readObject(new TypeReference<Map<String, Model>>() {
                });
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        public void test_error_n() throws Exception {
            String text = "{\"values\":[n";
            JSONReader reader = new JSONReader(new StringReader(text));

            Exception error = null;
            try {
                reader.readObject(Model.class);
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        public void test_error_nu() throws Exception {
            String text = "{\"values\":[nu";
            JSONReader reader = new JSONReader(new StringReader(text));

            Exception error = null;
            try {
                reader.readObject(Model.class);
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        public void test_error_nul() throws Exception {
            String text = "{\"values\":[nul";
            JSONReader reader = new JSONReader(new StringReader(text));

            Exception error = null;
            try {
                reader.readObject(Model.class);
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        public void test_error_null() throws Exception {
            String text = "{\"values\":[null";
            JSONReader reader = new JSONReader(new StringReader(text));

            Exception error = null;
            try {
                reader.readObject(Model.class);
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        public void test_error_rbacket() throws Exception {
            String text = "{\"values\":[null,]";
            JSONReader reader = new JSONReader(new StringReader(text));

            Exception error = null;
            try {
                reader.readObject(Model.class);
            } catch (JSONException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

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

     */
}
