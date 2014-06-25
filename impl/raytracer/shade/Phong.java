package raytracer.shade;

import java.util.Collection;

import com.sun.corba.se.impl.orbutil.closure.Constant;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Constants;
import raytracer.math.Vec3;

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

		// failt sobald es angewendet wird, könnte auch an Sphere liegen!!

		/* eintreffende Lichtstrahlen-Farben berechnen */
	
		// LightSources kann man auch aus Trace bekommen
		
		Collection<LightSource> Lichter = trace.getScene().getLightSources();
		int LichterAnzahl = Lichter.size();
		
		LightSource[] LichterArray = new LightSource[LichterAnzahl];
		
		Lichter.toArray(LichterArray);
		
		Color helpLichter = LichterArray[0].getColor();
		Color helpLichter2 = LichterArray[0].getColor();
	

		
		Color IAmbient = this.ambient;
		

		float helpFloat1 = (diffuse * Math.max(0, trace.getRay().dir()
				.normalized().dot(hit.getNormal().normalized())));

		// TODO IDiffuse für jede Lichtquelle berechnen die trifft
		Color IDiffuse = helpLichter.mul(inner.shade(hit, trace)).scale(
				helpFloat1);

		Vec3 helpVek2 = trace.getRay().base().sub(hit.getPoint());

		Vec3 helpVek3 = trace.getRay().reflect(hit.getPoint(), hit.getNormal())
				.dir();

		Color ISpecular = helpLichter2.scale((float) (specular * Math.pow(
				Math.max(0, diffuse * (helpVek3).angle(helpVek2)), shininess)));

		return IAmbient.add(IDiffuse.add(ISpecular));
		
		

	}

	/*
	 * private Color AmbientColor(Hit hit, Trace trace) { return this.ambient; }
	 * 
	 * 
	 * private Color DiffusColor(Hit hit, Trace trace) {
	 * 
	 * // Farbe des darunterliegenden Shaders = 1 !!! (Überbleibsel aus altem //
	 * Code in Beschreibung) Collection<LightSource> Lichter =
	 * trace.getScene().getLightSources();
	 * 
	 * Color helpLichter = new Color(0, 0, 0);
	 * 
	 * while (Lichter.iterator().hasNext()) {
	 * helpLichter.add(Lichter.iterator().next().getColor()); }
	 * 
	 * // Winkel berechnen pro einzelner Lichtstrahl ? Übersprungen bis jetzt
	 * 
	 * Color Diffus = helpLichter.scale(this.diffuse).scale( Math.max(
	 * Constants.EPS, hit.getNormal().normalized()
	 * .dot(trace.getRay().dir().normalized()))); return Diffus; }
	 * 
	 * 
	 * private Color SpecularColor(Hit hit, Trace trace) {
	 * 
	 * Collection<LightSource> Lichter = trace.getScene().getLightSources();
	 * 
	 * Color helpLichter = new Color(0, 0, 0);
	 * 
	 * while (Lichter.iterator().hasNext()) {
	 * helpLichter.add(Lichter.iterator().next().getColor()); }
	 * 
	 * Color Specular = helpLichter.scale(this.specular).scale(
	 * Math.max(Constants.EPS, /* an der Oberfläche gespiegelter Sehstrahl r
	 * 
	 * 
	 * return new Color(1,1,1); }
	 */
}
