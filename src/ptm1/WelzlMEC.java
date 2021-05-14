package ptm1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class WelzlMEC implements MinimumEnclosingCircle {
	
	public Circle welzlHelper(ArrayList<Point> pArr1, ArrayList<Point> pArr2, int n)
	{
		
		if(n==0|| pArr2.size()==3)
			return minCircleTrivial(pArr2);
		
		Random r = new Random();
		int index = r.nextInt()% n;		
		Point p = pArr1.get(index);
		Collections.swap(pArr1, index, n-1);
		Circle d = welzlHelper(pArr1, pArr2, n-1);
		
		if (isInside(d, p))
			return d;
		pArr2.add(p);
		
		return welzlHelper(pArr1, pArr2, n-1);
		
	}
	
	public Circle welzl(ArrayList<Point> pArr)
	{
		ArrayList<Point> pCopy = new ArrayList<>(pArr);
		Collections.shuffle(pCopy);
		
		return welzlHelper(pCopy, new ArrayList<>(), pCopy.size());
		
	}
	
}
