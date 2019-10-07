package jp.posl.jprophet.FL;

import java.io.Serializable;

public class FullyQualifiedName implements Serializable {
	private static final long serialVersionUID = 1L;
	
	final public String value;

	public FullyQualifiedName(String value) {
		this.value = value;
	}
}