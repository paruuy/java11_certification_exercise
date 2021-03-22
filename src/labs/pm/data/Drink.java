package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalTime;

// Final modifier prevent extend from this class
public final class Drink extends Product {

	Drink(int id, String name, BigDecimal price, Raiting raiting) {
		super(id, name, price, raiting);
	}

	@Override
	public BigDecimal getDiscount() {
		LocalTime now = LocalTime.now();
		
		return (now.isAfter(LocalTime.of(17,30)) && now.isBefore(LocalTime.of(18, 30))) ? super.getDiscount() : BigDecimal.ZERO;
	}

	@Override
	public Product applyRaiting(Raiting newRaiting) {
		return new Drink(getId(), getName(), getPrice(), newRaiting);
	}

	
}
