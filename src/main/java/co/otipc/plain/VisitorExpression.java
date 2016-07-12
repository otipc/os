package co.otipc.plain;

import co.otipc.job.Condition;
import co.otipc.job.Job;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.List;


/**
 * Created by Chaoguo.Cui on 16/7/12.
 */
public class VisitorExpression {

  //  private static Condition condition;

  public static void doExpr(Job job, Expression expression) {

    if (expression instanceof AndExpression) {
      job.getConditions().setType("AND");
      AndExpression and = (AndExpression) expression;
      Expression left = and.getLeftExpression();
      Expression right = and.getRightExpression();

      doExpr(job, right);
      doExpr(job, left);

    } else if (expression instanceof OrExpression) {
      job.getConditions().setType("OR");
      OrExpression and = (OrExpression) expression;
      Expression left = and.getLeftExpression();
      Expression right = and.getRightExpression();

      doExpr(job, right);
      doExpr(job, left);

    } else {

      job.getConditions().setType("AND");

      if (expression instanceof EqualsTo) {

        Condition condition = new Condition();
        condition.setType("==");
        job.getConditions().getItems().add(condition);
        EqualsTo eq = (EqualsTo) expression;
        Expression left = eq.getLeftExpression();
        Expression right = eq.getRightExpression();
        doExpr(job, right);
        doExpr(job, left);

      } else if (expression instanceof SubSelect) {
        SubSelect subSelect = (SubSelect) expression;
        PlainSelect plain = (PlainSelect) subSelect.getSelectBody();
        Job innerJob = new Job();
        VisitorSelect.doSelect(innerJob, plain);
        List<String> result = innerJob.doExec();
        job.getConditions().getItems().getLast().setValue(result);

      } else if (expression instanceof InExpression) {
        Condition condition = new Condition();
        condition.setType("in");
        job.getConditions().getItems().add(condition);
        InExpression in = (InExpression) expression;
        Expression left = in.getLeftExpression();
        doExpr(job, left);
        ItemsList items = in.getRightItemsList();
        if (items instanceof SubSelect) {
          SubSelect subSelect = (SubSelect) items;
          PlainSelect plain = (PlainSelect) subSelect.getSelectBody();
          Job innerJob = new Job();
          VisitorSelect.doSelect(innerJob, plain);
          List<String> result = innerJob.doExec();
          job.getConditions().getItems().getLast().setValue(result);
        } else {
          job.getConditions().getItems().getLast().setValue(items);
        }
      } else if (expression instanceof Column) {
        Column column = (Column) expression;
        job.getConditions().getItems().getLast().setColumn(column.getColumnName().toString());

      } else if (expression instanceof Function) {
        Function fun = (Function) expression;

        ExpressionList list = fun.getParameters();
        for (int i = 0; i < list.getExpressions().size(); i++) {

        }
      } else {
        String str = expression.toString();
        if (str.startsWith("'") && str.endsWith("'")) {
          str = str.substring(1, str.length() - 1);
        }
        job.getConditions().getItems().getLast().setValue(str);
      }

    }
  }

}
