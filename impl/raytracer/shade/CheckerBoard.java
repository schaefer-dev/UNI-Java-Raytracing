package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;

public class CheckerBoard implements Shader {

	private final Shader shade1;
	private final Shader shade2;
	private final float size;

	/**
	 * Generates a simple single-colored texture.
	 * 
	 * @param color
	 *            The color of this surface.
	 */

	public CheckerBoard(final Shader shade1, final Shader shade2,
			final float size) {
		this.shade1 = shade1;
		this.shade2 = shade2;
		this.size = size;
	}

	/**
	 * Computes the shaded color for the given hit and the trace
	 * 
	 * @param hit
	 *            The hit to use
	 * @param trace
	 *            The trace to use
	 * @return The computed color
	 */
	@Override
	public Color shade(Hit hit, Trace trace) {


		float x = hit.getUV().get(0);
		float z = hit.getUV().get(1);

		int xys = (int) Math.floor(x / this.size)
				+ (int) Math.floor(z / this.size);
		if (xys % 2 == 0)
			return shade1.shade(hit, trace);
		return shade2.shade(hit, trace);
	}

}
