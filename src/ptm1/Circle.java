package ptm1;

import java.util.Collection;

public class Circle {

	public final float radius;
	public Point p;
	private static final double MULTIPLICATIVE_EPSILON = 1 + 1e-14;
	public Circle(Point p, float radius) {
		this.p = p;
		this.radius = radius;
	}

	public boolean isCircleInside(Circle second) {
		double distSq = Math.sqrt(Math.pow(this.getPoint().x - second.getPoint().x, 2)
				* Math.pow(this.getPoint().y - second.getPoint().y, 2));
		if (distSq + second.getRadius() == this.getRadius())
			return true;
		else if (distSq + second.getRadius() < this.getRadius())
			return true;
		else
			return false;
	}

	public Point getPoint() {
		return p;
	}

	public float getRadius() {
		return radius;
	}

	@Override
	public String toString() {
		return "Circle [radius=" + radius + ", p=" + p + "]";
	}
	
	
    public boolean contains(Point p) {
        return p.distance(p) <= radius * MULTIPLICATIVE_EPSILON;
    }


    public boolean contains(Collection<Point> ps) {
        for (Point p : ps) {
            if (!contains(p))
                return false;
        }
        return true;
    }

}
