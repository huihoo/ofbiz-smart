package org.huihoo.ofbiz.smart.entity;

import java.util.List;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;

public class Expr {
  private final static String TAG = Expr.class.getName();

  private final static Expr ME = new Expr();

  private final static StringBuilder EXPR_SB = new StringBuilder();

  private Expr() {

  }
  
  public static Expr create() {
    if (EXPR_SB.length() > 0) {
      EXPR_SB.delete(0, EXPR_SB.length());
    }
    return ME;
  }

  public Expr eq(String name, Object value) {
    if (CommUtil.isEmpty(value)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_EQ);
    EXPR_SB.append(",");
    EXPR_SB.append(value);
    EXPR_SB.append("}");
    return ME;
  }

  public Expr neq(String name, Object value) {
    if (CommUtil.isEmpty(value)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_NE);
    EXPR_SB.append(",");
    EXPR_SB.append(value);
    EXPR_SB.append("}");
    return ME;
  }

  public Expr ge(String name, Object value) {
    if (CommUtil.isEmpty(value)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_GE);
    EXPR_SB.append(",");
    EXPR_SB.append(value);
    EXPR_SB.append("}");
    return ME;
  }

  public Expr gt(String name, Object value) {
    if (CommUtil.isEmpty(value)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_GT);
    EXPR_SB.append(",");
    EXPR_SB.append(value);
    EXPR_SB.append("}");
    return ME;
  }

  public Expr le(String name, Object value) {
    if (CommUtil.isEmpty(value)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_LE);
    EXPR_SB.append(",");
    EXPR_SB.append(value);
    EXPR_SB.append("}");
    return ME;
  }

  public Expr lt(String name, Object value) {
    if (CommUtil.isEmpty(value)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_LT);
    EXPR_SB.append(",");
    EXPR_SB.append(value);
    EXPR_SB.append("}");
    return ME;
  }

  public Expr between(String name, Object from, Object to) {
    if (CommUtil.isEmpty(from) || CommUtil.isEmpty(to)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_BETWEEN);
    EXPR_SB.append(",");
    EXPR_SB.append(from);
    EXPR_SB.append("#");
    EXPR_SB.append(to);
    EXPR_SB.append("}");
    return ME;
  }

  public Expr in(String name, List<Object> vals) {
    if (CommUtil.isEmpty(vals)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_IN);
    EXPR_SB.append(",");
    int size = vals.size();
    for (int i = 0; i < size; i++) {
      EXPR_SB.append(vals.get(i));
      if (i < size - 1) {
        EXPR_SB.append("#");
      }
    }
    EXPR_SB.append("}");
    return ME;
  }

  public Expr notIn(String name, List<Object> vals) {
    if (CommUtil.isEmpty(vals)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_NIN);
    EXPR_SB.append(",");
    int size = vals.size();
    for (int i = 0; i < size; i++) {
      EXPR_SB.append(vals.get(i));
      if (i < size - 1) {
        EXPR_SB.append("#");
      }
    }
    EXPR_SB.append("}");
    return ME;
  }

  public Expr like(String name, Object value) {
    if (CommUtil.isEmpty(value)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_LIKE);
    EXPR_SB.append(",");
    EXPR_SB.append(value);
    EXPR_SB.append("}");
    return ME;
  }

  public Expr llike(String name, Object value) {
    if (CommUtil.isEmpty(value)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_LLIKE);
    EXPR_SB.append(",");
    EXPR_SB.append(value);
    EXPR_SB.append("}");
    return ME;
  }

  public Expr rlike(String name, Object value) {
    if (CommUtil.isEmpty(value)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_RLIKE);
    EXPR_SB.append(",");
    EXPR_SB.append(value);
    EXPR_SB.append("}");
    return ME;
  }
  
  public Expr isNull(String name) {
    if (CommUtil.isEmpty(name)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_IS_NULL);
    EXPR_SB.append(",");
    EXPR_SB.append("placeHolder");
    EXPR_SB.append("}");
    return ME;
  }
  
  public Expr isNotNull(String name) {
    if (CommUtil.isEmpty(name)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(name);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_IS_NOT_NULL);
    EXPR_SB.append(",");
    EXPR_SB.append("placeHolder");
    EXPR_SB.append("}");
    return ME;
  }

  public Expr or(String leftExpr, String rightExpr) {
    if (CommUtil.isEmpty(leftExpr) || CommUtil.isEmpty(rightExpr)) {
      return ME;
    }
    EXPR_SB.append("{");
    EXPR_SB.append(leftExpr);
    EXPR_SB.append(",");
    EXPR_SB.append(C.EXPR_OR);
    EXPR_SB.append(",");
    EXPR_SB.append(rightExpr);
    EXPR_SB.append("}");
    return ME;
  }

  public String build() {
    Log.d("Expr:" + EXPR_SB, TAG);
    return EXPR_SB.toString();
  }
}
