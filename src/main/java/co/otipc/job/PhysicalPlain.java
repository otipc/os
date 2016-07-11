package co.otipc.job;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class PhysicalPlain {

  private Job job;

  private Conditions conditions;

  List<Condition> conditionItems = new ArrayList<>();


  public PhysicalPlain() {

  }

  public Job getJob(String sql) throws JSQLParserException {

    Statement parse = CCJSqlParserUtil.parse(sql);
    Select select = (Select) parse;
    PlainSelect ps = (PlainSelect) select.getSelectBody();


    printPlan(ps);

    return job;
  }



  private Map<Object, Object> testExpr(Expression expression, Condition condition) {

    Map<Object, Object> result = new HashMap<>();

    //    Condition condition;

    if (expression instanceof AndExpression) {
      conditions.setType("AND");
      AndExpression and = (AndExpression) expression;
      Expression left = and.getLeftExpression();
      Expression right = and.getRightExpression();
      testExpr(right, null);
      testExpr(left, null);
      result.put(testExpr(left, null), testExpr(right, null));
    } else if (expression instanceof OrExpression) {
      conditions.setType("OR");
      OrExpression and = (OrExpression) expression;
      Expression left = and.getLeftExpression();
      Expression right = and.getRightExpression();
      result.put(testExpr(left, null), testExpr(right, null));
    } else if (expression instanceof EqualsTo) {
      condition = new Condition();
      condition.setType("==");
      EqualsTo eq = (EqualsTo) expression;
      Expression left = eq.getLeftExpression();
      Expression right = eq.getRightExpression();
      result.put(testExpr(left, condition), testExpr(right, condition));
    } else if (expression instanceof SubSelect) {
      SubSelect subSelect = (SubSelect) expression;
      PlainSelect plain = (PlainSelect) subSelect.getSelectBody();
      printPlan(plain);
      //      result.put("subSelect", printPlan(plain));
      return null;
    } else if (expression instanceof InExpression) {
      condition = new Condition();
      condition.setType("in");
      InExpression in = (InExpression) expression;
      Expression left = in.getLeftExpression();
      result.put("where_eq_l", testExpr(left, condition));
      ItemsList items = in.getRightItemsList();
      if (items instanceof SubSelect) {
        SubSelect subSelect = (SubSelect) items;
        PlainSelect plain = (PlainSelect) subSelect.getSelectBody();
        printPlan(plain);
        return null;
      }
    } else if (expression instanceof Column) {
      Column column = (Column) expression;
      condition.setColumn(column.getColumnName().toString());
      result.put("Column", expression);
    } else if (expression instanceof Function) {
      Function fun = (Function) expression;
      result.put("function", fun.getName());
      ExpressionList list = fun.getParameters();
      for (int i = 0; i < list.getExpressions().size(); i++) {
        result.put("param_" + i, list.getExpressions().get(i).toString());
      }
    } else {
      String str = expression.toString();
      if (str.startsWith("'") && str.endsWith("'")) {
        str = str.substring(1, str.length() - 1);
      }
      condition.setValue(str);
      this.conditionItems.add(condition);
    }



    return result;
  }

  private Map<Object, Object> testItem(SelectItem item) {
    Map<Object, Object> result = new HashMap<>();

    if (item instanceof AllColumns) {
      this.job.getDims().add("*");
      result.put("all", "all");
    } else if (item instanceof AllTableColumns) {
      AllTableColumns columns = (AllTableColumns) item;
      result.put("all", "all");
      result.put("table", columns.getTable());
    } else if (item instanceof SelectExpressionItem) {
      Expression expression = ((SelectExpressionItem) item).getExpression();
      if (expression instanceof Column) {
        Column column = (Column) expression;
        this.job.getDims().add(column.getColumnName());
        result.put("Column", expression);
      } else if (expression instanceof Function) {
        Function fun = (Function) expression;
        result.put("function", fun.getName());
        ExpressionList list = fun.getParameters();
        for (int i = 0; i < list.getExpressions().size(); i++) {
          result.put("param_" + i, list.getExpressions().get(i).toString());
        }
      }
    }



    return result;
  }

  private void printPlan(PlainSelect plain) {
    this.job = new Job();
    //    Map<Object, Object> result = new HashMap<>();

    if (null != plain.getSelectItems()) {
      List<Object> l = new ArrayList<>();
      for (SelectItem item : plain.getSelectItems()) {
        l.add(testItem(item));
      }
      //      result.put("selectItem", l);
    }

    if (null != plain.getFromItem()) {
      this.job.setTable(plain.getFromItem().toString());

    }
    if (null != plain.getWhere()) {
      Expression expression = plain.getWhere();
      conditions = new Conditions();
      testExpr(expression, null);
      if (this.conditionItems.size() > 0) {
        conditions.setItems(this.conditionItems);
      }
      this.job.setConditions(conditions);
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
            printPlan(subPs);
          } else {
            joins.add(fromItem.toString());
          }
        } else {
          joins.add(fromItem.toString());
        }
      }

    }



  }

  private static void test2() throws JSQLParserException {
    String sql =
      "select t1.a, t2.b from t1, t2, t3 where t1.id = t2.id and a > b group by name order by id";
    Statement parse = CCJSqlParserUtil.parse(sql);
    Select select = (Select) parse;
    PlainSelect ps = (PlainSelect) select.getSelectBody();
    System.out.println(ps);
    System.out.println(ps.getFromItem());
    System.out.println(ps.getJoins());

    System.out.println(ps.getWhere());

    System.out.println(ps.getGroupByColumnReferences());
    System.out.println(ps.getOrderByElements());
  }

  private static void test() throws JSQLParserException {

    CCJSqlParserManager pm = new CCJSqlParserManager();

    String sql = "select count(*) from table1";

    Statement stat = pm.parse(new StringReader(sql));

    if (stat instanceof Select) {
      Select select = (Select) stat;
      PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

      System.out.println(plainSelect.getSelectItems().size());

      for (SelectItem item : plainSelect.getSelectItems()) {
        if (item instanceof SelectExpressionItem) {
          Expression expression = ((SelectExpressionItem) item).getExpression();
          if (expression instanceof Function) {
            Function function = (Function) expression;
            System.out.println(function.isAllColumns());
            System.out.println(function.getName());
          }
        }
      }


    }
  }


}
