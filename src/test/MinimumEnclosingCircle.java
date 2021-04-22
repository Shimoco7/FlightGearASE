package test;

import java.util.ArrayList;

public interface MinimumEnclosingCircle {

    public default float dist(Point a, Point b){
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2)
                + Math.pow(a.y - b.y, 2));
    }

    public default boolean isInside(Circle c, Point p) {
        return dist(c.p,p)<= c.radius;
    }

    public default Point getCircleCenter(float bx,float by, float cx, float cy){
        float b = (float) (Math.pow(bx,2)+ Math.pow(by,2));
        float c = (float) (Math.pow(cx,2)+ Math.pow(cy,2));
        float d = bx*cy - by*cx;

        return new Point((cy*b-by*c)/2*d,(bx*c-cx*b)/2*d);
    }

    public default Circle getCircleFrom(Point a, Point b, Point c){
        Point I = getCircleCenter(b.x-a.x,b.y-a.y,c.x-a.x,c.y-a.y);

        Point p = new Point(I.x+a.x,I.y+a.y);
        return new Circle(p,dist(p,a));
    }

    public default Circle getCircleFrom(Point a, Point b){
        Point c = new Point((a.x+b.x)/2,(a.y+b.y)/2);

        return new Circle(c, dist(a,b)/2);
    }

    public default boolean isValidCircle(Circle c, ArrayList<Point> pArr){
        for(Point p : pArr){
            if(!isInside(c,p))
                return false;
        }
        return true;
    }

    public default Circle minCircleTrivial(ArrayList<Point> pArr){
        assert pArr.size()<=3 : "Number of Points out of range";

        if(pArr.size()==0)
            return new Circle(new Point(0,0),0);

        else if(pArr.size()==1)
            return new Circle(pArr.get(0),0);

        else if(pArr.size()==2)
            return getCircleFrom(pArr.get(0),pArr.get(1));

        else{
            for(int i=0;i<3;i++){
                for(int j=i+1;j<3;j++){
                    Circle c = getCircleFrom(pArr.get(i), pArr.get(j));
                    if(isValidCircle(c,pArr))
                        return c;
                }
            }

            return getCircleFrom(pArr.get(0), pArr.get(1), pArr.get(2));
        }
    }




}
