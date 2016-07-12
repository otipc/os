package co.otipc.job;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class Conditions {

  private String type = null;

  private LinkedList<Condition> items = new LinkedList<>();


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public LinkedList<Condition> getItems() {
    return items;
  }

  public void setItems(LinkedList<Condition> items) {
    this.items = items;
  }



  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
