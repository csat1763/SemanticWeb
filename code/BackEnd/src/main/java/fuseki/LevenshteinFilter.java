package fuseki;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;

public class LevenshteinFilter extends FunctionBase2 {
	@Override
	public NodeValue exec(NodeValue value1, NodeValue value2) {
		return NodeValue
				.makeInteger(LevenshteinDistance.getDefaultInstance().apply(value1.asString(), value2.asString()));

	}

}