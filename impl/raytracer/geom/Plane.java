package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
//import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;
//import raytracer.math.Vec4;


class Plane extends BBoxedPrimitive { 
	private final Vec3 n;
	private final Point p;
	private final float d;

	public Plane(final Point a, final Point b, final Point c) {
	
		super(BBox.INF); 

		Vec3 u = a.sub(b).normalized();
		Vec3 v = a.sub(c).normalized();
		this.n = u.cross(v).normalized();
		Point ursprung = new Point(0.0f, 0.0f, 0.0f);
		Vec3 vecA = a.sub(ursprung); // VecA = Ortsvektor von Punkt a
		this.d = vecA.dot(n);
		this.p = a;
	}

	public Plane(final Vec3 n, final Point p) {
		
		super(BBox.INF); // BBox Unendlich?
		
		this.n = n.normalized();
		Point ursprung = new Point(0.0f, 0.0f, 0.0f);
		Vec3 vecP = p.sub(ursprung); // VecP = Ortsvektor von Punkt p
		this.d = vecP.dot(n);
		this.p = p;
	}

	@Override
	public Hit hitTest(final Ray ray, final Obj obj, final float tmin,
			final float tmax) {
		return new LazyHitTest(obj) {			
			private Point point = null;
			
			private float r;

			@Override
			public float getParameter() {
				return r;
			}

			@Override
			public Point getPoint() {
				if (point == null)
					point = ray.eval(r).add(n.scale(0.0001f));
				return point;
			}

			@Override
			protected boolean calculateHit() {
				
				

				float a = d - ray.base().dot(n);
				float b = ray.dir().dot(n);
				if (Constants.isZero(b))
					return false;
				r = a / b;

				if (r < Constants.EPS)
					return false;
				else {
					//r = r;
					return true;
				}				

			}

			@Override
			public Vec3 getNormal() {
				return n;
			}

			@Override
			public Vec2 getUV() {
						return raytracer.geom.Util.computePlaneUV(n, p, this.getPoint());
			}
		};
	}

	@Override
	public int hashCode() {
		return p.hashCode() ^ n.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Plane) {
			if ((this.n.normalized()).equals(((Plane)other).n.normalized())&&(this.p.equals(((Plane)other).p)))
				return true;

		}
		return false;
	}

}
