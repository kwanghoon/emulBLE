package emul.bluetooth.model.util;

import java.util.ArrayList;

/**
 * Created by moonh on 2017-07-17.
 */
public class Vertex {

    public static int INITIAL_STATE = 1;
    public static int INTERMEDIATE_STATE = 2;
    public static int FINAL_STATE = 3;

    private String stateNumber;
    private ArrayList<Vertex> adjacencyList;
    private int kind;

    public Vertex(String stateNumber) {
        this(stateNumber, INTERMEDIATE_STATE);
    }

    public Vertex(String stateNumber, int kind) {
        this.stateNumber = stateNumber;
        this.adjacencyList = new ArrayList<>();
        this.kind = kind;
    }

    public String getStateNumber() {
        return stateNumber;
    }

    public ArrayList<Vertex> getAdjacencyList() {
        return adjacencyList;
    }

    public boolean isFinal() {
        return kind==FINAL_STATE;
    }

    public boolean isInitial() { return kind==INITIAL_STATE; }
}