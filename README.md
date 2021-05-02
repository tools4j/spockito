[![Build Status](https://travis-ci.org/tools4j/spockito.svg?branch=master)](https://travis-ci.org/tools4j/spockito)
[![Coverage Status](https://coveralls.io/repos/github/tools4j/spockito/badge.svg?branch=master)](https://coveralls.io/github/tools4j/spockito?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/org.tools4j/tools4j-spockito.svg)](https://search.maven.org/search?q=spockito)

# spockito
Simple Java library to define data in a table-like manner.  The library also provides a Junit 5 
[@TableSource](https://github.com/tools4j/spockito/blob/master/spockito-junit5/src/main/java/org/tools4j/spockito/jupiter/TableSource.java)
annotation to define arguments for parameterized tests in a simple table structure.  The
[SpockitoExtension](https://github.com/tools4j/spockito/blob/master/spockito-junit5/src/main/java/org/tools4j/spockito/jupiter/SpockitoExtension.java)
can be used to automatically propagate fields in a test class with table data.  

We also support parameterized test data in table format for Junit 4 tests through the classic
[Spockito](https://github.com/tools4j/spockito/blob/master/spockito-junit4/src/main/java/org/tools4j/spockito/Spockito.java)
test runner (see [here](https://github.com/tools4j/spockito/blob/master/README-JUNIT4.md) for more information and 
examples for spockito with Junit 4).
 
### Parameterized tests with @TableSource

Arguments for parameterized tests can be defined via ``@TableSource`` annotation as follows:

```java
public class TableSourceTest {

    @TableSource({
            "| Last Name | First Name |",
            "| Jones     | David      |",
            "| Jensen    | Astrid     |"
    })
    @ParameterizedTest(name = "[{index}] {1} {0}")
    public void testUnrollNames(String lastName, String firstName) {
        assertTrue(lastName.startsWith("J"), "Last Name should start with J");
        assertTrue(firstName.endsWith("id"), "First Name should end with id");
    }

    @TableSource({
            "| Name  | Year | Birthday   |",
            "|-------|------|------------|",
            "| Henry | 1981 | 1981-11-28 |",
            "| Jessy | 1965 | 1965-03-28 |"
    })
    @ParameterizedTest(name = "[{index}] {0}")
    public void testUnrollBirthdays(String name, int year, LocalDate birthday) {
        assertEquals(5, name.length(), "Name should have 5 characters");
        assertTrue(1990 > year, "Year is before 1990");
        assertEquals(28, birthday.getDayOfMonth(), "Day is 28th");
        assertEquals(year, birthday.getYear(), "Year is consistent with birthday");
    }

    @TableSource({
            "| Name  | Year | Birthday   |",
            "|-------|------|------------|",
            "| Henry | 1981 | 1981-11-28 |",
            "| Jessy | 1965 | 1965-03-28 |"
    })
    @ParameterizedTest(name = "[{index}] {0}")
    public void testUnrollNameAndYearOnly(String name, int year) {
        assertEquals(5, name.length(), "Name should have 5 characters");
        assertTrue(1990 > year, "Year is before 1990");
    }

    @TableSource({
            "| Object   | Vertices | Angle sum |",
            "|==========|==========|===========|",
            "| Triangle |     3    |    180    |",
            "| Square   |     4    |    360    |",
            "| Pentagon |     5    |    540    |",
            "|----------|----------|-----------|",
    })
    @ParameterizedTest(name = "{2}: ({1}-2)*180 = {0}")
    public void testUnrollAngularSums(@Column("Vertices") int n,
                                      @Column("Angle sum") int degrees,
                                      @Column("Object") String name) {
        assertTrue(3 <= n, "There should be 3 or more vertices");
        assertEquals(degrees, (n-2)*180, "Angular sum is wrong for: " + name);
    }
}
```
This and other examples can be found [here](https://github.com/tools4j/spockito/blob/master/spockito-junit5/src/test/java/org/tools4j/spockito/jupiter).

#### Run above test in IDE (here: IntelliJ)
![spockito-junit5-idea-testrun.png](https://github.com/tools4j/spockito/blob/master/spockito-junit5-idea-testrun.png)

### Injecting test data into fields of the test class

Sometimes test data is applicable to multiple methods or tests use more than one table source.  Spockito supports this
conveniently through the ``@SpockitoExtension``:

```java
@ExtendWith(SpockitoExtension.class)
public class SpockitoExtensionTest {

    public static class DataRow {
        Operation operation;
        char sign;
        int operand1;
        int operand2;
        int result;
        int neutralOperand;
    }

    @TableSource({
            "| Operation | Sign | Operand1 | Operand2 | Result | NeutralOperand |",
            "|-----------|------|----------|----------|--------|----------------|",
            "| Add       |   +  |        4 |        7 |     11 |              0 |",
            "| Subtract  |   -  |      111 |       12 |     99 |              0 |",
            "| Multiply  |   *  |       24 |        5 |    120 |              1 |",
            "| Divide    |   /  |       24 |        3 |      8 |              1 |"
    })
    private TableRow[] testData;

    @ParameterizedTest(name = "[{index}]: {2} {1} {3} = {4}")
    public void testOperation(Operation operation, char sign, int operand1, int operand2, int result) {
        assertEquals(result, operation.evaluate(operand1, operand2), "Result is wrong!");
    }

    @ParameterizedTest(name = "[{index}]: {2} {1} {3} = {2}")
    public void testNeutralOperand(Operation operation, char sign, int operand1, int neutralOperand) {
        assertEquals(operand1, operation.evaluate(operand1, neutralOperand), "Result with neutral operand is wrong!");
    }
}
```

This and other examples can be found [here](https://github.com/tools4j/spockito/spockito-junit5/blob/master/src/test/java/org/tools4j/spockito/jupiter/).

### Maven
Add the following dependency to your maven pom.xml file:

 ```xml
 <dependency>
    <groupId>org.tools4j</groupId>
    <artifactId>spockito-junit5</artifactId>
    <version>2.0</version>
    <scope>test</scope>
</dependency>
```

### Download
Sources and binaries can be downloaded from maven central:
* [tools4j-spockito](https://search.maven.org/search?q=spockito) in Maven Central

### FAQ
* [Frequently asked Questions](https://github.com/tools4j/spockito/issues?q=label:question)
* [Bugs and Issues](https://github.com/tools4j/spockito/issues?q=label:bug)

### More Information
* [MIT License](https://github.com/tools4j/spockito/blob/master/LICENSE)
