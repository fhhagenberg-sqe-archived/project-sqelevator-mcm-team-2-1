package at.fhhagenberg.sqelevator.model.states;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoorStatusTest {
    @Test
    public void testTextMapping() {
        assertEquals("Closed", DoorStatus.CLOSED.getPrintValue());
        assertEquals(2, DoorStatus.CLOSED.getRawValue());
        assertEquals("Undefined", DoorStatus.UNDEFINED.getPrintValue());
        assertEquals(0, DoorStatus.UNDEFINED.getRawValue());
        assertEquals("Open", DoorStatus.OPEN.getPrintValue());
        assertEquals(1, DoorStatus.OPEN.getRawValue());
    }

    @Test
    public void testRawValueMapping() {
        assertEquals(DoorStatus.UNDEFINED, DoorStatus.fromInteger(0));
        assertEquals(DoorStatus.OPEN, DoorStatus.fromInteger(1));
        assertEquals(DoorStatus.CLOSED, DoorStatus.fromInteger(2));
    }
}
