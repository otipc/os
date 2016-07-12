package co.otipc.plain;

import co.otipc.job.Job;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chaoguo.Cui on 16/7/12.
 */
public class VistorItem {

  public static void doItem(Job job, SelectItem item) {

    if (item instanceof AllColumns) {
      job.getDims().add("*");

    } else if (item instanceof AllTableColumns) {
      AllTableColumns columns = (AllTableColumns) item;
    } else if (item instanceof SelectExpressionItem) {
      Expression expression = ((SelectExpressionItem) item).getExpression();
      if (expression instanceof Column) {
        Column column = (Column) expression;
        job.getDims().add(column.getColumnName());
      } else if (expression instanceof Function) {

        //// TODO: 16/7/12
        Function fun = (Function) expression;

        ExpressionList list = fun.getParameters();
        for (int i = 0; i < list.getExpressions().size(); i++) {

        }
        
      }
    }



  }


}
