package com.mathandoro.coachplus;

import android.widget.SectionIndexer;

import com.mathandoro.coachplus.helpers.RecycleViewStack;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class RecycleViewStackTest {

    final int SECTION_1 = 1;
    final int SECTION_2 = 2;
    final int SECTION_3 = 3;
    final int SECTION_4 = 4;

    @Test()
    public void testGetPosition(){
        RecycleViewStack stack = new RecycleViewStack();
        stack.addSection(SECTION_1, 1);
        stack.addSection(SECTION_2, 3);
        stack.addSection(SECTION_3, 1);

        int position = 10;
        assertEquals(10, stack.positionInSection(SECTION_1, position));
        assertEquals(9, stack.positionInSection(SECTION_2, position));
        assertEquals(6, stack.positionInSection(SECTION_3, position));

        stack.updateSection(SECTION_1, 3);

        assertEquals(10, stack.positionInSection(SECTION_1, position));
        assertEquals(7, stack.positionInSection(SECTION_2, position));
        assertEquals(4, stack.positionInSection(SECTION_3, position));
    }

    @Test()
    public void testGetSection(){
        RecycleViewStack stack = new RecycleViewStack();
        stack.addSection(SECTION_1, 1);
        stack.addSection(SECTION_2, 3);
        stack.addSection(SECTION_3, 1);

        assertEquals(SECTION_1, stack.getSectionIdAt(0));
        assertEquals(SECTION_2,stack.getSectionIdAt(1));
        assertEquals(SECTION_2,stack.getSectionIdAt(2));
        assertEquals(SECTION_2,stack.getSectionIdAt(3));
        assertEquals(SECTION_3,stack.getSectionIdAt(4));

        stack.updateSection(SECTION_2, 1);

        assertEquals(SECTION_1, stack.getSectionIdAt(0));
        assertEquals(SECTION_2,stack.getSectionIdAt(1));
        assertEquals(SECTION_3,stack.getSectionIdAt(2));
    }

    @Test()
    public void testSize(){
        RecycleViewStack stack = new RecycleViewStack();
        stack.addSection(SECTION_1, 0);
        stack.addSection(SECTION_2, 6);
        stack.addSection(SECTION_3, 0);
        stack.addSection(SECTION_4, 4);

        assertEquals(10, stack.size());
        stack.updateSection(SECTION_1, 2);
        assertEquals(12, stack.size());
    }
}
