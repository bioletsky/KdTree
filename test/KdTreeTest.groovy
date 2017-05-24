import edu.princeton.cs.algs4.Point2D
import edu.princeton.cs.algs4.RectHV

/**
 * Created by 1 on 23.05.2017.
 */
class KdTreeTest extends GroovyTestCase {
    void testIsEmpty() {
        KdTree ps = new KdTree();
        assert ps.isEmpty();
        ps.insert(new Point2D(0,0));
        assert !ps.isEmpty();
    }

    void testSize() {
        KdTree ps = new KdTree();
        ps.insert(new Point2D(0,0));
        assert ps.size() == 1
        ps.insert(new Point2D(0,0));
        assert ps.size() == 1
        ps.insert(new Point2D(0,1));
        assert ps.size() == 2
    }

    void testInsert() {
        KdTree ps = new KdTree();
        shouldFail(NullPointerException) {ps.insert(null);}
    }

    void testContains() {
        KdTree ps = new KdTree();
        ps.insert(new Point2D(0,0));
        assert ps.contains(new Point2D(0,0));
        assert !ps.contains(new Point2D(1,1));
    }

    void testRange() {
        KdTree ps = new KdTree();
        TreeSet ts = new TreeSet();
        TreeSet rTs = new TreeSet();
        ps.insert(new Point2D(0.5, 0.5));
        ps.insert(new Point2D(0.2, 0.2));
        ps.insert(new Point2D(0.5, 0.2));
        ps.insert(new Point2D(0.7, 0.2));
        ps.insert(new Point2D(0.5, 0.9));
        ps.insert(new Point2D(0.1, 0.9));

        RectHV rect = new RectHV(0,0,1,1);
        rTs.add(new Point2D(0.1, 0.9));
        rTs.add(new Point2D(0.5, 0.5));
        rTs.add(new Point2D(0.2, 0.2));
        rTs.add(new Point2D(0.5, 0.2));
        rTs.add(new Point2D(0.7, 0.2));
        rTs.add(new Point2D(0.5, 0.9));

        for (Point2D p : ps.range(rect)) ts.add(p);
        assert ts.equals(rTs);

        rect = new RectHV(0,0.5,1,1);
        rTs.clear();
        rTs.add(new Point2D(0.1, 0.9));
        rTs.add(new Point2D(0.5, 0.5));
        rTs.add(new Point2D(0.5, 0.9));

        ts.clear();
        for (Point2D p : ps.range(rect)) ts.add(p);
        assert ts.equals(rTs);

        rect = new RectHV(0,0.3,0.2,0.4);
        rTs.clear();
        ts.clear();
        for (Point2D p : ps.range(rect)) ts.add(p);
        assert ts.equals(rTs);

        rect = new RectHV(0,0.0,0.0,0.0);
        rTs.clear();
        ts.clear();
        for (Point2D p : ps.range(rect)) ts.add(p);
        assert ts.equals(rTs);

        rect = new RectHV(0.5,0.5,0.5,0.5);
        rTs.clear();
        rTs.add(new Point2D(0.5, 0.5));
        ts.clear();
        for (Point2D p : ps.range(rect)) ts.add(p);
        assert ts.equals(rTs);

        rect = new RectHV(0.3,0.1,0.6,0.3);
        rTs.clear();
        rTs.add(new Point2D(0.5, 0.2));

        ts.clear();
        for (Point2D p : ps.range(rect)) ts.add(p);
        assert ts.equals(rTs);
    }

    void testNearest() {
        KdTree ps = new KdTree();
        ps.insert(new Point2D(0.5, 0.5));
        ps.insert(new Point2D(0.2, 0.2));
        ps.insert(new Point2D(0.5, 0.2));
        ps.insert(new Point2D(0.7, 0.2));
        ps.insert(new Point2D(0.5, 0.9));
        ps.insert(new Point2D(0.1, 0.9));

        assert ps.nearest(new Point2D(0,0)).equals(new Point2D(0.2,0.2));
        assert ps.nearest(new Point2D(0.5,0.5)).equals(new Point2D(0.5,0.5));
        assert ps.nearest(new Point2D(0.5,0.3)).equals(new Point2D(0.5,0.2));
    }

}
