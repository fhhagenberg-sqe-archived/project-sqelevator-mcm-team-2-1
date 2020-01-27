package at.fhhagenberg.sqelevator.model.states;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommittedDirectionTest {
    @Test
    public void testTextMapping() {
        assertEquals("Up", CommittedDirection.UP.getPrintValue());
        assertEquals(0, CommittedDirection.UP.getRawValue());
        assertEquals("Down", CommittedDirection.DOWN.getPrintValue());
        assertEquals(1, CommittedDirection.DOWN.getRawValue());
        assertEquals("Uncommitted", CommittedDirection.UNCOMMITTED.getPrintValue());
        assertEquals(2, CommittedDirection.UNCOMMITTED.getRawValue());
    }

    @Test
    public void testRawValueMapping() {
        assertEquals(CommittedDirection.UP, CommittedDirection.fromInteger(0));
        assertEquals(CommittedDirection.DOWN, CommittedDirection.fromInteger(1));
        assertEquals(CommittedDirection.UNCOMMITTED, CommittedDirection.fromInteger(2));
        assertEquals(CommittedDirection.UNCOMMITTED, CommittedDirection.fromInteger(-1));
    }
}
