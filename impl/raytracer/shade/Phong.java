package raytracer.shade;

import java.util.Collection;

//import com.sun.corba.se.impl.orbutil.closure.Constant;

import java.util.Iterator;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Point;
import raytracer.math.Ray;
//import raytracer.math.Constants;
import raytracer.math.Vec3;

public class Phong implements Shader {

	// Test

	private final Shader inner;
	private final Color ambient;
	private final float diffuse;
	private final float specular;
	private final float shininess;

	public Phong(final Shader inner, final Color ambient, final float diffuse,
			final float specular, final float shininess) {
		this.inner = inner;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
	}

	@Override
	public Color shade(Hit hit, Trace trace) {

		// TODO abfangen von wird das Objekt überhaupt getroffen fraglich! +
		// TODO Pixelfehler beheben

		Color result = ambient;

		Collection<LightSource> Lichter = trace.getScene().getLightSources();
		int LichterAnzahl = Lichter.size();

		LightSource[] LichterArray = new LightSource[LichterAnzahl];

		Iterator<LightSource> iterator = Lichter.iterator();

		for (int i = 0; i < LichterAnzahl; i++) {
			LichterArray[i] = iterator.next(); // stimmt so??
		}

		// Lichter.toArray(LichterArray);

		Color help = inner.shade(hit, trace);

		for (int i = 0; i < LichterAnzahl; i++) {

			Vec3 v = LichterArray[i].getLocation().sub(hit.getPoint())
					.normalized();

			Color Cl = LichterArray[i].getColor();

			Color Idiffuse = Cl.mul(help).scale(
					diffuse * Math.max(0, hit.getNormal().dot(v)));

			if (!(trace.getScene().hit(new Ray(hit.getPoint(), v)).hits())) { // warum
																				// funktioniert
																				// ????
																				// Warum
																				// zählt
																				// Lichtquelle
																				// nicht
																				// als
																				// Treffer?
				result = result.add(Idiffuse);
			}

		}

		// neg invertiert vektor!

		Vec3 r = trace.getRay().dir().neg().reflect(trace.getHit().getNormal())
				.normalized();

		for (int i = 0; i < LichterAnzahl; i++) {

			Vec3 v = hit.getPoint().sub(LichterArray[i].getLocation())
					.normalized();

			Color Cl = LichterArray[i].getColor();

			Color Ispecular = Cl.scale(specular
					* (float) Math.pow(Math.max(0, r.angle(v)), shininess));

			if (!(trace.getScene().hit(new Ray(hit.getPoint(), v.neg())).hits())) { // warum
																					// funktioniert
																					// ????
																					// s.o
				result = result.add(Ispecular);
			}
		}

		return result;

	}

}
