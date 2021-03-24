package labs.pm.data;

import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//Factory Pattern Class with polymorphism
// Removed the public modifier of constructors (drink, product, food) to use the factory
public class ProductManager {

	private static final Logger logger = Logger.getLogger(ProductManager.class.getName());

	private Map<Product, List<Review>> products = new HashMap<>();

	// Add final to ensure they are not accidentally reassigned (inmutable)
	private static final Map<String, ResourceFormatter> formatters = Map.of("en-GB", new ResourceFormatter(Locale.UK),
			"en-US", new ResourceFormatter(Locale.US), "fr-FR", new ResourceFormatter(Locale.FRANCE), "ru-RU",
			new ResourceFormatter(new Locale("ru", "RU")), "zh-CN", new ResourceFormatter(Locale.CHINA));
	private final ResourceBundle config = ResourceBundle.getBundle("labs.pm.data.config");
	private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
	private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
	private final Path reportFolder = Path.of(config.getString("reports.folder"));
	private final Path dataFolder = Path.of(config.getString("data.folder"));
	private final Path tempFolder = Path.of(config.getString("temp.folder"));

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock writeLock = lock.writeLock();
	private final Lock readLock = lock.readLock();

	// Singleton
	private static final ProductManager pm = new ProductManager();

	// For singleton class
	private ProductManager() {
		loadAllData();
	}

	public static ProductManager getInstance() {
		return pm;
	}

	private static class ResourceFormatter {

		private Locale locale;
		private ResourceBundle resources;
		private DateTimeFormatter dateFormat;
		private NumberFormat moneyFormat;

		private ResourceFormatter(Locale locale) {
			this.locale = locale;
			resources = ResourceBundle.getBundle("labs.pm.data.resources", locale);
			dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
			moneyFormat = NumberFormat.getCurrencyInstance(locale);
		}

		private String formatProduct(Product product) {
			return MessageFormat.format(resources.getString("product"), product.getName(),
					moneyFormat.format(product.getPrice()), product.getRaiting().getStars(),
					dateFormat.format(product.getBestBefore()));
		}

		private String formatReview(Review review) {
			return MessageFormat.format(resources.getString("review"), review.getRaiting().getStars(),
					review.getComments());
		}

		private String getText(String key) {
			return resources.getString(key);
		}

	}

	public void printProducts(Predicate<Product> filter, Comparator<Product> sorter, String languageTag) {

		try {
			readLock.lock();
			StringBuilder txt = new StringBuilder();
			ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));

			products.keySet().stream().sorted(sorter).filter(filter)
					.forEach(p -> txt.append(formatter.formatProduct(p) + '\n'));
			System.out.println(txt);
		} finally {
			readLock.unlock();
		}
	}

//	public void changeLocale(String languageTag) {
//		
//		formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
//	}

	public static Set<String> getSupportedLocales() {
		return formatters.keySet();
	}

	public Product createProduct(int id, String name, BigDecimal price, Raiting raiting, LocalDate bestBefore) {
		Product product = null;
		try {
			writeLock.lock();
			product = new Food(id, name, price, raiting, bestBefore);
			products.putIfAbsent(product, new ArrayList<>());
		} catch (Exception ex) {
			logger.log(Level.INFO, "Error adding product " + ex.getMessage());
			return null;
		} finally {
			writeLock.unlock();
		}

		return product;
	}

	public Product createProduct(int id, String name, BigDecimal price, Raiting raiting) {
		Product product = null;
		try {
			writeLock.lock();
			product = new Drink(id, name, price, raiting);
			products.putIfAbsent(product, new ArrayList<>());
		} catch (Exception ex) {
			logger.log(Level.INFO, "Error adding product " + ex.getMessage());
			return null;
		} finally {
			writeLock.unlock();
		}
		return product;
	}

	private Product reviewProduct(Product product, Raiting raiting, String comments) {

		List<Review> reviews = products.get(product);
		products.remove(product, reviews);
		reviews.add(new Review(raiting, comments));

		product = product.applyRaiting(Rateable.convert(
				(int) Math.round(reviews.stream().mapToInt(r -> r.getRaiting().ordinal()).average().orElse(0))));

		products.put(product, reviews);

		return product;
	}

	public Product reviewProduct(int id, Raiting raiting, String comments) {
		try {
			writeLock.lock();
			return reviewProduct(findProduct(id), raiting, comments);
		} catch (ProductManagerExeption e) {
			logger.log(Level.INFO, e.getMessage());
		} finally {
			writeLock.unlock();
		}
		return null;
	}

	public void printProductReport(int id, String languageTag, String client) {
		try {
			readLock.lock();
			printProductReport(findProduct(id), languageTag, client);
		} catch (ProductManagerExeption e) {
			logger.log(Level.INFO, e.getMessage());
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Error printing product report " + ex.getMessage());
		} finally {
			readLock.unlock();
		}
	}

	private void printProductReport(Product product, String languageTag, String client) throws IOException {

		ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));

		Path productFile = reportFolder
				.resolve(MessageFormat.format(config.getString("report.file"), product.getId(), client));
		List<Review> reviews = products.get(product);

		try (PrintWriter out = new PrintWriter(
				new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE), "UTF-8"))) {

			out.append(formatter.formatProduct(product) + System.lineSeparator());
			Collections.sort(reviews);

			if (reviews.isEmpty()) {
				out.append(formatter.getText("no-review") + System.lineSeparator());
			} else {

				out.append(reviews.stream().map(r -> formatter.formatReview(r) + System.lineSeparator())
						.collect(Collectors.joining()));
			}
		}
	}

	public Product findProduct(int id) throws ProductManagerExeption {
		try {
			readLock.lock();
			return products.keySet().stream().filter(p -> p.getId() == id).findFirst()
					.orElseThrow(() -> new ProductManagerExeption("Product with " + id + " not found")); // .get
																											// //.orElseGet(()
		} finally {
			readLock.unlock();
		}
	}

	public Map<String, String> getDiscount(String languageTag) {

		try {
			readLock.lock();
			ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));

			return products.keySet().stream()
					.collect(Collectors.groupingBy(product -> product.getRaiting().getStars(),
							Collectors.collectingAndThen(
									Collectors.summingDouble(product -> product.getDiscount().doubleValue()),
									discount -> formatter.moneyFormat.format(discount))));
		} finally {

			readLock.unlock();
		}
	}

	private List<Review> loadReviews(Product product) {
		List<Review> reviews = null;
		Path file = dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
		if (Files.notExists(file)) {
			reviews = new ArrayList<>();
		} else {
			try {

				reviews = Files.lines(file, Charset.forName("UTF-8")).map(text -> parseReview(text))
						.filter(review -> review != null).collect(Collectors.toList());
			} catch (IOException ex) {
				logger.log(Level.WARNING, "Error loading reviews " + ex.getMessage());
			}
		}
		return reviews;

	}

	private Review parseReview(String text) {
		Review review = null;
		try {
			Object[] values = reviewFormat.parse(text);
			review = new Review(Rateable.convert(Integer.parseInt((String) values[0])), (String) values[1]);
		} catch (ParseException | NumberFormatException e) {
			logger.log(Level.WARNING, "Error parsing the review: " + text);
		}
		return review;
	}

	private Product loadProduct(Path file) {
		Product product = null;
		try {
			product = parseProduct(
					Files.lines(dataFolder.resolve(file), Charset.forName("UTF-8")).findFirst().orElseThrow());
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Error loading product " + ex.getMessage());
		}
		return product;
	}

	private void loadAllData() {
		try {
			products = Files.list(dataFolder).filter(file -> file.getFileName().toString().startsWith("product"))
					.map(file -> loadProduct(file)).filter(product -> product != null)
					.collect(Collectors.toMap(product -> product, product -> loadReviews(product)));
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Error loading data " + ex.getMessage());
		}
	}

	private void dumpData() {
		try {
			if (Files.notExists(tempFolder)) {
				Files.createDirectories(tempFolder);
			}
			Path file = tempFolder.resolve(MessageFormat.format(config.getString("temp.file"), Instant.now()));
			try (ObjectOutputStream out = new ObjectOutputStream(
					Files.newOutputStream(file, StandardOpenOption.CREATE))) {
				out.writeObject(products);
//				products = new HashMap<>();
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Error dumping data " + ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void restoreData() {
		try {
			Path tempFile = Files.list(tempFolder).filter(path -> path.getFileName().toString().endsWith("tmp"))
					.findFirst().orElseThrow();
			try (ObjectInputStream in = new ObjectInputStream(
					Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) {
				products = (HashMap) in.readObject();
			}
		} catch (Exception ex) { // IoException, ClassNotFoundException, NoSuchElementEx..
			logger.log(Level.SEVERE, "Error restoring data " + ex.getMessage());
		}
	}

	private Product parseProduct(String text) {

		Product product = null;
		try {
			Object[] values = productFormat.parse(text);
			int id = Integer.parseInt((String) values[1]);
			String name = (String) values[2];
			BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
			Raiting raiting = Rateable.convert(Integer.parseInt((String) values[4]));

			switch ((String) values[0]) {
			case "D":
				product = new Drink(id, name, price, raiting);
				break;

			case "F":
				LocalDate bestBefore = LocalDate.parse((String) values[5]);
				product = new Food(id, name, price, raiting, bestBefore);
				break;
			}

		} catch (ParseException | NumberFormatException | DateTimeParseException e) {
			logger.log(Level.WARNING, "Error parsing the product: " + text + " " + e.getMessage());
		}

		return product;
	}

}
