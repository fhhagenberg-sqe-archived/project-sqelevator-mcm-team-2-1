package at.fhhagenberg.sqelevator.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
public class Elevator {
    private int id;
    private int floor;
    private int position;
    private int target;
    private int committedDirection;
    private int doorStatus;
    private int speed;
    private int acceleration;
    private int capacity;
    private int weight;
    private HashMap<Integer, Boolean> buttons = new HashMap<>();
}
