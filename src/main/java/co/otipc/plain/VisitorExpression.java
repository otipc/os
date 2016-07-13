package co.otipc.plain;

import co.otipc.job.Condition;
import co.otipc.job.Conditions;
import co.otipc.job.Job;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Created by Chaoguo.Cui on 16/7/12.
 */
public class VisitorExpression {

  private static final Logger LOGGER = LoggerFactory.getLogger(VisitorExpression.class);


  public static void doExpr(Conditions conditions, Expression expression) {

    if (expression instanceof AndExpression) {

      conditions.setType("AND");
      AndExpression and = (AndExpression) expression;
      Expression left = and.getLeftExpression();
      Expression right = and.getRightExpression();

      doExpr(conditions, right);
      doExpr(conditions, left);

    } else if (expression instanceof OrExpression) {

      conditions.setType("OR");
      OrExpression and = (OrExpression) expression;
      Expression left = and.getLeftExpression();
      Expression right = and.getRightExpression();

      doExpr(conditions, right);
      doExpr(conditions, left);

    } else {

      if (expression instanceof EqualsTo) {

        Condition condition = new Condition();
        condition.setType("==");
        conditions.getItems().add(condition);

        EqualsTo eq = (EqualsTo) expression;
        Expression left = eq.getLeftExpression();
        Expression right = eq.getRightExpression();
        doExpr(conditions, right);
        doExpr(conditions, left);

      } else if (expression instanceof MinorThan) {

        Condition condition = new Condition();
        condition.setType("<");
        conditions.getItems().add(condition);

        MinorThan minorThan = (MinorThan) expression;
        Expression left = minorThan.getLeftExpression();
        Expression right = minorThan.getRightExpression();

      } else if (expression instanceof GreaterThan) {

        Condition condition = new Condition();
        condition.setType(">");
        conditions.getItems().add(condition);

        GreaterThan minorThan = (GreaterThan) expression;
        Expression left = minorThan.getLeftExpression();
        Expression right = minorThan.getRightExpression();

      } else if (expression instanceof SubSelect) {

        SubSelect subSelect = (SubSelect) expression;
        PlainSelect plain = (PlainSelect) subSelect.getSelectBody();
        Job innerJob = new Job();
        VisitorSelect.doSelect(innerJob, plain);
        List<String> result = innerJob.doExec();
        conditions.getItems().getLast().setValue(result);

      } else if (expression instanceof InExpression) {

        Condition condition = new Condition();
        condition.setType("in");
        conditions.getItems().add(condition);
        InExpression in = (InExpression) expression;
        Expression left = in.getLeftExpression();
        doExpr(conditions, left);
        ItemsList items = in.getRightItemsList();
        if (items instanceof SubSelect) {

          SubSelect subSelect = (SubSelect) items;
          PlainSelect plain = (PlainSelect) subSelect.getSelectBody();
          Job innerJob = new Job();
          VisitorSelect.doSelect(innerJob, plain);
          List<String> result = innerJob.doExec();
          conditions.getItems().getLast().setValue(result);

        } else if (items instanceof ExpressionList) {
          ExpressionList list = (ExpressionList) items;
          List<Expression> exprs = list.getExpressions();
          for (Expression e : exprs) {
            doExpr(conditions, e);
          }

        } else if (items instanceof MultiExpressionList) {

          MultiExpressionList list = (MultiExpressionList) items;

          List<ExpressionList> exprlist = list.getExprList();
          for (ExpressionList l : exprlist) {
            List<Expression> exprs = l.getExpressions();
            for (Expression e : exprs) {
              doExpr(conditions, e);
            }
          }

        } else {

          conditions.getItems().getLast().setValue(items);

        }

      } else if (expression instanceof Column) {

        conditions.getItems().getLast().setColumn(((Column) expression).getColumnName().toString());

      } else if (expression instanceof Function) {

        Function fun = (Function) expression;

        ExpressionList list = fun.getParameters();
        for (int i = 0; i < list.getExpressions().size(); i++) {

        }
      } else if (expression instanceof StringValue) {

        StringValue value = (StringValue) expression;
        System.out.println(value.getValue());
        conditions.getItems().getLast().setValue(value.getValue());

      } else if (expression instanceof LongValue) {

        LongValue value = (LongValue) expression;
        System.out.println(value.getValue());
        conditions.getItems().getLast().setValue(value.toString());

      } else if (expression instanceof DoubleValue) {

        DoubleValue value = (DoubleValue) expression;
        System.out.println(value.getValue());
        conditions.getItems().getLast().setValue(value.getValue());

      } else if (expression instanceof DateValue) {
        DateValue value = (DateValue) expression;
        System.out.println(value.getValue());
      } else {

        String str = expression.toString();
        if (str.startsWith("'") && str.endsWith("'")) {
          str = str.substring(1, str.length() - 1);
        }
        conditions.getItems().getLast().setValue(str);

      }
    }

  }
}

