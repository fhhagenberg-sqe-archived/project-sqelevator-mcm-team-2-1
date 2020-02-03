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

    CommittedDirection(String text, Integer rawValue) {
        printValue = text;
        this.rawValue = rawValue;
    }

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

    public String getPrintValue() {
        return printValue;
    }

    public Integer getRawValue() {
        return rawValue;
    }
}
