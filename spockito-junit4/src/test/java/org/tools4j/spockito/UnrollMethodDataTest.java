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
package org.tools4j.spockito;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

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
        Assert.assertEquals("Year is consistent with birthday", year, birthday.getYear());
    }

    @Test
    @Spockito.Unroll({
            "| Name  | Year | Birthday   |",
            "|-------|------|------------|",
            "| Henry | 1981 | 1981-11-28 |",
            "| Jessy | 1965 | 1965-03-28 |"
    })
    @Spockito.Name("[{row}]: Name={0}")
    public void testUnrollNameAndYearOnly(String name, int year) {
        Assert.assertEquals("Name should have 5 characters", 5, name.length());
        Assert.assertTrue("Year is before 1990", 1990 > year);
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