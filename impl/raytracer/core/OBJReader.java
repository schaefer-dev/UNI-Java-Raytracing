package raytracer.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.sun.corba.se.impl.orbutil.closure.Constant;

import raytracer.core.def.Accelerator;
import raytracer.core.def.BVH;
import raytracer.core.def.StandardObj;
import raytracer.geom.GeomFactory;
import raytracer.math.Point;
import raytracer.math.Vec3;

/**
 * Represents a model file reader for the OBJ format
 */
public class OBJReader {

	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param filename
	 *            The file to read the data from
	 * @param accelerator
	 *            The target acceleration structure
	 * @param shader
	 *            The shader which is used by all triangles
	 * @param scale
	 *            The scale factor which is responsible for scaling the model
	 * @param translate
	 *            A vector representing the translation coordinate with which
	 *            all coordinates has to be translated
	 * @throws IllegalArgumentException
	 *             If the filename is null or the empty string, the accelerator
	 *             is null, the shader is null, the translate vector is null
	 *             the translate vector is not finite or scale does not
	 *             represent a legal (finite) floating point number
	 */
	public static void read(final String filename,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		if (filename=="")
			throw new IllegalArgumentException("filename is empty");
		read(new BufferedInputStream(new FileInputStream(filename)), accelerator, shader, scale, translate);
	}


	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param in
	 *            The InputStream of the data to be read.
	 * @param accelerator
	 *            The target acceleration structure
	 * @param shader
	 *            The shader which is used by all triangles
	 * @param scale
	 *            The scale factor which is responsible for scaling the model
	 * @param translate
	 *            A vector representing the translation coordinate with which
	 *            all coordinates has to be translated
	 * @throws IllegalArgumentException
	 *             If the filename is null or the empty string, the accelerator
	 *             is null, the shader is null, the translate vector is null,
	 *             the translate vector is not finite or scale does not
	 *             represent a legal (finite) floating point number
	 */
	public static void read(final InputStream in,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		// TODO Implement this method		//Streamtokenizer verwenden?
		
		if ((in==null)|(accelerator==null)|(shader==null)|(translate==null)|(!translate.isFinite())|(Float.isNaN(scale)|(translate.isInfinity())))
			throw new IllegalArgumentException("filename or accelerator or shader or translate == null or translate is infinite or scale is no valid float");
		
		java.util.Scanner sc = new java.util.Scanner(in);
		
		sc.useLocale(Locale.ENGLISH);
		
		ArrayList<Point> pointList = new ArrayList<Point>();
		
		pointList.add(new Point(0,0,0));
			
		/* ab hier schleife über File */
		
		while (sc.hasNextLine()){
			
			String c = sc.next();
			if(c.matches("#"))
				sc.nextLine();
			
			
			if(c.matches("v")){
				Boolean Error = false;
				float f1 = 0f;
				float f2 = 0f;
				float f3 = 0f;
				if (sc.hasNextFloat())
					f1 = sc.nextFloat()*scale;
				else 
					Error = true;
				
				if (sc.hasNextFloat())
					f2 = sc.nextFloat()*scale;
				else 
					Error = true;
				
				if (sc.hasNextFloat())
					f3 = sc.nextFloat()*scale;
				else 
					Error = true;
				if (sc.hasNextFloat())
					Error = true;
				
				if (Error){
					sc.close();
					throw new InputMismatchException("v does not contain only 3 floats");
				}
				pointList.add(new Point(f1, f2, f3).add(translate));
				sc.nextLine();
			}
			
			if(c.matches("f")) {
				Boolean Error = false;
				int f1 = 0;
				int f2 = 0;
				int f3 = 0;
				if (sc.hasNextInt())
					f1 = sc.nextInt();
				else 
					Error = true;
				
				if (sc.hasNextInt())
					f2 = sc.nextInt();
				else 
					Error = true;
				
				if (sc.hasNextInt())
					f3 = sc.nextInt();
				else 
					Error = true;
				if (sc.hasNextInt())
					Error = true;
				
				if (Error){
					sc.close();
					throw new InputMismatchException("f does not contain only 3 floats");
				}
				if ((f1>=pointList.size())|(f2>=pointList.size())|(f3>=pointList.size())){
					sc.close();
					throw new IndexOutOfBoundsException("readed numbers after f contain wrong indexes");
				}
				accelerator.add(new StandardObj(GeomFactory.createTriangle(pointList.get(sc.nextInt()), pointList.get(sc.nextInt()), pointList.get(sc.nextInt())),shader));
				sc.nextLine();
			}
		}		
	    System.out.print("Reader finished");
	    sc.close();
	    return;
	}
}