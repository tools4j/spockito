[![Build Status](https://travis-ci.org/tools4j/spockito.svg?branch=master)](https://travis-ci.org/tools4j/spockito)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.tools4j/tools4j-spockito/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.tools4j/tools4j-spockito)

# spockito
Java JUnit runner for parameterized tests where the test cases are defined in a table-like
manner. The @Unroll annotation has been inspired the Groovy framework Spock.
 
### Unroll at method level

Test cases are defined via ``@Spockito.Unroll`` annotation directly on the test method. The best explanation are
probably a few simple examples:

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
        Assert.assertEquals("Angular sum is wrong for: " + name, degrees, (n-2)*180);
    }
}
```
This and other examples can be found [here](https://github.com/tools4j/spockito/blob/master/src/test/java/org/tools4j/spockito/).

#### Run above test in IDE (here: IntelliJ)
![spockito-ide-test-run](https://github.com/tools4j/spockito/blob/master/ide-run-SpockitoTest.png)

### Unroll at class level

Alternatively, the test data can be defined at class level. All methods can then use the same test cases. The values
are either
* directly injected to the method as method parameters
* passed to the single test constructor where they are usually assigned to a member variable
* directly assigned to a field annotated with ``@Spockito.Ref``

An example with field injection is shown next:

```java
@Spockito.Unroll({
        "| Operation | Sign | Operand1 | Operand2 | Result | NeutralOperand2 |",
        "|-----------|------|----------|----------|--------|-----------------|",
        "| Add       |   +  |        4 |        7 |     11 |               0 |",
        "| Subtract  |   -  |      111 |       12 |     99 |               0 |",
        "| Multiply  |   *  |       24 |        5 |    120 |               1 |",
        "| Divide    |   /  |       24 |        3 |      8 |               1 |"
})
@Spockito.Name("[{row}]: {Operation}")
@RunWith(Spockito.class)
public class UnrollClassDataToFieldsTest {

    @Spockito.Ref
    private Operation operation;
    @Spockito.Ref
    private char sign;
    @Spockito.Ref
    private int operand1;
    @Spockito.Ref
    private int operand2;
    @Spockito.Ref
    private int result;
    @Spockito.Ref
    private int neutralOperand2;

    @Test
    @Spockito.Name("[{row}]: {Operand1} {Sign} {Operand2} = {Result}")
    public void testOperation() {
        Assert.assertEquals("Result is wrong!", result, operation.evaluate(operand1, operand2));
    }

    @Test
    @Spockito.Name("[{row}]: {Operand1} {Sign} {NeutralOperand2} = {Operand1}")
    public void testNeutralOperand() {
        Assert.assertEquals("Result with neutral operand is wrong!",
                operand1, operation.evaluate(operand1, neutralOperand2));
    }
}
```
This and other examples can be found [here](https://github.com/tools4j/spockito/blob/master/src/test/java/org/tools4j/spockito/).

### More examples
* [UnrollMethodDataTest.java](https://github.com/tools4j/spockito/blob/master/src/test/java/org/tools4j/spockito/UnrollMethodDataTest.java)
* [UnrollClassDataToFieldsTest.java](https://github.com/tools4j/spockito/blob/master/src/test/java/org/tools4j/spockito/UnrollClassDataToFieldsTest.java)
* [UnrollClassDataToConstructorTest.java](https://github.com/tools4j/spockito/blob/master/src/test/java/org/tools4j/spockito/UnrollClassDataToConstructorTest.java)
* [UnrollClassDataToMethodTest.java](https://github.com/tools4j/spockito/blob/master/src/test/java/org/tools4j/spockito/UnrollClassDataToMethodTest.java)
* [all tests](https://github.com/tools4j/spockito/blob/master/src/test/java/org/tools4j/spockito/)

### Maven
Add the following dependency to your maven pom.xml file:

 ```xml
 <dependency>
     <groupId>org.tools4j</groupId>
     <artifactId>tools4j-spockito</artifactId>
     <version>1.3</version>
     <scope>test</scope>
 </dependency>
 ```

### Download
Sources and binaries can be downloaded from maven central:
* [tools4j-spockito](http://search.maven.org/#search%7Cga%7C1%7Ctools4j-spockito) in Maven Central

### FAQ
* [Frequently asked Questions](https://github.com/tools4j/spockito/issues?q=label:question)

### More Information
* [MIT License](https://github.com/tools4j/spockito/blob/master/LICENSE)
