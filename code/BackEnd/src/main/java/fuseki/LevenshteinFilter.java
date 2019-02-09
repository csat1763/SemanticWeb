package fuseki;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;

public class LevenshteinFilter extends FunctionBase2 {
	@Override
	public NodeValue exec(NodeValue value1, NodeValue value2) {
		String a = value1.asString();
		String b = value2.asString();
		int ld = LevenshteinDistance.getDefaultInstance().apply(a, b);
		return NodeValue.makeInteger(ld);

	}

}