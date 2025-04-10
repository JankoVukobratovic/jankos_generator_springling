package org.jankos.springling.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.swing.*;

@Data
@AllArgsConstructor
public class Field {
  public String name;
  public String type;

  public JLabel toLabel() {
    String displayText = "<html><b>" + type + "</b> " + name + "</html>";
    return new JLabel(displayText);
  }
}
