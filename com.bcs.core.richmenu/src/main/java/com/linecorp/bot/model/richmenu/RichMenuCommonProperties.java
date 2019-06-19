package com.linecorp.bot.model.richmenu;

import java.util.List;

public abstract interface RichMenuCommonProperties
{
  public abstract RichMenuSize getSize();
  
  public abstract boolean isSelected();
  
  public abstract String getName();
  
  public abstract String getChatBarText();
  
  public abstract List<RichMenuArea> getAreas();
}
