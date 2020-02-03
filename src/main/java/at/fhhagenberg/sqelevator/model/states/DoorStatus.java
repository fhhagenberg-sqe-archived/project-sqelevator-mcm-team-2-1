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

    DoorStatus(String text, int rawValue) {
        printValue = text;
        this.rawValue = rawValue;
    }

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

    public String getPrintValue() {
        return printValue;
    }

    public int getRawValue() {
        return rawValue;
    }
}
