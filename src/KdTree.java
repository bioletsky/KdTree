import com.sun.org.apache.regexp.internal.RE;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by obil on 22.05.17.
 */
public class KdTree {

    private int size;

    private class Node {
        private Point2D key;
        private RectHV val;
        private Node left;
        private Node rigth;
        private int level;
        Node(Point2D key, Node left, Node rigth, RectHV val, int level) {
            this.key = key;
            this.left = left;
            this.rigth = rigth;
            this.val = val;
            this.level = level;
        }

        private void draw() {
            if (level % 2 == 0) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(key.x(), val.ymin(), key.x(), val.ymax());
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(val.xmin(), key.y(),val.xmax(), key.y());
            }
            StdDraw.setPenColor(StdDraw.BLACK);
            key.draw();
        }
    }

    private Node root;

    public KdTree()                               // construct an empty set of points
    {
        root = null;
        size = 0;
    }

    public boolean isEmpty()                      // is the set empty?
    {
        return (size == 0);
    }

    public int size()                         // number of points in the set
    {
        return size;
    }

    public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
    {
        if (p == null) throw new NullPointerException();

        if (p.x() > 1 || p.x() <0 || p.y() > 1 || p.y() < 0) throw new IllegalArgumentException();

        root = insertNode(root, new Point2D(p.x(), p.y()),0,new RectHV(0.0,0.0,1.0,1.0));

    }

    private Node insertNode(Node parent, Point2D p, int level, RectHV val) {

        if (parent == null) {
            size++;
            return new Node(p, null, null, val, level);
        } else {
            if (p.equals(parent.key)) return parent;

            if (level % 2 == 0) //x-coord
                if (p.x() < parent.key.x())
                    parent.left = insertNode(parent.left, p, ++level, new RectHV(val.xmin(), val.ymin(), parent.key.x(), val.ymax()));  //go left
                else
                    parent.rigth = insertNode(parent.rigth, p, ++level, new RectHV(parent.key.x(), val.ymin(), val.xmax(), val.ymax()));  //go right
            else //y-coord
                if (p.y() < parent.key.y())
                    parent.left = insertNode(parent.left, p, ++level, new RectHV(val.xmin(), val.ymin(), val.xmax(), parent.key.y()));  //go left
                else
                    parent.rigth = insertNode(parent.rigth, p, ++level, new RectHV(val.xmin(), parent.key.y(), val.xmax(), val.ymax()));  //go right
        }
        return parent;

    }

    public boolean contains(Point2D p)            // does the set contain point p?
    {
        if (p == null) throw new NullPointerException();

        return containsRec(root,p);
    }

    private boolean containsRec(Node n, Point2D p)
    {
        if (n == null) return false;

        if (n.key.equals(p)) return true;

        if ((n.level % 2 == 0 && p.x() < n.key.x()) || (n.level % 2 == 1 && p.y() < n.key.y()))
            return containsRec(n.left,p);
        else
            return containsRec(n.rigth,p);
    }

    public  void draw()                         // draw all points to standard draw
    {
        drawRec(root);
    }
    private  void drawRec (Node n) {
        if (n == null) return;
        n.draw();
        drawRec(n.left);
        drawRec(n.rigth);


    }

    private class HvIterator implements Iterator<Point2D> {

        private class PointNode {
            private Point2D p;
            private PointNode next;
            private PointNode(Point2D p, PointNode next) {
                this.p = p;
                this.next = next;
            }
        }
        PointNode curPoint;
        RectHV rect;



        public HvIterator(RectHV rect) {

            this.rect = rect;

            curPoint = null;

            fillIterRec(root);
        }

        private void fillIterRec(Node n) {

            if (n == null) return;

            if (n.val.intersects(rect)) {
                if (rect.contains(n.key)) curPoint = new PointNode(n.key, curPoint);
                fillIterRec(n.left);
                fillIterRec(n.rigth);
            }

        }

        @Override
        public boolean hasNext() {
            return curPoint != null;
        }

        @Override
        public Point2D next() {
            if (!hasNext()) throw new NoSuchElementException();
            Point2D retVal = curPoint.p;
            curPoint = curPoint.next;
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

    private Point2D nearestPoint = null;
    private double minSqDist = Double.MAX_VALUE;

    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        if (p == null) throw new NullPointerException();

        findNearestRec(p, root);

        return nearestPoint;
    }

    private void findNearestRec(Point2D p, Node n)
    {
        if (n == null) return;

        if (n.val.distanceSquaredTo(p) < minSqDist) {
            if (n.key.distanceSquaredTo(p) < minSqDist) {
                minSqDist =  n.key.distanceSquaredTo(p);
                nearestPoint = n.key;
            }
            if ((n.level % 2 == 0 && p.x() < n.key.x()) || (n.level % 2 == 1 && p.y() < n.key.y())) {
                findNearestRec(p, n.left);
                findNearestRec(p, n.rigth);
            } else{
                findNearestRec(p, n.rigth);
                findNearestRec(p, n.left);
            }
        }
    }

    public static void main(String[] args){
        KdTree t = new KdTree();
        t.insert(new Point2D(0.5,0.5));
        t.insert(new Point2D(0.3,0.3));
        t.insert(new Point2D(0.1,0.1));
        /*
        t.insert(new Point2D(0.5,0.3));
        t.insert(new Point2D(0.3,0.5));
        t.insert(new Point2D(0.4,0.5));
        StdOut.println(t.size());
        StdOut.println(t.contains(new Point2D(0.5,0.5)));
        for (Point2D p: t.range(new RectHV(0.4,0.4,0.6,0.6)))
            StdOut.println(p);
*/
        StdOut.println(t.nearest(new Point2D(0.3,0.3)));


        /*
        //StdOut.println(t.root.key);
        //StdOut.println(t.root.val);
        t.root.val.draw();
        //StdOut.println(t.root.left.key);
        //StdOut.println(t.root.left.val);
        t.root.left.val.draw();
        //StdOut.println(t.root.left.rigth.key);
        //StdOut.println(t.root.left.rigth.val);
        t.root.left.rigth.val.draw();
        */

    }
}
