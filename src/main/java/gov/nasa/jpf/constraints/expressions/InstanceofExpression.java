package gov.nasa.jpf.constraints.expressions;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ExpressionVisitor;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.types.Type;

import java.io.IOException;
import java.util.Collection;

public class InstanceofExpression extends AbstractExpression<Boolean> {

  private final Expression<?> expr;
  // private final Type<?> checkType;
  private final String typeSignature;

  public InstanceofExpression(Expression<?> expr, String typeSignature) {
    this.expr = expr;
    this.typeSignature = typeSignature;
  }

  @Override
  public Boolean evaluate(Valuation values) {

    System.out.println("[Debug] Evaluating instanceof expression");

    Object val = expr.evaluate(values);
    if (val == null) {
      return false;
    }
    try {
      Class<?> typeClass = Class.forName(typeSignature);
      return typeClass.isInstance(val);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Type not found: " + typeSignature, e);
    }
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
  public Type<Boolean> getType() {
    return BuiltinTypes.BOOL;
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
    return new InstanceofExpression(newChildren[0], typeSignature);
  }

  @Override
  public void print(Appendable a, int flags) throws IOException {
    a.append('(');
    expr.print(a, flags);
    a.append(" instanceof ");
    a.append(typeSignature);
    a.append(')');
  }

  @Override
  public void printMalformedExpression(Appendable a, int flags) throws IOException {
    a.append('(');
    if (expr == null) {
      a.append("null");
    } else {
      expr.printMalformedExpression(a, flags);
    }
    a.append(" instanceof ");
    a.append(typeSignature != null ? typeSignature : "null");
    a.append(')');
  }

}
