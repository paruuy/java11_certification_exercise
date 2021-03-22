package labs.pm.app;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;

import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.Raiting;

/**
 * {@code Shop} class represent an application that manages products
 * 
 * @version 1.0
 * @author pablorodriguez
 *
 */
public class Shop {

	public static void main(String[] args) {

		ProductManager pm = new ProductManager("en-GB");

		// pm.printProductReport(101);
		// pm.printProductReport(103);

		pm.createProduct(164, "Kombucha", BigDecimal.valueOf(2.99), Raiting.NOT_RATED);
		pm.reviewProduct(164, Raiting.THREE_STAR, "Looks like tea but it is?");
		pm.reviewProduct(164, Raiting.FOUR_STAR, "Fine tea");
		pm.reviewProduct(164, Raiting.FOUR_STAR, "This is not tea");
		pm.reviewProduct(164, Raiting.FIVE_STAR, "Excellent!");
		// pm.printProductReport(164);

		pm.printProductReport(164);
		pm.printProductReport(101);
		 pm.printProducts(p -> p.getPrice().floatValue() < 2, (p1, p2) -> p2.getRaiting().ordinal() - p1.getRaiting().ordinal());
		 pm.getDiscount().forEach((raiting, discount) -> System.out.println(raiting + '\t' + discount));


	}

}
