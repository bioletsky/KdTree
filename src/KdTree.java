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


        Node curNode = root;
        Node parNode = null;
        int level = 0;

        while (curNode != null) {

            if (curNode.key.equals(p)) return;

            parNode = curNode;
            if ((level % 2 == 0 && p.x() < curNode.key.x()) || (level % 2 != 0 && p.y() < curNode.key.y()))
                curNode = curNode.left;
            else
                curNode = curNode.rigth;

            level++;
        }

        if (parNode == null)
            root = new Node(new Point2D(p.x(), p.y()),null,null, new RectHV(0.0,0.0,1.0,1.0),0);
        else
            if (level % 2 != 0)
                if (p.x() < parNode.key.x())
                    parNode.left = new Node(p,null,null,new RectHV(parNode.val.xmin(), parNode.val.ymin(),parNode.key.x(),parNode.val.ymax()),level);
                else
                    parNode.rigth = new Node(p,null,null,new RectHV(parNode.key.x(), parNode.val.ymin(),parNode.val.xmax(),parNode.val.ymax()),level);
            else
                if (p.y() < parNode.key.y())
                    parNode.left = new Node(p,null,null,new RectHV(parNode.val.xmin(), parNode.val.ymin(),parNode.val.xmax(),parNode.key.y()),level);
                else
                    parNode.rigth = new Node(p,null,null,new RectHV(parNode.val.xmin(), parNode.key.y(),parNode.val.xmax(),parNode.val.ymax()),level);

        size++;
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

        if ((n.level % 2 == 0 && p.x() < n.key.x()) || (n.level % 2 != 0 && p.y() < n.key.y()))
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

    private static class HvIterator implements Iterator<Point2D> {

        private class NodeNode {
            private Node n;
            private NodeNode next;
            private NodeNode(Node n, NodeNode next) {
                this.n = n;
                this.next = next;
            }
        }

        NodeNode top;
        RectHV rect;

        private void pop() {
            if (top == null) throw new NoSuchElementException();
            top = top.next;
        }
        private void push(Node n){
            if (top == null)
                top = new NodeNode(n,null);
            else
                top = new NodeNode(n,top);
        }




        public HvIterator(RectHV rect, Node root) {

            this.rect = rect;

            top = null;

            if (root == null) return;

            if (!rect.intersects(root.val)) return;

            push(root);

            while (true) {
                if (rect.contains(top.n.key)) return;
                moveNextNode();
                if (top == null) return;
            }

        }

        private void moveNextNode() {

            NodeNode oldTop = null;

            if (top == null) return;
            if (top.n.left != null && top.n.left.val.intersects(rect))
                push(top.n.left);
            else if (top.n.rigth != null && top.n.rigth.val.intersects(rect))
                push(top.n.rigth);
            else {
                while (true) {
                    oldTop = top;
                    pop();
                    if (top == null) return;
                    if (top.n.rigth == null) continue;
                    if (top.n.rigth == oldTop.n) continue;
                    if (!top.n.rigth.val.intersects(rect)) continue;
                    push(top.n.rigth);
                }

            }
        }

        @Override
        public boolean hasNext() {
            return top != null;
        }

        @Override
        public Point2D next() {
            if (!hasNext()) throw new NoSuchElementException();
            Point2D retVal = top.n.key;
            moveNextNode();
            return retVal;
        }
    }

    public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle
    {
        if (rect == null) throw new NullPointerException();

        return new Iterable<Point2D>() {
            @Override
            public Iterator<Point2D> iterator() {
                return new HvIterator(rect, root);
            }
        };
    }

    private Point2D nearestPoint = null;
    private double minSqDist = Double.MAX_VALUE;

    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        if (p == null) throw new NullPointerException();

        nearestPoint = null;
        minSqDist = Double.MAX_VALUE;

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
            if ((n.level % 2 == 0 && p.x() < n.key.x()) || (n.level % 2 != 0 && p.y() < n.key.y())) {
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
