package gov.nasa.jpf.constraints.expressions;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ExpressionVisitor;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;

import java.io.IOException;
import java.util.Collection;

/**
 * Expression that checks whether the given object-valued expression evaluates to null.
 */
public class IsNullExpression extends AbstractBoolExpression {

  private final Expression<?> expr;

  public IsNullExpression(Expression<?> expr) {
    this.expr = expr;
  }

  @Override
  public Boolean evaluate(Valuation values) {
    Object value = expr.evaluate(values);
    System.out.println("**********************************************************");
    System.out.println("Evaluating IsNullExpression: value=" + value);
    System.out.println("**********************************************************");
    return value == null || (value instanceof Integer && (Integer) value == 0);
  }

  @Override
  public void collectFreeVariables(Collection<? super Variable<?>> variables) {
    expr.collectFreeVariables(variables);
  }

  @Override
  public <R, D> R accept(ExpressionVisitor<R, D> visitor, D data) {
    return visitor.visit(this, data);
  }

  @Override
  public boolean isHighLevel() {
    return true;
  }

  @Override
  public Expression<?>[] getChildren() {
    return new Expression<?>[] { expr };
  }

  @Override
  public Expression<?> duplicate(Expression<?>[] newChildren) {
    assert newChildren.length == 1;
    if (identical(newChildren, expr)) {
      return this;
    }
    return new IsNullExpression(newChildren[0]);
  }

  @Override
  public void print(Appendable a, int flags) throws IOException {
    a.append('(');
    expr.print(a, flags);
    a.append(" is null)");
  }

  @Override
  public void printMalformedExpression(Appendable a, int flags) throws IOException {
    a.append('(');
    if (expr == null) {
      a.append("null");
    } else {
      expr.printMalformedExpression(a, flags);
    }
    a.append(" is null)");
  }
}


