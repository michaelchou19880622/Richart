package com.linecorp.bot.model.richmenu;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class RichMenuSize
{
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if (!(o instanceof RichMenuSize)) {
      return false;
    }
    RichMenuSize other = (RichMenuSize)o;
    if (getWidth() != other.getWidth()) {
      return false;
    }
    return getHeight() == other.getHeight();
  }
  
  public int hashCode()
  {
    int PRIME = 59;int result = 1;result = result * 59 + getWidth();result = result * 59 + getHeight();return result;
  }
  
  public String toString()
  {
    return "RichMenuSize(width=" + getWidth() + ", height=" + getHeight() + ")";
  }
  
  public static final RichMenuSize FULL = new RichMenuSize(2500, 1686);
  public static final RichMenuSize HALF = new RichMenuSize(2500, 843);
  private final int width;
  private final int height;
  
  public int getWidth()
  {
    return this.width;
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  @JsonCreator
  public RichMenuSize(int width, int height)
  {
    this.width = width;
    this.height = height;
  }
}
