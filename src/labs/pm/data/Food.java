package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalDate;

//Final modifier prevent extend from this class
public final class Food extends Product {

	private LocalDate bestBefore;

	public LocalDate getBestBefore() {
		return bestBefore;
	}

	Food(int id, String name, BigDecimal price, Raiting raiting, LocalDate bestBefore) {
		super(id, name, price, raiting);
		this.bestBefore = bestBefore;
	}

	@Override
	public String toString() {
		// Calling befor to toString of product (plymorfism)
		return super.toString() + " bestBefore=" + bestBefore;
	}

	@Override
	public BigDecimal getDiscount() {
		return (bestBefore.isEqual(LocalDate.now()) ? super.getDiscount() : BigDecimal.ZERO);
	}

	@Override
	public Product applyRaiting(Raiting newRaiting) {
		return new Food(getId(), getName(), getPrice(), newRaiting, bestBefore);
	}
}
