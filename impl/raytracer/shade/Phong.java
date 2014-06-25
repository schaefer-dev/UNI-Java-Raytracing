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

		Color result = ambient;

		Collection<LightSource> Lichter = trace.getScene().getLightSources();
		int LichterAnzahl = Lichter.size();

		LightSource[] LichterArray = new LightSource[LichterAnzahl];

		Lichter.toArray(LichterArray);

		
		Color Csup = inner.shade(hit, trace);
		
		for (int i = 0; i < LichterAnzahl; i++) {

			Vec3 v = LichterArray[i].getLocation().sub(hit.getPoint())
					.normalized();

			Color Cl = LichterArray[i].getColor();

			Color Idiffuse = Cl.mul(Csup).scale(
					diffuse * Math.max(0, hit.getNormal().dot(v)));

			result = result.add(Idiffuse);
		}

		Vec3 r = trace.getScene().getCamera().cast(1, 1).base()
				.sub(hit.getPoint()).reflect(hit.getNormal()).normalized();
		
		for (int i = 0; i < LichterAnzahl; i++) {
		
		//Vec3 v = LichterArray[i].getLocation().sub(hit.getPoint()).normalized();
		
		Vec3 v = hit.getPoint().sub(LichterArray[i].getLocation()).normalized();
		
		Color Cl = LichterArray[i].getColor();
		
		Color Ispecular = Cl.scale(specular*(float)Math.pow(Math.max(0, r.angle(v)), shininess));
		
		result=result.add(Ispecular);
		
		}
		
		return result;
	}

	/*
	 * Version 2.0
	 * 
	 * // LightSources kann man auch aus Trace bekommen
	 * 
	 * Collection<LightSource> Lichter = trace.getScene().getLightSources(); int
	 * LichterAnzahl = Lichter.size();
	 * 
	 * LightSource[] LichterArray = new LightSource[LichterAnzahl];
	 * 
	 * Lichter.toArray(LichterArray);
	 * 
	 * Color helpLichter = LichterArray[0].getColor(); Color helpLichter2 =
	 * LichterArray[0].getColor();
	 * 
	 * 
	 * 
	 * Color IAmbient = this.ambient;
	 * 
	 * 
	 * float helpFloat1 = (diffuse * Math.max(0, trace.getRay().dir()
	 * .normalized().dot(hit.getNormal().normalized())));
	 * 
	 * // TODO IDiffuse für jede Lichtquelle berechnen die trifft Color IDiffuse
	 * = helpLichter.mul(inner.shade(hit, trace)).scale( helpFloat1);
	 * 
	 * Vec3 helpVek2 = trace.getRay().base().sub(hit.getPoint());
	 * 
	 * Vec3 helpVek3 = trace.getRay().reflect(hit.getPoint(), hit.getNormal())
	 * .dir();
	 * 
	 * Color ISpecular = helpLichter2.scale((float) (specular * Math.pow(
	 * Math.max(0, diffuse * (helpVek3).angle(helpVek2)), shininess)));
	 * 
	 * return IAmbient.add(IDiffuse.add(ISpecular));
	 */

	/*
	 * version 1.0
	 * 
	 * 
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
