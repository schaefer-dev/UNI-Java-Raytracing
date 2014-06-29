package raytracer.shade;

import raytracer.core.Shader;
import raytracer.math.Color;
import raytracer.math.Constants;

public final class ShaderFactory {
	private ShaderFactory() {
	}

	/**
	 * Generates a checkerboard structured shader. Two shaders are "placed"
	 * alternately on the whole structure.
	 * 
	 * @param a
	 *            The first shader structure (x + y even)
	 * @param b
	 *            The second shader structure (x + y odd)
	 * @param scale
	 *            The size of the tiles
	 * @throws IllegalArgumentException
	 *             If the scale value is not a valid number (must be positive,
	 *             representing a number, not infinity). In addition to that the
	 *             two shaders must be instance values (not null).
	 * @throws UnsupportedOperationException
	 *             If the scale factor is equal to zero (with respect to
	 *             epsilon)
	 */
	public static Shader createCheckerBoard(final Shader a, final Shader b,
			final float scale) {
		// Implement this method
		if (a == null)
			throw new IllegalArgumentException(
					"two shaders must be instance values");
		if (b == null)
			throw new IllegalArgumentException(
					"two shaders must be instance values");
		if (scale == Constants.EPS)								// notwendig?
			throw new UnsupportedOperationException(
					"scale factor is equal to zero (EPs)");
		if (scale <= 0)											// = 0 auch abfangen?
			throw new IllegalArgumentException(
					"The scale value is not a valid number");
		if (Float.isNaN(scale))
			throw new IllegalArgumentException(
					"The scale value is not a valid number");
		if (Float.isInfinite(scale))
			throw new IllegalArgumentException(
					"The scale value is infinite");
		if (Constants.isZero(scale))
			throw new UnsupportedOperationException(
					"scale factor is equal to zero");

		return new CheckerBoard(a, b, scale);
	}

	/**
	 * Generates a Phong (http://en.wikipedia.org/wiki/Phong_shading) shader.
	 * 
	 * @param inner
	 *            The base shader of this Phong shader
	 * @param ambient
	 *            Color of the ambient light emitted to the scene
	 * @param diffuse
	 *            The ratio of reflection of the diffuse term of incoming light
	 * @param specular
	 *            The ratio of reflection of the specular term of incoming light
	 * @param shininess
	 *            A shininess constant defining this material. The larger the
	 *            value the more "mirror-like" is the structure
	 * @throws IllegalArgumentException
	 *             If the diffuse, specular or shininess parts are not valid
	 *             numbers (must be positive, representing a number, not
	 *             infinity). In addition to that the shader and the ambient
	 *             color must be instance values (not null).
	 */
	
	public static Shader createPhong(final Shader inner, final Color ambient,
			final float diffuse, final float specular, final float shininess) {
		// Implement this method
		if (Float.isNaN(diffuse) | Float.isNaN(specular)
				| Float.isNaN(shininess) | (diffuse < 0)
				| (specular < 0) | (shininess < 0)
				| (inner == null) | (ambient == null)
				| Float.isInfinite(diffuse) | Float.isInfinite(specular) | Float.isInfinite(shininess))
			throw new IllegalArgumentException(
					"no valid numbers / shader and/or Color == null");

		return new Phong(inner, ambient, diffuse, specular, shininess);
	}
}
