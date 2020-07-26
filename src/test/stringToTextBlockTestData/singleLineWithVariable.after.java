public class X {
	void f(boolean isMale) {
		String name = "world";
		String title = String.format("""
                hello %s
                """, name);
		System.out.println("title = " + title);
	}
}