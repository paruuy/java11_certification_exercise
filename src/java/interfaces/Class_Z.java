package java.interfaces;

public class Class_Z extends Class_W implements Interface_X, Interface_Y{

	@Override
	public void a() {
		System.out.println("override method a() of interface X");
	}

	//Se tenho metodos iguais default com diferene codigo nas interfaces
	// Preciso de escolher um dos dois para implementar
	@Override
	public void b() {
		Interface_X.super.b();
	}

}
