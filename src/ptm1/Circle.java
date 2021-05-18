package ptm1;

import java.util.Collection;

public class Circle {

	public final float radius; //Circel's radius and center point
	public Point p;
	private static final double MULTIPLICATIVE_EPSILON = 1 + 1e-14;
	public Circle(Point p, float radius) {
		this.p = p;
		this.radius = radius;
	}

	public boolean isCircleInside(Circle second) { //Checks if Circle second is contained in this circle
		float distSq = (float) Math.sqrt(Math.pow(this.getPoint().x - second.getPoint().x, 2)
				* Math.pow(this.getPoint().y - second.getPoint().y, 2));
		if (distSq + second.getRadius() == this.getRadius())
			return true;
		else if (distSq + second.getRadius() < this.getRadius())
			return true;
		else
			return false;
	}

	public Point getPoint() { //Returns Circel's center point
		return p;
	}

	public float getRadius() { //Returns Circel's radius
		return radius;
	}

	@Override
	public String toString() { ////toString function
		return "Circle [radius=" + radius + ", p=" + p + "]";
	}
	
	
    public boolean contains(Point p) { //Checks if point is in the circle
        return p.distance(p) <= radius * MULTIPLICATIVE_EPSILON;
    }


    public boolean contains(Collection<Point> ps) { //Checks if few points is in the circle
        for (Point p : ps) {
            if (!contains(p))
                return false;
        }
        return true;
    }

}
