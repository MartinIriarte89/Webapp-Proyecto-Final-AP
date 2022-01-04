package modelo.objetoNulo;

import modelo.Promocion;

public class PromocionNula extends Promocion {

	public PromocionNula() {
		super("", null, "", "", 0, "");

	}

	public static Promocion construir() {
		return new PromocionNula();
	}

	public boolean esNulo() {
		return true;
	}
}
