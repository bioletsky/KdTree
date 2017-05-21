import com.thoughtworks.xstream.mapper.Mapper;
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
    TreeSet<Point2D> tS;

    public         PointSET()                               // construct an empty set of points
    {
        tS = new TreeSet<Point2D>();
    }

    public           boolean isEmpty()                      // is the set empty?
    {
        return tS.isEmpty();
    }

    public               int size()                         // number of points in the set
    {
        return tS.size();
    }

    public              void insert(Point2D p)              // add the point to the set (if it is not already in the set)
    {
        if (p == null) throw new NullPointerException();
        tS.add(p);
    }

    public           boolean contains(Point2D p)            // does the set contain point p?
    {
        if (p == null) throw new NullPointerException();
        return tS.contains(p);
    }

    public              void draw()                         // draw all points to standard draw
    {
        for (Point2D t: tS) t.draw();


    }

    private  class HvIterator implements Iterator<Point2D>{

        Iterator<Point2D> tsI = null;
        Point2D currentPoint = null;
        RectHV rect;

        public HvIterator(RectHV rect){

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
            if (!tsI.hasNext())
                currentPoint=null;
            else{
                currentPoint = tsI.next();
                if (!rect.contains(currentPoint)) currentPoint=null;
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
                return new HvIterator(rect);
            }
        };

    }

    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        if (p == null) throw new NullPointerException();

        Point2D retVal = null;
        double retDist = Double.MAX_VALUE;
        for (Point2D p1 : tS )
            if (p.distanceTo(p1) < retDist) {
                retVal = p1;
                retDist = p.distanceTo(p1);
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
