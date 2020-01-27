package at.fhhagenberg.sqelevator.model.states;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ButtonStateTest {
    @Test
    public void testTextMapping() {
        assertEquals("-", ButtonState.UNSET.getPrintValue());
        assertEquals("▲ ▼", ButtonState.BOTH.getPrintValue());
        assertEquals("▼", ButtonState.DOWN.getPrintValue());
        assertEquals("▲", ButtonState.UP.getPrintValue());
    }
}
