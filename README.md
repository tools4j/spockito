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
            "| Last Name | First Name |",
            "| Jones     | David      |",
            "| Jensen    | Astrid     |"
    })
    public void testUnrollNames(String lastName, String firstName) {
        Assert.assertTrue("Last Name should start with J", lastName.startsWith("J"));
        Assert.assertTrue("First Name should end with id", firstName.endsWith("id"));
    }

    @Test
    @Spockito.Unroll({
            "| Name  | Year | Birthday   |",
            "|-------|------|------------|",
            "| Henry | 1981 | 1981-11-28 |",
            "| Jessy | 1965 | 1965-03-28 |"
    })
    @Spockito.Name("[{row}]: Name={0}")
    public void testUnrollBirthdays(String name, int year, LocalDate birthday) {
        Assert.assertEquals("Name should have 5 characters", 5, name.length());
        Assert.assertTrue("Year is before 1990", 1990 > year);
        Assert.assertEquals("Day is 28th", 28, birthday.getDayOfMonth());
    }

    @Test
    @Spockito.Unroll({
            "| Object   | Vertices | Angle sum |",
            "|==========|==========|===========|",
            "| Triangle |     3    |    180    |",
            "| Square   |     4    |    360    |",
            "| Pentagon |     5    |    540    |",
            "|----------|----------|-----------|",
    })
    @Spockito.Name("[{Object}]: ({Vertices}-2)*180 = {Angle sum}")
    public void testUnrollAngularSums(@Spockito.Ref("Vertices") int n,
                                      @Spockito.Ref("Angle sum") int degrees,
                                      @Spockito.Ref("Object") String name) {
        Assert.assertTrue("There should be 3 or more vertices", 3 <= n);
        Assert.assertEquals("Angular sum of is wrong for: " + name, degrees, (n-2)*180);
    }
}
```

#### Run test in IDE (here: IntelliJ)
![spockito-ide-test-run](https://github.com/tools4j/spockito/blob/master/ide-run-SpockitoTest.png)

#### More Information
* [MIT License](https://github.com/tools4j/spockito/blob/master/LICENSE)
