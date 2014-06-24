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

class Sphere extends BBoxedPrimitive {
	private final float r;
	private final Point c;

	public Sphere(float r, Point c) {
		super(BBox.create(c.add(new Vec3(r, r, r)), c.sub(new Vec3(r, r, r))));
		this.r = r;
		this.c = c;
	}

	@Override
	public Hit hitTest(final Ray ray, final Obj obj, final float tmin,
			final float tmax) {
		return new LazyHitTest(obj) {
			private Point point = null;
			// tmin und tmax noch einbauen??
			private float z;

			@Override
			public float getParameter() {
				return z;
			}

			@Override
			public Point getPoint() {
				if (point == null)
					point = ray.eval(z).add(ray.invDir().scale(0.0001f));
				return point;
			}

			@Override
			protected boolean calculateHit() {
				// TODO implement this Method

				// tmin und tmax noch einbauen??

				float b = ray
						.dir()
						.scale(2)
						.dot(ray.base().sub(c).x(), ray.base().sub(c).y(),
								ray.base().sub(c).z(), ray.base().sub(c).w());
				float cquad = ray.base().sub(c).add(new Vec3(0, 0, 0))
						.dot(ray.base().sub(c).add(new Vec3(0, 0, 0)))
						- r * r;

				if (((b * b) - 4 * cquad) < 0)
					return false;

				float lamda1 = ((-b) + (float) Math.sqrt((b * b) - 4 * cquad)) / 2;

				float lamda2 = ((-b) - (float) Math.sqrt((b * b) - 4 * cquad)) / 2;

				if (lamda1 <= lamda2) {
					if (lamda1 < Constants.EPS)
						return false;
					else {
						z = lamda1;
						return true;
					}
				} else {
					if (lamda2 < Constants.EPS)
						return false;
					else {
						z = lamda2;
						return true;
					}
				}

			}

			@Override
			public Vec3 getNormal() {

				return (point.sub(c)).normalized();
			}

			@Override
			public Vec2 getUV() {
				return raytracer.geom.Util.computeSphereUV(this.getNormal());
			}
		};
	}

	@Override
	public int hashCode() {
		float hilf = r * 3;
		return c.hashCode() ^ (int) hilf;
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Sphere) {
			final Sphere cobj = (Sphere) other;
			return (cobj.c.equals(c) && (cobj.r == r)); // Muesste richtig sein?
		}
		return false;
	}

}
