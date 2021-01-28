/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2021 tools4j.org (Marco Terzer)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.tools4j.spockito.table;

/**
 * Class that can be extended and whose {@link Data data} provider elements will be automatically initialised upon
 * construction.
 *
 * <p>For instance a table can be inlined in a (test) method as follows:
 *
 * <pre>
   void printPersons() {
     final class Person {
       String name;
       int age;
     }
     final class MyData extends SpockitoData {
       {@code @TableData}({
         "| Name  | Age |",
         "| Henry |  27 |",
         "| Frank |  29 |"
       })
      {@code List<Person> persons;}
     }

     final MyData data = new MyData();
     data.persons.forEach(person -&gt; System.out.println(person.name + " is " + person.age + " years old"));
   }
 * </pre>
 */
public class SpockitoData {

    /**
     * Default constructor; invokes {@link SpockitoAnnotations#initData(Object)} with itself.
     */
    public SpockitoData() {
        SpockitoAnnotations.initData(this);
    }

}
