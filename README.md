# tools4j-spockito
Java JUnit runner for parameterized tests where the test cases are defined in a table-like
manner. The @Unroll annotation has been inspired the groovy framework Spock.

 
#### Examples
###### Unroll at method level
```java
@RunWith(Spockito.class)
public class SpockitoTest {
    @Test
    @Spockito.Unroll({
            "| TestName |",
            "| Test 1   |",
            "| Test 2   |"
    })
    public void testUnrollWithString(final String name) {
        Assert.assertNotNull("should be the colum 1 value", name);
        Assert.assertTrue("should start with Test", name.startsWith("Test "));
    }

    @Test
    @Spockito.Unroll({
            "| Index | TestName |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    @Spockito.Name("[row]: index={0}, name={1}")
    public void testUnrollWithIndex(int index, String name) {
        Assert.assertTrue("should be greated than 0", index > 0);
        Assert.assertNotNull("should be the column 1 value", value);
        Assert.assertTrue("should start with Test", name.startsWith("Test "));
    }
}
```

#### More Information
* [MIT License](https://github.com/tools4j/spockito/blob/master/LICENSE)
