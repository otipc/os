package co.otipc.plain;

import co.otipc.job.Executor;
import co.otipc.job.Job;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chaoguo.Cui on 16/7/12.
 */
public class VistorItem {

  private static final Logger LOGGER = LoggerFactory.getLogger(VistorItem.class);

  public static void doItem(Job job, SelectItem item) {

    if (item instanceof AllColumns) {

      job.getDims().add("*");

    } else if (item instanceof AllTableColumns) {

      AllTableColumns columns = (AllTableColumns) item;


    } else if (item instanceof SelectExpressionItem) {

      Expression expression = ((SelectExpressionItem) item).getExpression();

      if (expression instanceof Column) {

        job.getDims().add(((Column) expression).getColumnName().toString());

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
