package gov.nasa.jpf.constraints.expressions;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ExpressionVisitor;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.types.Type;

import java.io.IOException;
import java.util.Collection;

/**
 * Expression for reference equality comparison (== or !=).
 * Used for symbolic execution of if_acmpeq and if_acmpne bytecode instructions.
 */
public class ReferenceComparisonExpression extends AbstractBoolExpression {

  private final Expression<?> left;
  private final Expression<?> right;
  private final boolean isEqual; // true for ==, false for !=

  public ReferenceComparisonExpression(Expression<?> left, Expression<?> right, boolean isEqual) {
    this.left = left;
    this.right = right;
    this.isEqual = isEqual;
  }

  @Override
  public Boolean evaluate(Valuation values) {
    Object leftVal = left.evaluate(values);
    Object rightVal = right.evaluate(values);
    
    // Handle integer references (object references are represented as integers in JPF)
    boolean equal;
    if (leftVal instanceof Integer && rightVal instanceof Integer) {
      equal = leftVal.equals(rightVal);
    } else {
      equal = (leftVal == rightVal);
    }
    
    return isEqual ? equal : !equal;
  }

  @Override
  public void collectFreeVariables(Collection<? super Variable<?>> variables) {
    left.collectFreeVariables(variables);
    right.collectFreeVariables(variables);
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
  public boolean isHighLevel() {
    return true;
  }

  @Override
  public Expression<?>[] getChildren() {
    return new Expression<?>[] { left, right };
  }

  @Override
  public Expression<?> duplicate(Expression<?>[] newChildren) {
    assert newChildren.length == 2;
    if (identical(newChildren, left, right)) {
      return this;
    }
    return new ReferenceComparisonExpression(newChildren[0], newChildren[1], isEqual);
  }

  @Override
  public void print(Appendable a, int flags) throws IOException {
    a.append('(');
    left.print(a, flags);
    a.append(isEqual ? " == " : " != ");
    right.print(a, flags);
    a.append(')');
  }

  @Override
  public void printMalformedExpression(Appendable a, int flags) throws IOException {
    a.append('(');
    if (left == null) {
      a.append("null");
    } else {
      left.printMalformedExpression(a, flags);
    }
    a.append(isEqual ? " == " : " != ");
    if (right == null) {
      a.append("null");
    } else {
      right.printMalformedExpression(a, flags);
    }
    a.append(')');
  }

  public Expression<?> getLeft() {
    return left;
  }

  public Expression<?> getRight() {
    return right;
  }

  public boolean isEqual() {
    return isEqual;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ReferenceComparisonExpression other = (ReferenceComparisonExpression) obj;
    return isEqual == other.isEqual &&
           left.equals(other.left) &&
           right.equals(other.right);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + left.hashCode();
    hash = 31 * hash + right.hashCode();
    hash = 31 * hash + (isEqual ? 1 : 0);
    return hash;
  }
}
