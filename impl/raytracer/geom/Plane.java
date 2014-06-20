package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;
import raytracer.math.Vec4;

class Plane extends BBoxedPrimitive {
	private final Vec3 n;
	private final float d;

	public Plane(final Point a, final Point b, final Point c) {
		//super(BBox.create(a, b));
		/* TODO BBox super ?? */
		
		Vec3 u = a.sub(b).normalized();
		Vec3 v = a.sub(c).normalized();
		this.n = u.cross(v).normalized();
		Point ursprung = new Point(0.0f, 0.0f, 0.0f);
		Vec3 vecA = a.sub(ursprung);	// VecA = Ortsvektor von Punkt a
		this.d = vecA.dot(n);
	}
	
	public Plane(final Vec3 n, final Point p){
		/* TODO BBox?? */
		
		this.n = n;
		Point ursprung = new Point(0.0f, 0.0f, 0.0f);
		Vec3 vecP = p.sub(ursprung);	// VecP = Ortsvektor von Punkt p
		this.d = vecP.dot(n);
	}
	

	@Override
	public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// TODO Rest Implementierung?

}