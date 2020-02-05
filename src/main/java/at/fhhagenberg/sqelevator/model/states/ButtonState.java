package at.fhhagenberg.sqelevator.model.states;

/**
 * This enum is used to represent the buttons pressed at a floor (up, down, both, unset)
 *
 * @author Martin Schneglberger
 */
public enum ButtonState {
    UNSET("-"),
    UP("▲"),
    DOWN("▼"),
    BOTH("▲ ▼");

    private String printValue;

    /**
     * Constructor
     * @param stringValue
     */
    ButtonState(String stringValue) {
        this.printValue = stringValue;
    }

    /**
     * get the Text value
     * @return
     */
    public String getPrintValue() {
        return printValue;
    }
}
