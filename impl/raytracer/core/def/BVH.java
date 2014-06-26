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


// TODO calcMinMax
public class BVH extends BVHBase {

	private final List<Obj> objList;

	public BVH() {
		objList = new LinkedList<Obj>();
	}

	@Override
	public BBox bbox() {
		BBox result = BBox.create(this.calculateMinMax().a,this.calculateMinMax().b);

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
		if (this.getObjects().size() <= THRESHOLD){
			return;
		}
		BVH b = new BVH();
		BVH a = new BVH();
			
			Vec3 diag = this.bbox().getMax().sub(this.bbox().getMin());
		
			int dimension = this.calculateSplitDimension(diag);
			
			float mid=0f;
			
			if (dimension==1){
				//x-achsen split
				mid = (this.bbox().getMin().x()+this.bbox().getMax().x())/2;
			}
			
			if (dimension==2){
				//y-achsen split
				mid = (this.bbox().getMin().y()+this.bbox().getMax().y())/2;	
			}
			
			if (dimension==3){
				//z-achsen split
				mid = (this.bbox().getMin().z()+this.bbox().getMax().z())/2;
			}
			
			distributeObjects(a, b, dimension, mid);
			
			objList.clear();
			a.buildBVH();
			b.buildBVH();
			objList.add(a);
			objList.add(b);

	}

	@Override
	public Pair<Point, Point> calculateMinMax() {
		
		BBox result = BBox.EMPTY;
		
		for (final Obj obj : objList) {	
			result = BBox.surround(obj.bbox(), result);
		}
		
		return new Pair<Point, Point>(result.getMin(), result.getMax());

	}

	@Override
	public int calculateSplitDimension(final Vec3 size) {
		if ((size.x() >= size.y()) & (size.x() >= size.z()))
			return 1;
		if ((size.y() >= size.x()) & (size.y() >= size.z()))
			return 2;
		else
			return 3;
	}

	@Override
	public void distributeObjects(final BVHBase a, final BVHBase b,
			final int splitdim, final float splitpos) {
		
		List<Obj> helpList = this.getObjects();						// Nicht splitten anhand von mitte sondern an getMin() ecke
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
			final float tmax) {									/* obj -> The object to compute the intersection with*/ 
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
