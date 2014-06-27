package raytracer.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
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
	 *             is null, the shader is null, the translate vector is null,
	 *             the translate vector is not finite or scale does not
	 *             represent a legal (finite) floating point number
	 */
	public static void read(final String filename,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
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
		
		if ((in==null)|(accelerator==null)|(shader==null)|(translate==null)|(!translate.isFinite())|(Float.isNaN(scale)))
			throw new IllegalArgumentException("filename or accelerator or shader or translate == null or translate is infinite or scale is no valid float");
		
		java.util.Scanner sc = new java.util.Scanner(in);
		
		sc.useLocale(Locale.ENGLISH);
		
		boolean Kommentar= false;
		
		ArrayList<Point> pointList = new ArrayList<Point>();
		
		pointList.add(new Point(0,0,0));
			
		
		/* ab hier schleife über File */
		
		while (sc.hasNextLine()){
			
			Kommentar =false;
			
			if (sc.hasNext(Pattern.compile("#"))){
				Kommentar=true;
			}
			if (sc.hasNext(Pattern.compile("v"))){				
				boolean error = false;
				float v1=0;
				float v2=0;
				float v3=0;				
				sc.next();
				
				if (sc.hasNextFloat())	{		
					v1 = sc.nextFloat()*scale;		
				}
				else 
					error = true;
				if (sc.hasNextFloat()){
					v2 = sc.nextFloat();
				}
				else 
					error = true;	
				if (sc.hasNextFloat()){
					v3 = sc.nextFloat()*scale;
				}
				else 
					error = true;				
				
				if (!error){									
					pointList.add(new Point(v1,v2,v3));	
				}
				
						
			}
			if (sc.hasNext(Pattern.compile("f"))){
				boolean error = false;
				int f1=0;
				int f2=0;
				int f3=0;
				
				sc.next();
				
				if (sc.hasNextInt())	{
					f1 = sc.nextInt();		
				}
				else 
					error = true;
				
				if (sc.hasNextInt()){
					f2 = sc.nextInt();
				}
				else 
					error = true;
				
				if (sc.hasNextInt()){
					f3 = sc.nextInt();
				}
				else 
					error = true;
					
				
				if (!error){					
					
					Point d1 = pointList.get(f1);
					Point d2 = pointList.get(f2);
					Point d3 = pointList.get(f3);
					
					if ((d1==null)|(d2==null)|(d3==null)) {
						
					}
					else
						accelerator.add(new StandardObj((GeomFactory.createTriangle(d1.add(translate),d2.add(translate),d3.add(translate))),shader));
		
				}
				
			}	
			sc.nextLine();
			
		}		
	    System.out.print("Reader finished");
	    sc.close();
	    return;
	}
}
