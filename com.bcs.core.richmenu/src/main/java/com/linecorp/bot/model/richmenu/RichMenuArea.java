package com.linecorp.bot.model.richmenu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.linecorp.bot.model.action.Action;

public final class RichMenuArea
{
  private final RichMenuBounds bounds;
  private final Action action;
  
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if (!(o instanceof RichMenuArea)) {
      return false;
    }
    RichMenuArea other = (RichMenuArea)o;Object this$bounds = getBounds();Object other$bounds = other.getBounds();
    if (this$bounds == null ? other$bounds != null : !this$bounds.equals(other$bounds)) {
      return false;
    }
    Object this$action = getAction();Object other$action = other.getAction();return this$action == null ? other$action == null : this$action.equals(other$action);
  }
  
  public int hashCode()
  {
    int PRIME = 59;int result = 1;Object $bounds = getBounds();result = result * 59 + ($bounds == null ? 43 : $bounds.hashCode());Object $action = getAction();result = result * 59 + ($action == null ? 43 : $action.hashCode());return result;
  }
  
  public String toString()
  {
    return "RichMenuArea(bounds=" + getBounds() + ", action=" + getAction() + ")";
  }
  
  public RichMenuBounds getBounds()
  {
    return this.bounds;
  }
  
  public Action getAction()
  {
    return this.action;
  }
  
  @JsonCreator
  public RichMenuArea(RichMenuBounds bounds, Action action)
  {
    this.bounds = bounds;
    this.action = action;
  }
}
