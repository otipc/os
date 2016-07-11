package co.otipc;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chaoguo.Cui on 16/7/7.
 */
public class MainTest {

  //实现从SQL中提取表名
  public static final List<String> getTables(String sql) {
    CCJSqlParserManager parserManager = new CCJSqlParserManager();
    Statement stmt;
    try {
      //解析SQL语句
      stmt = parserManager.parse(new StringReader(sql));
    } catch (JSQLParserException e) {
      return null;
    }
    final List<String> tableNames = new ArrayList<String>();
    //使用visitor模式访问SQL的各个组成部分
    stmt.accept(new MyStatementVisitor(tableNames));
    return tableNames;
  }

  static class MySelectVisitor implements SelectVisitor {

    List<String> tableNames;

    public MySelectVisitor(List<String> tableNames) {
      this.tableNames = tableNames;
    }

    @Override public void visit(SetOperationList setOpList) {

    }

    @Override public void visit(WithItem withItem) {

    }

    @Override
    public void visit(PlainSelect ps) {
      FromItemVisitor fromItemVisitor = new FromItemVisitor() {

        @Override
        public void visit(Table table) {
          tableNames.add(table.getName());
        }

        @Override public void visit(LateralSubSelect lateralSubSelect) {

        }

        @Override public void visit(TableFunction tableFunction) {

        }

        @Override public void visit(ValuesList valuesList) {

        }

        @Override
        public void visit(SubSelect ss) {
          ss.getSelectBody().accept(new MySelectVisitor(tableNames));
        }

        @Override
        public void visit(SubJoin sj) {
          sj.getLeft().accept(this);
          sj.getJoin().getRightItem().accept(this);
        }

      };

      ps.getFromItem().accept(fromItemVisitor);

      List<Join> joins = ps.getJoins();
      if (joins != null) {
        for (Join join : joins) {
          join.getRightItem().accept(fromItemVisitor);
        }
      }
    }


  }

  static class MyStatementVisitor implements StatementVisitor {
    List<String> tableNames;

    public MyStatementVisitor(List<String> tableNames) {
      this.tableNames = tableNames;
    }

    @Override public void visit(Merge merge) {

    }

    @Override public void visit(Alter alter) {

    }

    @Override public void visit(CreateIndex createIndex) {

    }

    @Override public void visit(CreateView createView) {

    }

    @Override public void visit(Execute execute) {

    }

    @Override public void visit(SetStatement set) {

    }

    @Override public void visit(Statements stmts) {

    }

    //访问select语句
    public void visit(Select select) {
      //访问select的各个组成部分
      select.getSelectBody().accept(new MySelectVisitor(tableNames));
    }

    //访问delete语句
    public void visit(Delete delete) {
      tableNames.add(delete.getTable().getName());
    }

    //访问update语句
    public void visit(Update update) {
      for(Table table:update.getTables()){
        tableNames.add(table.getName());
      }

    }

    //访问insert语句
    public void visit(Insert insert) {
      tableNames.add(insert.getTable().getName());
    }

    //访问replace，忽略
    public void visit(Replace replace) {
    }

    //访问drop，忽略
    public void visit(Drop drop) {
    }

    //访问truncate，忽略
    public void visit(Truncate truncate) {
    }

    //访问create，忽略
    public void visit(CreateTable arg0) {
    }
  }

  public static void main(String[] args) throws JSQLParserException {
    System.out.println(getTables("select * from  (select * from table_a left outer join table_b on table_a.aa=table_b.bb)"));
    //输出结果：[table_a, table_b]
  }


}
