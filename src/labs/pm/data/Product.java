package labs.pm.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * {@code Product} class represent properties and behaviours of product objects
 * in the Product Management System. <br>
 * Each product has a id, name and price <br>
 * Each product can be a discount
 * 
 * @version 1.0
 * @author pablorodriguez
 *
 */

// INMUTABLE CLASS (doesn have setters)
public abstract class Product implements Rateable<Product>, Serializable {

	private int id;
	private String name;
	private BigDecimal price;
	private Raiting raiting;


	Product(int id, String name, BigDecimal price, Raiting raiting) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.raiting = raiting;
	}

	// This constructor calls the constructor above
	Product(int id, String name, BigDecimal price) {
		this(id, name, price, Raiting.NOT_RATED);
	}

	/**
	 * Discount Rate constant {@link DISCOUNT_RATE discount rate}
	 * 
	 */
	public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public Raiting getRaiting() {
		return raiting;
	}

	/**
	 * 
	 * @return a {@link java.math.BigDecimal BigDecimal} value of the discount
	 */
	public BigDecimal getDiscount() {
		return price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
	}

	//public abstract Product applyRaiting(Raiting newRaiting);
	// Changes the values of raiting, creating a new product object
	//public Product applyRaiting(Raiting newRaiting) {
	//	return new Product(id, name, price, newRaiting);
	//}

	public LocalDate getBestBefore() {
		//Beacuse doent have the bestBefor attribute (exist in food class)
		//in product class is created o method with the now date
		return LocalDate.now();
	}
	
	@Override
	public String toString() {
		return "id=" + id + ", name=" + name + ", price=" + getDiscount() + ", raiting=" + raiting.getStars() + " " + getBestBefore();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) { return true; }
		//if (obj != null && getClass() == obj.getClass()) {
		if (obj instanceof Product) {

			final Product other = (Product) obj;
			
			return this.id==other.id;
		}

		return false;
	}

}
