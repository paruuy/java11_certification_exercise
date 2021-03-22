package java.interfaces;

public interface Interface_X {

	void a();
	public default void b() {
		System.out.println("Method default b() from interface X");
	}
	private void c() {
		System.out.println("Internal Private method c() from interface X");
	}
	
	public static void d() {
		System.out.println("Static method d() from inteface X");
	}
}
