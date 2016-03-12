package br.udesc.dcc.bdes.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class Fn {
	
	public static <A, B> List<A> transform(List<B> bList, Function<B, A> transformer) {
		List<A> aList = new LinkedList<>();
		bList.forEach( b -> aList.add(transformer.apply(b)));
		return aList;
	}
}
