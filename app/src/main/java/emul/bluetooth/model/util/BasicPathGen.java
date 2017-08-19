package emul.bluetooth.model.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by moonh on 2017-07-15.
 */
public class BasicPathGen {

    private static ArrayList<Vertex> vertices;

    private static Vertex findState(String state, ArrayList<Vertex> vertices) {

        for ( Vertex v : vertices ) {
            if ( v.getStateNumber().equals(state) )
                return v;
        }
        Vertex w = new Vertex(state);
        vertices.add(w);
        return w;
    }

    private static void print(ArrayList<Vertex> vertices) {
        for ( Vertex v : vertices ) {
            System.out.print("[ " + v.getStateNumber() + " ] " );
            for ( Vertex w : v.getAdjacencyList() ) {
                System.out.print(w.getStateNumber() + " ");
            }
            System.out.println();
        }
    }

    private static void DFS(Vertex v, Path p, int bound, BasicPaths basicPaths) {

        if ( p.length() <= bound  && p.isBasicPath(v) ) {
            p.add(v);

            if ( v.isFinal() ) {
                basicPaths.add(p);

            } else {
                for (Vertex n : v.getAdjacencyList()) {
                    DFS(n, p, bound, basicPaths);
                }
            }

            p.remove();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);

        vertices = new ArrayList<Vertex>();
        int bound = 0;

        System.out.println("Input Edge: sourceState, destState");

        while ( scan.hasNext() ) {

            int source = scan.nextInt();
            int dest = scan.nextInt();

            if ( source == -1 && dest == -1 )
                break;

            bound++;

            Vertex srcVertex = findState(BasicPathGen.toString(source), vertices);
            Vertex destVertex = findState(BasicPathGen.toString(dest), vertices);

            srcVertex.getAdjacencyList().add(destVertex);

        }

        print(vertices);
        Vertex start = findState(BasicPathGen.toString(0), vertices);
        Path path = new Path();
        BasicPaths basicPaths = new BasicPaths();

        bound = bound * 2;
        DFS(start, path, bound, basicPaths );

        basicPaths.print();
    }

    private static String toString(int stateNumber) {
        return "State" + stateNumber;
    }
}