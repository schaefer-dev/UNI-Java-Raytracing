package raytracer.core.def;

import java.util.LinkedList;
import java.util.List;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.geom.BBox;
import raytracer.geom.Primitive;
import raytracer.math.Pair;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec3;

/**
 * Represents a bounding volume hierarchy acceleration structure
 */


// TODO calcMinMax
public class BVH extends BVHBase {

	private final List<Obj> objList;
	private final BBox boundingBox;

	public BVH() {
		objList = new LinkedList<Obj>();
		boundingBox = BBox.EMPTY;
	}

	@Override
	public BBox bbox() {
		
		if (boundingBox == BBox.EMPTY){
		
		BBox hilf = objList.get(0).bbox();
		
		for (final Obj obj : objList) {	
			hilf = BBox.surround(obj.bbox(), hilf);
		}
		BBox result = BBox.create(hilf.getMin(),hilf.getMax());

		return result;
		}
		else
			return boundingBox;
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
		if (this.getObjects().size() <= THRESHOLD){
			return;
		}
		BVH b = new BVH();
		BVH a = new BVH();
			
			Vec3 diag = this.bbox().getMax().sub(this.bbox().getMin());
		
			int dimension = this.calculateSplitDimension(diag);
			
			float mid = (this.bbox().getMin().get(dimension)+this.bbox().getMax().get(dimension))/2;

			
			distributeObjects(a, b, dimension, mid);
			
			objList.clear();
			if (a.objList.isEmpty())
				objList.add(b);
			else{
				if (b.objList.isEmpty())
					objList.add(a);
				else{
					a.buildBVH();
					b.buildBVH();
					objList.add(a);
					objList.add(b);
				}
			}

	}

	@Override
	public Pair<Point, Point> calculateMinMax() {
		
		BBox result = BBox.create(objList.get(0).bbox().getMin(),objList.get(0).bbox().getMin());
		
		for (final Obj obj : objList) {	
			result = BBox.surround(BBox.create(obj.bbox().getMin(),obj.bbox().getMin()), result);
		}
		
		return new Pair<Point, Point>(result.getMin(), result.getMax());

	}

	@Override
	public int calculateSplitDimension(final Vec3 size) {
		if ((size.get(0) >= size.get(1)) & (size.get(0) >= size.get(2)))
			return 0;
		if ((size.get(1) >= size.get(0)) & (size.get(1) >= size.get(2)))
			return 1;
		else
			return 2;
	}

	@Override
	public void distributeObjects(final BVHBase a, final BVHBase b,
			final int splitdim, final float splitpos) {
		
		List<Obj> helpList = this.getObjects();						// Nicht splitten anhand von mitte sondern an getMin() ecke
		if (splitdim==0){
			// x - achsen split
			for (Obj o : helpList) {
				Point m = o.bbox().getMin().add((o.bbox().getMax().sub(o.bbox().getMin()).scale(0.5f)));
				if (m.x()<splitpos)
					a.add(o);
				else 
					b.add(o);
				
			}
		}
		if (splitdim==1){
			// y - achsen split
			for (Obj o : helpList) {
				Point m = o.bbox().getMin().add((o.bbox().getMax().sub(o.bbox().getMin()).scale(0.5f)));
				if (m.y()<splitpos)
					a.add(o);
				else 
					b.add(o);
			}
		}
		if (splitdim==2){
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
			final float tmax) {									/* obj -> The object to compute the intersection with*/ 
		// implemened
		
		float ttmax=tmax;										// fehlt ein Hit.no.get()
		
		List<Obj> helpList = this.getObjects();
		
		if (helpList.get(0) instanceof Primitive){
		
		if (this.bbox().hit(ray, tmin, tmax).hits()) {
			
			Hit nearest = Hit.No.get();
			for (final Obj p : helpList) {
				final Hit hit = p.hit(ray, p, tmin, ttmax);
				if (hit.hits()) {
					final float t = hit.getParameter();
					if (t < ttmax) {
						nearest = hit;
						ttmax = t;
					}
				}
			}

			return nearest;
		}
		return Hit.No.get();
		}
		else{


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
