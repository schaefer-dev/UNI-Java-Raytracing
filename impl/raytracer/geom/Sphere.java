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
import raytracer.geom.Util;

class Sphere extends BBoxedPrimitive{
	private final float r;
	private final Point c;
	
	public Sphere (float r, Point c){
		super(BBox.create(c.add(new Vec3(r,r,r)), c.sub(new Vec3(r,r,r))));
		this.r=r;
		this.c=c;
	}
	
	
	@Override
	public Hit hitTest(final Ray ray, final Obj obj, final float tmin,
			final float tmax) {
		return new LazyHitTest(obj) {
			private Point point = null;
			private float z, s, t;

			@Override
			public float getParameter() {
				return z;
			}

			@Override
			public Point getPoint() {
				if (point == null)
					point = ray.eval(z).add(new Vec3(r,r,r).scale(0.0001f));	// richtig??
				return point;
			}

			@Override
			protected boolean calculateHit() {
				// TODO implement this Method
				throw new UnsupportedOperationException(
						"This method has not yet been implemented.");

			}

			@Override
			public Vec3 getNormal() {
				// TODO implement this Method
				throw new UnsupportedOperationException(
						"This method has not yet been implemented.");
			}

			@Override
			public Vec2 getUV() {
				return Util.computeSphereUV(this.getPoint().sub(c));
			}
		};
	}
	
	
	
	@Override
	public int hashCode() {
		return c.hashCode() ^ (int)r;		// was machen mit r ??
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Sphere) {
			final Sphere cobj = (Sphere) other;
			return (cobj.c.equals(c) && (cobj.r == r)); // Müsste richtig sein?
		}
		return false;
	}

}
