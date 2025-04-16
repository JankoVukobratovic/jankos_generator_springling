package org.jankos.springling.exceptions;

import org.freedesktop.dbus.errors.InvalidMethodArgument;

public class NotAnEntityException extends InvalidMethodArgument {
  public NotAnEntityException() {
    super("Not an entity class");
  }
}
