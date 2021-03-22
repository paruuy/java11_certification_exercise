package java.interfaces;

public interface Interface_Y {

	void e();

	public default void b() {
		System.out.println("Method default b() from interface Y");
	}

	private void c() {
		System.out.println("Internal Private method c() from interface Y");
	}

	public static void d() {
		System.out.println("Static method d() from inteface Y");
	}

}
