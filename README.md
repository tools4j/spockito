[![Build Status](https://travis-ci.org/tools4j/spockito.svg?branch=master)](https://travis-ci.org/tools4j/spockito)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.tools4j/tools4j-spockito/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.tools4j/tools4j-spockito)

# tools4j-spockito
Java JUnit runner for parameterized tests where the test cases are defined in a table-like
manner. The @Unroll annotation has been inspired the groovy framework Spock.
 
#### Examples
###### Unroll at method level
```java
@RunWith(Spockito.class)
public class UnrollMethodDataTest {

    @Test
    @Spockito.Unroll({
            "| Name     |",
            "| Test 1   |",
            "| Test 2   |"
    })
    public void testUnrollSimple(final String name) {
        Assert.assertNotNull("name should not be null", name);
        Assert.assertTrue("name should start with Test", name.startsWith("Test "));
    }

    @Test
    @Spockito.Unroll({
            "| Index | Name     |",
            "|-------|----------|",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    @Spockito.Name("[{row}]: index={0}, name={1}")
    public void testUnrollTwoColumns(int index, String name) {
        Assert.assertTrue("index should be greater than 0", index > 0);
        Assert.assertNotNull("name should be TestName", name);
        Assert.assertTrue("name should start with Test", name.startsWith("Test "));
    }

    @Test
    @Spockito.Unroll({
            "| Index | TestName |",
            "|=======|==========|",
            "| 1     | Test 1   |",
            "|------ |----------|",
            "| 2     | Test 2   |",
            "|------ |----------|",
            "| 3     | Test 3   |",
            "|------ |----------|",
            "| 4     | Test 4   |",
            "|------ |----------|",
    })
    @Spockito.Name("[{row}]: index={Index}, name={TestName}")
    public void testUnrollWithColumnRefs(@Spockito.Ref("TestName") String name,
                                         @Spockito.Ref("Index") int index,
                                         @Spockito.Ref("row") int row) {
        Assert.assertTrue("index should be greater than 0", index > 0);
        Assert.assertTrue("row should be greated or equal to 0", row >= 0);
        Assert.assertTrue("index should be row + 1", index == row + 1);
        Assert.assertNotNull("name should be TestName", name);
        Assert.assertTrue("name should start with Test", name.startsWith("Test "));
    }
}
```

#### Run test in IDE (here: IntelliJ)
![spockito-ide-test-run](https://github.com/tools4j/spockito/blob/master/ide-run-SpockitoTest.png)

#### More Information
* [MIT License](https://github.com/tools4j/spockito/blob/master/LICENSE)
