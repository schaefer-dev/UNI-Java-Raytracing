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

		if (this.getObjects().size() > THRESHOLD){
			
		
		BVH b = new BVH();
		BVH a = new BVH();
			
			Vec3 diag = this.bbox().getMax().sub(this.bbox().getMin());
		
			int dimension = this.calculateSplitDimension(diag);
			
			float mid = (this.bbox().getMin().get(dimension)+this.bbox().getMax().get(dimension))/2;

			
			distributeObjects(a, b, dimension, mid);
			
			objList.clear();
			boundingBox = BBox.EMPTY;
			
			// bei Jannis klappt es so muss also fehler sein bei distributeObjects oder splitdimension
			
			
			/*if (a.objList.isEmpty())
				System.out.print("a empty");
			if (b.objList.isEmpty())
				System.out.print("b empty");
			objList.add(a);
			objList.add(b);
			*/
			 //so komplexer sonderfall nicht nötig?
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
					Point getmin = (BBox.create(a.bbox().getMin(),b.bbox().getMin()).getMin());
					Point getmax = (BBox.create(a.bbox().getMin(),b.bbox().getMin()).getMin());
					this.boundingBox=BBox.create(getmin,getmax);
				}
			}

		}
		System.out.print("builded   ");
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
		
		if (helpList.get(0) instanceof Primitive){ 
			Hit nearest = Hit.No.get();
			if (this.bbox().hit(ray, tmin, ttmax).hits()) {
			
				for (final Obj p : helpList) {
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
			System.out.print("pHit");
			return nearest;
		}
		else{ // hier failt er NUR hier
			
			Hit nearest = Hit.No.get();
			Obj helpObj = null;

			if (this.bbox().hit(ray,tmin,ttmax).hits()){
				
				for (Obj o : helpList) {
					Hit helpHit = o.hit(ray, o, tmin, ttmax);
					if (helpHit.hits()) {
						final float t = helpHit.getParameter();
						if (t < ttmax) {
							nearest = helpHit;
							ttmax = t;
							helpObj=o;
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
