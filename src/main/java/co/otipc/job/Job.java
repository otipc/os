package co.otipc.job;

import net.sf.jsqlparser.statement.select.Join;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class Job {

  private List<String> dims = new ArrayList<>();

  private Conditions conditions = new Conditions();

  private String table;

  private Queue<Join> subJobs = new LinkedBlockingQueue<>();


  public boolean isNeedJoin() {
    return !subJobs.isEmpty();
  }

  public void setDims(List<String> dims) {
    this.dims = dims;
  }

  public List<String> getDims() {
    return dims;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getTable() {
    return table;
  }


  public void setConditions(Conditions conditions) {
    this.conditions = conditions;
  }

  public Conditions getConditions() {
    return conditions;
  }


  public List<String> doExec() {
    try {
      return Executor.exec(this);
    } catch (IOException e) {

    }
    return null;
  }

  public void setSubJobs(List<Join> subJobs) {
    this.subJobs.addAll(subJobs);
  }

  public Join getJoin() {
    return this.subJobs.poll();
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }


  public static void main(String[] args) {
    Job job = new Job();
    job.subJobs.add(new Join());
    System.out.println(job.isNeedJoin());

    job.getJoin();
    System.out.println(job.isNeedJoin());

  }

}
