import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@RunWith(value= Parameterized.class)
public class TestList {

    private List values;
    private String expected;
    private SerializerFeature feature;

    @Parameterized.Parameters
    public static Collection<Object[]> getTestParameters(){
        return Arrays.asList(new Object[][]{
                {new LinkedList<>(Arrays.asList(23L,45L)), SerializerFeature.WriteClassName, ElementType.LONG} //values, feature, element list type
        });
    }

    private void configure(List values, SerializerFeature feature, ElementType type){
        this.values = values;
        this.feature = feature;
        switch(type){
            case LONG:
                this.expected = "[";
                for(int i = 0; i < this.values.size(); i++){
                    String value = this.values.get(i).toString();
                    if(i < this.values.size() -1)
                        this.expected = this.expected + value + "L,";
                    else
                        this.expected = this.expected + value +"L]";
                }
                break;
        }

    }

    public TestList(List values, SerializerFeature feature, ElementType type){
        this.configure(values,feature, type);
    }

    @Test
    public void test_null(){
        String json = JSON.toJSONString(this.values,this.feature);

        Assert.assertEquals(expected, json);
    }

    public enum ElementType{
        LONG
    }

    /*
    package com.alibaba.json.bvt.serializer;

    import java.util.LinkedList;
    import java.util.List;

    import org.junit.Assert;
    import junit.framework.TestCase;

    import com.alibaba.fastjson.JSON;
    import com.alibaba.fastjson.serializer.SerializerFeature;

    public class ListTest extends TestCase {

        public void test_null() throws Exception {
            List list = new LinkedList();
            list.add(23L);
            list.add(45L);

            Assert.assertEquals("[23L,45L]", JSON.toJSONString(list, SerializerFeature.WriteClassName));
        }

        public static class VO {

            private Object value;

            public Object getValue() {
                return value;
            }

            public void setValue(Object value) {
                this.value = value;
            }

        }
    }
     */
}
