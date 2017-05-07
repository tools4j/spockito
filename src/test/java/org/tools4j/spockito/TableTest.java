package org.tools4j.spockito;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit test for {@link Table}
 */
public class TableTest {

    static final class Row {
        int index;
        List<Integer> listOfInteger;
    }



    @Test
    public void parse() {
        //when
        final Row[] rows = Table.parse(Row.class, new String[] {
                "| index | listOfInteger |",
                "|   0   | [1;2;3;4;5;6] |",
                "|   1   | [9;8;7;6;5;4] |"
        });

        //then
        Assert.assertEquals("unexpected row count", 2, rows.length);
        for (int row = 0; row < rows.length; row++) {
            Assert.assertEquals("unexpected index", row, rows[row].index);
            Assert.assertEquals("unexpected list length", 6, rows[row].listOfInteger.size());
        }
    }
}
