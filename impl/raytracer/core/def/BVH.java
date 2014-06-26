package raytracer.core.def;

import java.util.LinkedList;
import java.util.List;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.geom.BBox;
import raytracer.math.Pair;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec3;

/**
 * Represents a bounding volume hierarchy acceleration structure
 */
public class BVH extends BVHBase {

	private final List<Obj> objList;

	public BVH() {
		objList = new LinkedList<Obj>();
	}

	@Override
	public BBox bbox() {
		BBox result = BBox.EMPTY;

		for (final Obj obj : objList) {

			Float kl0 = Math.min(result.getMin().get(0), obj.bbox().getMin()
					.get(0));
			Float kl1 = Math.min(result.getMin().get(1), obj.bbox().getMin()
					.get(1));
			Float kl2 = Math.min(result.getMin().get(2), obj.bbox().getMin()
					.get(2));
			Float kl3 = Math.min(result.getMin().get(3), obj.bbox().getMin()
					.get(3));
			Float gr0 = Math.max(result.getMax().get(0), obj.bbox().getMax()
					.get(0));
			Float gr1 = Math.max(result.getMax().get(1), obj.bbox().getMax()
					.get(1));
			Float gr2 = Math.max(result.getMax().get(2), obj.bbox().getMax()
					.get(2));
			Float gr3 = Math.max(result.getMax().get(3), obj.bbox().getMax()
					.get(3));

			result = new BBox(new Point(kl0, kl1, kl2, kl3), new Point(gr0,
					gr1, gr2, gr3));
		}

		return result;
	}

	/**
	 * Adds an object to the acceleration structure
	 * 
	 * @param prim
	 *            The object to add
	 */
	@Override
	public void add(final Obj prim) {
		objList.add(prim);

	}

	/**
	 * Builds the actual bounding volume hierarchy
	 */
	@Override
	public void buildBVH() {
		// Implement this method
		// ruft nachher alles auf

	}

	@Override
	public Pair<Point, Point> calculateMinMax() {
		BBox help = this.bbox();
		return new Pair<Point, Point>(help.getMin(), help.getMax());

	}

	@Override
	public int calculateSplitDimension(final Vec3 size) {
		if ((size.x() <= size.y()) & (size.x() <= size.z()))
			return 1;
		if ((size.y() <= size.x()) & (size.y() <= size.z()))
			return 2;
		else
			return 3;
	}

	@Override
	public void distributeObjects(final BVHBase a, final BVHBase b,
			final int splitdim, final float splitpos) {
		
		List<Obj> helpList = this.getObjects();
		if (splitdim==1){
			// x - achsen split
			for (Obj o : helpList) {
				Point m = o.bbox().getMin().add((o.bbox().getMax().sub(o.bbox().getMin()).scale(0.5f)));
				if (m.x()<splitpos)
					a.add(o);
				else 
					b.add(o);
				
			}
		}
		if (splitdim==2){
			// y - achsen split
			for (Obj o : helpList) {
				Point m = o.bbox().getMin().add((o.bbox().getMax().sub(o.bbox().getMin()).scale(0.5f)));
				if (m.y()<splitpos)
					a.add(o);
				else 
					b.add(o);
			}
		}
		if (splitdim==3){
			// z - achsen split
			for (Obj o : helpList) {
				Point m = o.bbox().getMin().add((o.bbox().getMax().sub(o.bbox().getMin()).scale(0.5f)));
				if (m.z()<splitpos)
					a.add(o);
				else 
					b.add(o);
			
			}
		}	
	}

	@Override
	public Hit hit(final Ray ray, final Obj obj, final float tmin,
			final float tmax) {
		// implemened
		if (this.bbox().hit(ray, tmin, tmax).hits()) {
			
			List<Obj> helpList = this.getObjects();

			for (Obj o : helpList) {

				Hit helpHit = o.hit(ray, obj, tmin, tmax);
				if (helpHit.hits()) {
					return helpHit;
				}
			}
		}
		return Hit.No.get();
	}

	@Override
	public List<Obj> getObjects() {
		// implemened
		return objList;
	}
}
