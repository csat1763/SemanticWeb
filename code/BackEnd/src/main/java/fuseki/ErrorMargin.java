package fuseki;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

public class ErrorMargin extends FunctionBase1 {

	@Override
	public NodeValue exec(NodeValue v) {
		String a = v.asString();
		int margin = 0;
		if (a.length() <= 3)
			margin = 1;
		else if (a.length() > 3 && a.length() <= 6)
			margin = 2;
		else if (a.length() > 6 && a.length() <= 10)
			margin = 3;
		else
			margin = 4;

		return NodeValue.makeInteger(margin);
	}
}