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

	private List<Obj> objList;
	private BBox boundingBox;

	public BVH() {
		objList = new LinkedList<Obj>();
		boundingBox = BBox.EMPTY;
	}

	@Override
	public BBox bbox() {
		
		if (boundingBox == BBox.EMPTY){
		
		Point hilfmin = objList.get(0).bbox().getMin();
		Point hilfmax = objList.get(0).bbox().getMax();
		Point hilfmin2;
		Point hilfmax2;
		
		for (final Obj obj : objList) {	
			hilfmin2 = obj.bbox().getMin();
			hilfmax2 = obj.bbox().getMax();
			hilfmin = hilfmin.min(hilfmin2);
			hilfmax = hilfmax.max(hilfmax2);
		}
		boundingBox = BBox.create(hilfmin,hilfmax);

		
		}
		
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
		/*
		Pair<Point, Point> pair = calculateMinMax();
		if (!pair.a.equals(pair.b)) {
			int splitdim = calculateSplitDimension(pair.a.sub(pair.b));
			float splitpos = 0;
				splitpos = (pair.b.get(splitdim) + pair.a.get(splitdim)) / 2;
			BVH a = new BVH();
			BVH b = new BVH();
			distributeObjects(a, b, splitdim, splitpos);
			List<Obj> prim2 = new LinkedList<Obj>();
			prim2.add(a);
			prim2.add(b);
			objList = prim2;
			if (a.objList.size() > THRESHOLD) {
				a.buildBVH();
			}
			if (b.objList.size() > THRESHOLD) {
				b.buildBVH();
			}
		} */
		Pair<Point, Point> checkMinMax = calculateMinMax();	
	
		if ((this.getObjects().size() > THRESHOLD)&(!(checkMinMax.a.equals(checkMinMax.b)))) {
		
			BVH b = new BVH();
			BVH a = new BVH();
			
			Vec3 diag = this.bbox().getMax().sub(this.bbox().getMin());
		
			int dimension = this.calculateSplitDimension(diag);
			
			float mid = (checkMinMax.b.get(dimension)+checkMinMax.a.get(dimension))/2;

			
			distributeObjects(a, b, dimension, mid);
			
			objList.clear();
			boundingBox = BBox.EMPTY;
			
			
			if (a.objList.isEmpty())
				objList.add(b);
			else{
				if (b.objList.isEmpty())
					objList.add(a);
				else{
					a.buildBVH();
					a.boundingBox=a.bbox();
					b.buildBVH();
					b.boundingBox=b.bbox();
					objList.add(a);
					objList.add(b);
				}
			}

		}
		//System.out.print("builded   "); */
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
		if ((Math.abs(size.get(0)) >= Math.abs(size.get(1))) & Math.abs(size.get(0)) >= Math.abs(size.get(2)))
			return 0;
		if ((Math.abs(size.get(1)) >= Math.abs(size.get(0))) & (Math.abs(size.get(1)) >= Math.abs(size.get(2))))
			return 1;
		else
			return 2;
	}

	@Override
	public void distributeObjects(final BVHBase a, final BVHBase b,
			final int splitdim, final float splitpos) {
		
		List<Obj> helpList = this.getObjects();						// Nicht splitten anhand von mitte sondern an getMin() Ecke!
		
			// x - achsen split
			for (Obj o : helpList) {
				// wenn Mittelpunkt: Point m = o.bbox().getMin().add((o.bbox().getMax().sub(o.bbox().getMin()).scale(0.5f)));
				Point m = o.bbox().getMin();
				if (m.get(splitdim)<splitpos)
					a.add(o);
				else 
					b.add(o);
			}
		
	}

	@Override
	public Hit hit(final Ray ray, final Obj obj, final float tmin,
			final float tmax) {									/* obj -> The object to compute the intersection with*/ 
		// implemened
			
	
		float ttmax=tmax;										
		
		List<Obj> helpList = this.getObjects();
		
		Hit nearest = Hit.No.get();
		
		if (helpList.get(0) instanceof Primitive){ 
			
			if (this.bbox().hit(ray, tmin, ttmax).hits()) {
			
				for (Obj p : helpList) {
					final Hit helpHit = p.hit(ray, p, tmin, ttmax);
					if (helpHit.hits()) {
						final float t = helpHit.getParameter();
						if (t < ttmax) {
							nearest = helpHit;
							ttmax = t;
						}
					}
				}

			}
			return nearest;
		}
		else{ 
			
			Obj helpObj = null;

			if (this.bbox().hit(ray,tmin,ttmax).hits()){
				
				for (Obj bva : helpList) {
					Hit helpHit = bva.hit(ray, bva, tmin, ttmax);
					if (helpHit.hits()) {
						final float t = helpHit.getParameter();
						if (t < ttmax) {
							nearest = helpHit;
							ttmax = t;
							helpObj=bva;
					}		
				}	
			}
			if (helpObj==null)
				return Hit.No.get();
			return helpObj.hit(ray, helpObj, tmin, ttmax);
			
		}
			return Hit.No.get();
		}
		
	}
	

	@Override
	public List<Obj> getObjects() {
		// implemened
		return objList;
	}
}
