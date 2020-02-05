package at.fhhagenberg.sqelevator.model.states;

/**
 * This enum represents the direction an elevator is committed to
 *
 * @author Martin Schneglberger
 */
public enum CommittedDirection {
    UP("Up", 0),
    DOWN("Down", 1),
    UNCOMMITTED("Uncommitted", 2);
    private String printValue;
    private Integer rawValue;

    /**
     * Constructor
     * @param text
     * @param rawValue
     */
    CommittedDirection(String text, Integer rawValue) {
        printValue = text;
        this.rawValue = rawValue;
    }

    /**
     * Get a direction from an integer value
     * @param x
     * @return
     */
    public static CommittedDirection fromInteger(int x) {
        switch(x) {
            case 0:
                return UP;
            case 1:
                return DOWN;
            default:
                return UNCOMMITTED;
        }
    }

    /**
     * Get the String value
     * @return
     */
    public String getPrintValue() {
        return printValue;
    }

    /**
     * Get the direction as Integer
     * @return
     */
    public Integer getRawValue() {
        return rawValue;
    }
}
