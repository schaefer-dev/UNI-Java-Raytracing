package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
//import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;
//import raytracer.math.Vec4;

// evt nur als Primitive anlegen?
class Plane extends BBoxedPrimitive {
	private final Vec3 n;
	private final Point p;
	private final float d;

	public Plane(final Point a, final Point b, final Point c) {
		/* TODO BBox super ?? */
		super(BBox.INF); // BBox Unendlich?

		Vec3 u = a.sub(b).normalized();
		Vec3 v = a.sub(c).normalized();
		this.n = u.cross(v).normalized();
		Point ursprung = new Point(0.0f, 0.0f, 0.0f);
		Vec3 vecA = a.sub(ursprung); // VecA = Ortsvektor von Punkt a
		this.d = vecA.dot(n);
		this.p = a;
	}

	public Plane(final Vec3 n, final Point p) {
		/* TODO BBox?? */
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
			// tmin und tmax noch einbauen??
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

						// tmin und tmax noch einbauen??

				float a = d - ray.base().dot(n);
				float b = ray.dir().dot(n);
				if (b == 0)
					return false;
				r = a / b;

				if (r < 0)
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
				return raytracer.geom.Util.computePlaneUV(n, p, point);
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
			final Plane cobj = (Plane) other;
			return (cobj.n.equals(n) && (cobj.p.equals(p))); // Muesste richtig
																// sein? (this
																// in Klammern
																// ergänzen?)
		}
		return false;
	}

}
