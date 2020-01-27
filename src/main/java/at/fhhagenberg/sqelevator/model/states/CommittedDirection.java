package at.fhhagenberg.sqelevator.model.states;

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
            case 2:
                return UNCOMMITTED;
        }
        return UNCOMMITTED;
    }

    public String getPrintValue() {
        return printValue;
    }

    public Integer getRawValue() {
        return rawValue;
    }
}
