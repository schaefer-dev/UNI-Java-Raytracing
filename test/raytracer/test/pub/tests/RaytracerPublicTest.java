package raytracer.test.pub.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static raytracer.test.pub.tests.TestUtil.DEFAULT_TIMEOUT;

import org.junit.Before;
import org.junit.Test;

import raytracer.core.Hit;
import raytracer.core.OBJReader;
import raytracer.core.Obj;
import raytracer.core.def.BVHBase;
import raytracer.core.def.StandardObj;
import raytracer.geom.BBox;
import raytracer.geom.Primitive;
import raytracer.math.Color;
import raytracer.math.Constants;
import raytracer.math.Pair;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec3;
import raytracer.shade.SingleColor;

public class RaytracerPublicTest extends RayTracerTestBase {

	private BVHBase studentBvh;

	@Before
	public void setUp() {
		studentBvh = createStudentBVH();
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testReadSimple() {
		printCurrentMethodName();
		final ComparableAccelarator student = new ComparableAccelarator();
		final ComparableAccelarator ref = new ComparableAccelarator();
		getSimple(ref);
		try {
			OBJReader.read("obj/simple.obj", student, new SingleColor(
					Color.GRAY), 1.0f, new Vec3(0, 0, 0));
		} catch (final Exception e) {
			fail("No Exception expected but was: "
					+ e.getClass().getSimpleName());
		}
		assertTrue(
				"Simple Triangle was not read succesfully (or equals in Triangle is broken)",
				student.equals(ref));
	}

	private void getSimple(final ComparableAccelarator ref) {
		Primitive t;

		t = createTriangle(new Point(-1f, 0f, -1f), new Point(1f, 0f, -1f),
				new Point(-1f, 0f, 1f));
		ref.add(new StandardObj(t, new SingleColor(Color.GRAY)));

	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testBVH_One() {
		printCurrentMethodName();
		final Obj obj = createSomeObject();
		studentBvh.add(obj);
		studentBvh.buildBVH();
		assertTrue(studentBvh.getObjects().size() == 1);
		assertTrue(studentBvh.getObjects().get(0).equals(obj));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testBVH_SplitDimX() {
		printCurrentMethodName();
		assertEquals("Split dimension X not correct", 0,
				studentBvh.calculateSplitDimension(new Vec3(1.0f, 0.0f, 0.0f)));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testBVH_BBox1() {
		printCurrentMethodName();
		final Obj object = createSomeObject(createSphere(new Point(1, 2, 3), 4));
		final BBox box = object.bbox();
		studentBvh.add(object);
		assertTrue("BHV bbox not equal", box.equals(studentBvh.bbox()));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testBVH_MinMax1() {
		printCurrentMethodName();
		final Obj object = createSomeObject(createSphere(new Point(1, 2, 3), 4));
		final BBox box = object.bbox();
		studentBvh.add(object);
		final Pair<Point, Point> minMax = studentBvh.calculateMinMax();
		assertTrue("BVH MinMax not equal", box.getMin().equals(minMax.a));
		assertTrue("BVH MinMax not equal", box.getMin().equals(minMax.b));
	}

	public void testPlaneNoHitOrthogonal() {
		printCurrentMethodName();
		final Primitive plane = createPlane(new Vec3(1, 0, 0), new Point(0, 0, 0));
		final Hit hit = plane.hit(new Ray(new Point(0, 0, 0), new Vec3(0, 0, 1)),
				new StandardObj(plane, new SingleColor(Color.GRAY)), 0,
				Float.MAX_VALUE);
		assertFalse(
				"Ray should not hit plane with normal vector orthogonal to ray direction",
				hit.hits());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testPlaneHitSimple() {
		printCurrentMethodName();
		final Primitive plane = createPlane(new Vec3(0f, 0f, 1f), new Point(0f, 0f,
				1f));
		final Hit hit = plane.hit(new Ray(new Point(0, 0, 0), new Vec3(0, 0, 1)),
				new StandardObj(plane, new SingleColor(Color.GRAY)), 0,
				Float.MAX_VALUE);
		assertTrue("Ray should hit simple orthogonal plane", hit.hits());
		assertTrue("Hit normal should be (0,0,1) or (0,0,-1)",
				hit.getNormal().equals(new Vec3(0, 0, 1))
				|| hit.getNormal().equals(new Vec3(0, 0, -1)));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testSphereHitSimple() {
		printCurrentMethodName();
		final Primitive sphere = createSphere(new Point(0, 0, 42), 10);
		final Hit hit = sphere.hit(new Ray(new Point(0, 0, 0), new Vec3(0, 0, 1)),
				new StandardObj(sphere, new SingleColor(Color.GRAY)), 0,
				Float.MAX_VALUE);
		assertTrue("Ray should hit sphere", hit.hits());
		assertTrue("Hit normal should be (0,0,1) or (0,0,-1)",
				hit.getNormal().equals(new Vec3(0, 0, 1))
				|| hit.getNormal().equals(new Vec3(0, 0, -1)));
		assertTrue("Hit distance should be 32.0",
				Math.abs(hit.getParameter() - 32.0f) < Constants.EPS);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testPhongBlack() {
		printCurrentMethodName();
		checkDefaultPhong(new SingleColor(Color.MAGENTA), Color.BLACK, 0f, 0f,
				0f, Color.BLACK, Color.BLACK, Color.BLACK);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testPhongDiffuseSimple() {
		printCurrentMethodName();
		checkDefaultPhong(new SingleColor(Color.MAGENTA), Color.BLACK, 1f, 0f,
				0f, new Color(0.99f, 0f, 0.99f), new Color(0.7f, 0f, 0.7f),
				new Color(0.14f, 0f, 0.14f));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testPhongSpecularSimple() {
		printCurrentMethodName();
		checkDefaultPhong(new SingleColor(Color.YELLOW), Color.BLACK, 0f, 1f,
				0f, Color.WHITE, Color.WHITE, Color.WHITE);
	}

	// Shader constructor tests

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testPhongConstructor_Null() {
		printCurrentMethodName();
		assertTrue(createPhongWithException(IllegalArgumentException.class,
				null, Color.WHITE, 1.0f, 1.0f, 1.0f));
		assertTrue(createPhongWithException(IllegalArgumentException.class,
				new SingleColor(Color.BLACK), null, 1.0f, 1.0f, 1.0f));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testCheckerBoardSimple() {
		printCurrentMethodName();
		checkColorCheckerboardSimple(new SingleColor(Color.BLACK),
				new SingleColor(Color.WHITE), Color.BLACK, Color.WHITE);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testCheckerBoardConstructor_Null() {
		printCurrentMethodName();
		assertTrue(createCheckerBoardWithException(
				IllegalArgumentException.class, null,
				new SingleColor(Color.RED), 1.0f));
		assertTrue(createCheckerBoardWithException(
				IllegalArgumentException.class, new SingleColor(Color.RED),
				null, 1.0f));
	}
}
