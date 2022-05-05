/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 tools4j.org (Marco Terzer)
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
package org.tools4j.spockito.jupiter; /**
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

import org.junit.jupiter.params.ParameterizedTest;
import org.tools4j.spockito.table.Column;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @ParameterizedTest(name = "{2}: ({0}-2)*180 = {1}")
    public void testUnrollAngularSums(@Column("Vertices") int n,
                                      @Column("Angle sum") int degrees,
                                      @Column("Object") String name) {
        assertTrue(3 <= n, "There should be 3 or more vertices");
        assertEquals(degrees, (n-2)*180, "Angular sum is wrong for: " + name);
    }
}