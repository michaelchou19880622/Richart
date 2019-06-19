package com.linecorp.bot.model.richmenu;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class RichMenuBounds
{
  private final int x;
  private final int y;
  private final int width;
  private final int height;
  
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if (!(o instanceof RichMenuBounds)) {
      return false;
    }
    RichMenuBounds other = (RichMenuBounds)o;
    if (getX() != other.getX()) {
      return false;
    }
    if (getY() != other.getY()) {
      return false;
    }
    if (getWidth() != other.getWidth()) {
      return false;
    }
    return getHeight() == other.getHeight();
  }
  
  public int hashCode()
  {
    int PRIME = 59;int result = 1;result = result * 59 + getX();result = result * 59 + getY();result = result * 59 + getWidth();result = result * 59 + getHeight();return result;
  }
  
  public String toString()
  {
    return "RichMenuBounds(x=" + getX() + ", y=" + getY() + ", width=" + getWidth() + ", height=" + getHeight() + ")";
  }
  
  public int getX()
  {
    return this.x;
  }
  
  public int getY()
  {
    return this.y;
  }
  
  public int getWidth()
  {
    return this.width;
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  @JsonCreator
  public RichMenuBounds(int x, int y, int width, int height)
  {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
}
