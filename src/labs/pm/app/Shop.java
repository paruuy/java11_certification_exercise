package labs.pm.app;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		ProductManager pm = ProductManager.getInstance();
		AtomicInteger clientCount = new AtomicInteger();

		Callable<String> client = () -> {
			String clientId = "Client " + clientCount.incrementAndGet();
			String threadName = Thread.currentThread().getName();
			int productId = ThreadLocalRandom.current().nextInt(13) + 101;
			String languageTag = ProductManager.getSupportedLocales().stream()
					.skip(ThreadLocalRandom.current().nextInt(4)).findFirst().get();

			StringBuilder log = new StringBuilder();
			log.append(clientId + " " + threadName + "\n-\tstart of log\t-\n");

			log.append(pm.getDiscount(languageTag).entrySet().stream()
					.map(entry -> entry.getKey() + "\t" + entry.getValue()).collect(Collectors.joining("\n")));

			Product product = pm.reviewProduct(productId, Raiting.FOUR_STAR, "Yet another review");
			log.append((product != null) ? "\nProduct " + productId + " reviewed\n"
					: "\nProduct " + productId + " not reviewed\n");
			pm.printProductReport(productId, languageTag, clientId);
			log.append(clientId + " generated report for " + productId + " product");

			log.append("\n-\tend of log\t-\n");

			return log.toString();
		};

		List<Callable<String>> clients = Stream.generate(() -> client).limit(5).collect(Collectors.toList());

		ExecutorService executorsService = Executors.newFixedThreadPool(3);
		try {
			List<Future<String>> results = executorsService.invokeAll(clients);
			executorsService.shutdown();
			results.stream().forEach(result -> {
				try {
					System.out.println(result.get());
				} catch (InterruptedException | ExecutionException ex) {
					Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, "Error retrieving client log ", ex);
				}
			});
		} catch (InterruptedException ex) {
			Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, "Error invoking clients ", ex);
		}
		System.out.println("Init");

		System.out.println("Done");
//		pm.createProduct(164, "Kombucha", BigDecimal.valueOf(2.99), Raiting.NOT_RATED);
//		pm.reviewProduct(164, Raiting.THREE_STAR, "Looks like tea but it is?");
//		pm.reviewProduct(164, Raiting.FOUR_STAR, "Fine tea");
//		pm.reviewProduct(164, Raiting.FOUR_STAR, "This is not tea");
//		pm.reviewProduct(164, Raiting.FIVE_STAR, "Excellent!");
		// pm.printProductReport(164);

	}

}
