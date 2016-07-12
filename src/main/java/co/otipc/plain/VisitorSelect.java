package co.otipc.plain;


import co.otipc.job.Job;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chaoguo.Cui on 16/7/12.
 */
public class VisitorSelect {

  public static void doSelect(Job job, PlainSelect plain) {


    if (null != plain.getSelectItems()) {
      for (SelectItem item : plain.getSelectItems()) {
        VistorItem.doItem(job, item);
      }
    }

    if (null != plain.getFromItem()) {
      job.setTable(plain.getFromItem().toString());

    }
    if (null != plain.getWhere()) {
      Expression expression = plain.getWhere();
      VisitorExpression.doExpr(job, expression);
    }
    if (null != plain.getGroupByColumnReferences()) {

    }

    if (null != plain.getOrderByElements()) {

    }

    if (null != plain.getJoins()) {
      List<Object> joins = new ArrayList<>();
      for (int i = 0; i < plain.getJoins().size(); i++) {
        FromItem fromItem = plain.getJoins().get(i).getRightItem();
        if (fromItem instanceof SubSelect) {
          SubSelect subSelect = (SubSelect) fromItem;
          final PlainSelect subPs = (PlainSelect) subSelect.getSelectBody();
          if (null != subPs) {
            Job innerJob = new Job();
            doSelect(innerJob, subPs);
            List<String> result = innerJob.doExec();
            //todo  write to temp file
          } else {
            joins.add(fromItem.toString());
          }
        } else {
          joins.add(fromItem.toString());
        }
      }

    }



  }



}
