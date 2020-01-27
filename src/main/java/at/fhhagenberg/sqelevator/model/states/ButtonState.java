package at.fhhagenberg.sqelevator.model.states;

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
