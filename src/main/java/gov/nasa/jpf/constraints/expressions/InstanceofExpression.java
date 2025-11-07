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
  private final Type<?> checkType;

  public InstanceofExpression(Expression<?> expr, Type<?> checkType) {
    this.expr = expr;
    this.checkType = checkType;
  }

  @Override
  public Boolean evaluate(Valuation values) {
    Object val = expr.evaluate(values);
    if (val == null) {
      return false;
    }
    Class<?> typeClass = checkType.getCanonicalClass();

    // 4. 使用 isInstance 进行运行时检查
    return typeClass.isInstance(val);
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
    return new InstanceofExpression(newChildren[0], this.checkType);
  }

  @Override
  public void print(Appendable a, int flags) throws IOException {
    a.append('(');
    expr.print(a, flags);
    a.append(" instanceof ");
    a.append(checkType.getName());
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
    a.append(checkType != null ? checkType.getName() : "null");
    a.append(')');
  }

}
