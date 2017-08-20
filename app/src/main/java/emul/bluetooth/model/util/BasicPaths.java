package emul.bluetooth.model.util;

import java.util.ArrayList;

/**
 * Created by moonh on 2017-07-17.
 */
public class BasicPaths {

    private ArrayList<Path> basicPaths;

    public BasicPaths() {
        this.basicPaths = new ArrayList<Path>();
    }

    public void add(Path p) {
        // 1. 추가할지 테스트
        boolean newEdge = false;

        for ( int i=0; (i < p.length()-1); i++ ) {
            Vertex v = p.getVertex(i);
            Vertex w = p.getVertex(i+1);

            if ( includeEdge(v, w) == false ) {
                newEdge = true;
                break;
            }
        }
        // 2. basic path 복제 후 추가
        if ( newEdge )
            basicPaths.add(p.copy());
    }

    public boolean includeEdge( Vertex v, Vertex w ) {
        for (Path bp : basicPaths) {
            if ( bp.includeEdge(v, w) )
                return true;
        }
        return false;
    }

    public Path get(int index) {
        return basicPaths.get(index);
    }

    public void print() {
        System.out.println("Basic Path 수: " + basicPaths.size());
        for ( Path p : basicPaths ) {
            p.print();
        }
    }
}