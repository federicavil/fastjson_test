import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

@RunWith(value= Parameterized.class)
public class TestList {

    private String expected;
    private LinkedList value;

    @Parameterized.Parameters
    public static Collection<Object[]> getTestParameters(){
        return Arrays.asList(new Object[][]{
                {"[23L,45L]", new LinkedList<>(Arrays.asList(23L,45L))} //expected, value
        });
    }

    public TestList(String expected,LinkedList value){
        this.expected = expected;
        this.value = value;
    }

    @Test
    public void test_null(){
        Assert.assertEquals(this.expected, JSON.toJSONString(this.value,SerializerFeature.WriteClassName));
    }

}
