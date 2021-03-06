package co.otipc.plain;


import co.otipc.job.Conditions;
import co.otipc.job.Job;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chaoguo.Cui on 16/7/12.
 */
public class VisitorSelect {

  private static final Logger LOGGER = LoggerFactory.getLogger(VisitorSelect.class);

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
      Conditions conditions=new Conditions();
      VisitorExpression.doExpr(conditions, expression);
      job.setConditions(conditions);
    }
    if (null != plain.getGroupByColumnReferences()) {

    }

    if (null != plain.getOrderByElements()) {

    }

    if (null != plain.getJoins()) {

      job.setSubJobs(plain.getJoins());

//      for (int i = 0; i < plain.getJoins().size(); i++) {
//        Join join = plain.getJoins().get(i);
//        if(join.isSimple()){
//        }
//      }


      //      List<Object> joins = new ArrayList<>();
      //      for (int i = 0; i < plain.getJoins().size(); i++) {
      //        FromItem fromItem = plain.getJoins().get(i).getRightItem();
      //        if (fromItem instanceof SubSelect) {
      //          SubSelect subSelect = (SubSelect) fromItem;
      //          final PlainSelect subPs = (PlainSelect) subSelect.getSelectBody();
      //          if (null != subPs) {
      //            Job innerJob = new Job();
      //            doSelect(innerJob, subPs);
      //            List<String> result = innerJob.doExec();
      //            //todo  write to temp file
      //          } else {
      //            joins.add(fromItem.toString());
      //          }
      //        } else {
      //          joins.add(fromItem.toString());
      //        }
      //      }

    }



  }



}
