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

    ButtonState(String stringValue) {
        this.printValue = stringValue;
    }

    public String getPrintValue() {
        return printValue;
    }
}
