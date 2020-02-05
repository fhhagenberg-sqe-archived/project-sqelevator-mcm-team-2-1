package at.fhhagenberg.sqelevator.model.states;

/**
 * This enum represents the state in which an elevator's door is
 *
 * @author Martin Schneglberger
 */
public enum DoorStatus {
    OPEN("Open", 1),
    CLOSED("Closed", 2),
    UNDEFINED("Undefined", 0);

    private String printValue;
    private int rawValue;

    /**
     * Constructor
     * @param text
     * @param rawValue
     */
    DoorStatus(String text, int rawValue) {
        printValue = text;
        this.rawValue = rawValue;
    }

    /**
     * Get a door status from an integer value
     * @param x
     * @return
     */
    public static DoorStatus fromInteger(int x) {
        switch(x) {
            case 1:
                return OPEN;
            case 2:
                return CLOSED;
            default:
                return UNDEFINED;
        }
    }

    /**
     * Get the status as string
     * @return
     */
    public String getPrintValue() {
        return printValue;
    }

    /**
     * Get the status as integer
     * @return
     */
    public int getRawValue() {
        return rawValue;
    }
}
