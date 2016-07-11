package co.otipc.job;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class Conditions {

  String type = null;

  List<Condition> items;


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Condition> getItems() {
    return items;
  }

  public void setItems(List<Condition> items) {
    this.items = items;
  }



  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
