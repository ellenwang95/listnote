package test;

import test.Mock;

public class Mock {
	public Mock[] mock = new Mock[1];
	public String foo = "bar";
	public Mock() {}
	public Mock(Mock mock) {
		this.mock[0] = mock;
	}
	public Mock[] getMock() {
		return this.mock;
	}
	public String getfoo() {
		return this.foo;
	}
}
