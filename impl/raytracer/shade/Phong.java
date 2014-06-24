package raytracer.shade;

import java.util.Collection;

import com.sun.corba.se.impl.orbutil.closure.Constant;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Constants;

public class Phong implements Shader {

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
		Color Ambient = this.ambient;
		// Farbe des darunterliegenden Shaders = 1 !!! (Überbleibsel aus altem
		// Code in Beschreibung)
		Collection<LightSource> Lichter = trace.getScene().getLightSources();

		Color helpLichter = new Color(0, 0, 0);

		while (Lichter.iterator().hasNext()) {
			helpLichter.add(Lichter.iterator().next().getColor());
		}

		// Winkel berechnen pro einzelner Lichtstrahl ? Übersprungen bis jetzt

		Color Diffus = helpLichter.scale(this.diffuse).scale(
				Math.max(
						Constants.EPS,
						hit.getNormal().normalized()
								.dot(trace.getRay().dir().normalized())));

		Color Specular = helpLichter.scale(this.specular).scale(Math.max(Constants.EPS, /* an der Oberfläche gespiegelter Sehstrahl r */
				.dot(trace.getRay().dir().normalized())));

		return Ambient.add(Diffus.add(Specular));

	}
}
