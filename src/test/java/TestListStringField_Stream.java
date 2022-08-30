import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.TypeReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.StringReader;
import java.util.*;

@RunWith(Parameterized.class)
public class TestListStringField_Stream {

        private String value;
        private TypeReference typeReference;
        private ClassType type;
        private JSONReader reader;
        private List<Object> expected;
        private boolean isExceptionExpected;

        public TestListStringField_Stream(String value, ClassType type, boolean isExceptionExpected){
            this.configure(value,type,isExceptionExpected);
        }

        private void configure(String value, ClassType type, boolean isExceptionExpected){
            this.value = value;
            this.reader = new JSONReader(new StringReader(this.value));
            this.isExceptionExpected = isExceptionExpected;
            this.type = type;
            if(type == ClassType.Model){
                this.typeReference = new TypeReference<Model>(){};
                if(!isExceptionExpected)
                    this.expected = Oracle.parsingValues(this.value);
            }
            else{
                this.typeReference = new TypeReference<Map<String, Model>>() {};
                if(!isExceptionExpected)
                    this.expected = Oracle.parsingMapValues(this.value);
            }

        }

        @Parameterized.Parameters
        public static Collection<Object> getTestParameters(){
            return Arrays.asList(new Object[][]{
                    //value, typeReference, isExceptionExpected
                    {"{\"values\":[1",                      ClassType.Model,     true},
                    {"{\"values\":[\"b\"[",                 ClassType.Model,     true},
                    {"{\"values\":[n",                      ClassType.Model,     true},
                    {"{\"values\":[nu",                     ClassType.Model,     true},
                    {"{\"values\":[nul",                    ClassType.Model,     true},
                    {"{\"values\":[null",                   ClassType.Model,     true},
                    {"{\"values\":[null,]",                 ClassType.Model,     true},
                    {"{\"model\":{\"values\":[][",          ClassType.Map,       true},
                    {"{\"model\":{\"values\":[]}[",         ClassType.Map,       true},
                    {"{\"model\":{\"values\":[\"aaa]}[",    ClassType.Map,       true},
                    {"{\"model\":{\"values\":[]}}",         ClassType.Map,       false},
                    {"{\"values\":null}",                   ClassType.Model,     false},
                    {"{\"value\":[]}",                      ClassType.Model,     false},
                    {"{\"values\":[]}",                     ClassType.Model,     false},
                    {"{\"values\":[\"a\",null,\"b\",\"ab\\\\c\\\"\"]}", ClassType.Model, false},
            });
        }

        @Test
        public void test(){
            Exception error = null;
            Model model;
            try {
                Object result = reader.readObject(this.typeReference);
                if(type == ClassType.Model) {
                    model = (Model)result;
                }
                else{
                    Map<String, Model> map = (Map<String,Model>)result;
                    model = map.get("model");
                }
                if(expected == null){
                    Assert.assertNull(model.getValues());
                }
                else{
                    Assert.assertEquals(expected.size(),model.getValues().size());
                    for(int i = 0; i < expected.size(); i++){
                        Assert.assertEquals(expected.get(i),model.getValues().get(i));
                    }
                }
            } catch (JSONException ex) {
                error = ex;
            }
            if(isExceptionExpected)
                Assert.assertNotNull(error);
            else
                Assert.assertNull(error);
        }

    public enum ClassType{
        Model,
        Map
    }

    public static class Oracle{

        public Oracle(){}


        public static List<Object> parsingValues(String jsonString){
            return parsing(new JSONObject(jsonString));
        }

        public static List<Object> parsingMapValues(String jsonString){
            JSONObject obj = new JSONObject(jsonString);
            return parsing(obj.getJSONObject("model"));

        }

        private static List<Object> parsing(JSONObject obj){
            try{
                JSONArray array = obj.getJSONArray("values");
                List<Object> values = new ArrayList<>();
                for(Object value: array){
                    values.add(value);
                }
                return values;
            }catch(Exception e){
                return null;
            }
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
