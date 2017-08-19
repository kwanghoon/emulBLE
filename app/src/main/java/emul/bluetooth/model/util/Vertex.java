package emul.bluetooth.model.util;

import java.util.ArrayList;

/**
 * Created by moonh on 2017-07-17.
 */
public class Vertex {

    private String stateNumber;
    private ArrayList<Vertex> adjacencyList;

    public Vertex(String stateNumber) {
        this.stateNumber = stateNumber;
        this.adjacencyList = new ArrayList<>();
    }

    public String getStateNumber() {
        return stateNumber;
    }

    public ArrayList<Vertex> getAdjacencyList() {
        return adjacencyList;
    }

    public boolean isFinal() {
        return stateNumber.equals("State100");
    }
}