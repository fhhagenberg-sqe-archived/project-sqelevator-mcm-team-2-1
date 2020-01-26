package at.fhhagenberg.sqelevator.model.states;

public enum DoorStatus {
    OPEN("Open"),
    CLOSED("Closed"),
    UNDEFINED("Undefined");

    private String printValue;

    DoorStatus(String text) {
        printValue = text;
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
}
