package emul.bluetooth.model.util;

import java.util.ArrayList;

/**
 * Created by moonh on 2017-07-17.
 */
public class Path {

    private ArrayList<Vertex> path;

    public Path() {
        this.path = new ArrayList<>();
    }

    public int length() {
        return path.size();
    }

    public void add(Vertex v) {
        this.path.add(v);
    }

    public void remove() {
        this.path.remove(path.size()-1);
    }

    public Path copy() {
        Path p = new Path();
        for ( Vertex v : this.path ) {
            p.add(v);
        }
        return p;
    }

    public void print() {
        for ( Vertex v : path ) {
            System.out.print(v.getStateNumber() + "; ");
        }
        System.out.println();
    }

    public boolean isBasicPath( Vertex v ) {
        if ( path.size() == 0 )
            return true;
        else {
            Vertex w = path.get(path.size()-1);
            int flag = 0;
            for ( Vertex z : path ) {
                if ( flag == 0 && z == w )
                    flag = 1;
                else if ( flag == 1 && z == v )
                    flag = 2;
                else if ( flag == 1 && z != v )
                    flag = 0;
                else if ( flag == 2 && z == w )
                    flag = 3;
                else if ( flag == 3 && z == v )
                    flag = 4;
                else if ( flag == 3 && z != v )
                    flag = 2;
            }

            if ( w == v )
                return  flag < 2;
            else
                return  flag != 4;
        }
    }

    public Vertex getVertex( int i ) {
        return path.get(i);
    }

    public boolean includeEdge( Vertex v, Vertex w ) {

        for ( int i=0; (i < path.size()-1); i++ ) {
            Vertex x = path.get(i);
            if ( x.getStateNumber().equals(v.getStateNumber()) ) {
                Vertex y = path.get(i+1);

                if ( y.getStateNumber().equals(w.getStateNumber()) )
                    return true;
            }
        }
        return false;
    }

    public ArrayList<Vertex> get() {
        return path;
    }
}