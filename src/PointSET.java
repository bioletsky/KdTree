import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

/**
 * Created by obil on 21.05.17.
 */

public class PointSET {

    private TreeSet<Point2D> tS;

    public PointSET()                               // construct an empty set of points
    {
        tS = new TreeSet<Point2D>();
    }

    public boolean isEmpty()                      // is the set empty?
    {
        return tS.isEmpty();
    }

    public int size()                         // number of points in the set
    {
        return tS.size();
    }

    public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
    {
        if (p == null) throw new NullPointerException();

        if (p.x() > 1 || p.x() <0 || p.y() > 1 || p.y() < 0) throw new IllegalArgumentException();

        tS.add(new Point2D(p.x(), p.y()));
    }

    public boolean contains(Point2D p)            // does the set contain point p?
    {
        if (p == null) throw new NullPointerException();

        return tS.contains(p);
    }

    public void draw()                         // draw all points to standard draw
    {
        for (Point2D p: tS) p.draw();
    }

    private  static class HvIterator implements Iterator<Point2D>{

        Iterator<Point2D> tsI = null;
        Point2D currentPoint = null;
        RectHV rect;

        public HvIterator(RectHV rect, TreeSet<Point2D> tS){

            if (rect == null) throw new NullPointerException();

            this.rect = rect;

            tsI = tS.iterator();

            while (tsI.hasNext()) {
                currentPoint = tsI.next();
                if (rect.contains(currentPoint))
                    return;
            }

            currentPoint=null;

        }

        @Override
        public boolean hasNext() {
            return (currentPoint != null);
        }

        @Override
        public Point2D next() {
            if (!hasNext()) throw  new NoSuchElementException();
            Point2D retVal = currentPoint;
            Point2D iterVal = null;
            currentPoint = null;
            while (tsI.hasNext()){
                iterVal = tsI.next();
                if (rect.contains(iterVal)) {currentPoint = iterVal; break;}
                if  (iterVal.y()>rect.ymax()) break;
            }
            return retVal;
        }
    }

    public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle
    {
        if (rect == null) throw new NullPointerException();

        return new Iterable<Point2D>() {
            @Override
            public Iterator<Point2D> iterator() {
                return new HvIterator(rect, tS);
            }
        };

    }

    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        if (p == null) throw new NullPointerException();

        Point2D retVal = null;

        double minSqDist = Double.MAX_VALUE;

        for (Point2D p1 : tS )
            if (p.distanceSquaredTo(p1) < minSqDist) {
                retVal = p1;
                minSqDist = p.distanceSquaredTo(p1);
            }

        return retVal;
    }


    public static void main(String[] args)                  // unit testing of the methods (optional)
    {
        PointSET ps = new PointSET();
        ps.insert(new Point2D(0.5,0.5));
        ps.insert(new Point2D(0.6,0.6));
        ps.draw();
        //StdOut.println(ps.nearest(new Point2D(0.6,0.6)));
        for (Point2D p : ps.range(new RectHV(0.5,0.5,0.7,0.7)))
            StdOut.println(p);

    }
}
