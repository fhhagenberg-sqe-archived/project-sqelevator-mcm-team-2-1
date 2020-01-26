package at.fhhagenberg.sqelevator.model.states;

public enum CommittedDirection {
    UP("Up"),
    DOWN("Down"),
    UNCOMMITTED("Uncommitted");
    private String printValue;

    CommittedDirection(String text) {
        printValue = text;
    }

    public static CommittedDirection fromInteger(int x) {
        switch(x) {
            case 0:
                return UP;
            case 1:
                return DOWN;
            case 2:
                return UNCOMMITTED;
        }
        return UNCOMMITTED;
    }

    public String getPrintValue() {
        return printValue;
    }
}
