package co.otipc;

import co.otipc.job.Job;
import co.otipc.job.PhysicalPlain;
import net.sf.jsqlparser.JSQLParserException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Chaoguo.Cui on 16/7/7.
 */
public class App {


  public static void main(String[] args) throws JSQLParserException, IOException {


    //    String sql =
    //      "SELECT MY_TABLE1.* FROM MY_TABLE1, MY_TABLE2, (SELECT * FROM MY_TABLE3) LEFT OUTER JOIN MY_TABLE4 "
    //        + " WHERE ID = (SELECT MAX(ID) FROM MY_TABLE5) AND ID2 IN (SELECT * FROM MY_TABLE6) AND id3=3";

//    String sql = "select * from table_1 where id=0001 and age=31 and name='otipc'";

    String sql = "SELECT * FROM  a INNER JOIN  b";

    //        String sql = "select * from table_1 where age in(33,40)";

    //    String sql = "select * from student where class in (select id from class where name='class_4')";

    PhysicalPlain physicalPlain = new PhysicalPlain();

    Job job = physicalPlain.getJob(sql);

    System.out.println(job);

    List<String> result = job.doExec();

    System.out.println(result.size());
    for (String str : result) {
      System.out.println(str);
    }



  }



}
