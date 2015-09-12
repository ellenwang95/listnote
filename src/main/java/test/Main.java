package test;
import static spark.Spark.*;

public class Main {
	public static void main(String[] args) {
		get("/*", (request, response) -> {
			System.out.println(request.pathInfo());
			System.out.println(request.url());
			return "SOMETHING!";
		});
	}
}
