package com.linecorp.bot.model.richmenu;

import java.util.List;

public final class RichMenu
  implements RichMenuCommonProperties
{
  private final RichMenuSize size;
  private final boolean selected;
  private final String name;
  private final String chatBarText;
  private final List<RichMenuArea> areas;
  
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if (!(o instanceof RichMenu)) {
      return false;
    }
    RichMenu other = (RichMenu)o;Object this$size = getSize();Object other$size = other.getSize();
    if (this$size == null ? other$size != null : !this$size.equals(other$size)) {
      return false;
    }
    if (isSelected() != other.isSelected()) {
      return false;
    }
    Object this$name = getName();Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
      return false;
    }
    Object this$chatBarText = getChatBarText();Object other$chatBarText = other.getChatBarText();
    if (this$chatBarText == null ? other$chatBarText != null : !this$chatBarText.equals(other$chatBarText)) {
      return false;
    }
    Object this$areas = getAreas();Object other$areas = other.getAreas();return this$areas == null ? other$areas == null : this$areas.equals(other$areas);
  }
  
  public int hashCode()
  {
    int PRIME = 59;int result = 1;Object $size = getSize();result = result * 59 + ($size == null ? 43 : $size.hashCode());result = result * 59 + (isSelected() ? 79 : 97);Object $name = getName();result = result * 59 + ($name == null ? 43 : $name.hashCode());Object $chatBarText = getChatBarText();result = result * 59 + ($chatBarText == null ? 43 : $chatBarText.hashCode());Object $areas = getAreas();result = result * 59 + ($areas == null ? 43 : $areas.hashCode());return result;
  }
  
  public String toString()
  {
    return "RichMenu(size=" + getSize() + ", selected=" + isSelected() + ", name=" + getName() + ", chatBarText=" + getChatBarText() + ", areas=" + getAreas() + ")";
  }
  
  public static class RichMenuBuilder
  {
    private RichMenuSize size;
    private boolean selected;
    private String name;
    private String chatBarText;
    private List<RichMenuArea> areas;
    
    public String toString()
    {
      return "RichMenu.RichMenuBuilder(size=" + this.size + ", selected=" + this.selected + ", name=" + this.name + ", chatBarText=" + this.chatBarText + ", areas=" + this.areas + ")";
    }
    
    public RichMenu build()
    {
      return new RichMenu(this.size, this.selected, this.name, this.chatBarText, this.areas);
    }
    
    public RichMenuBuilder areas(List<RichMenuArea> areas)
    {
      this.areas = areas;return this;
    }
    
    public RichMenuBuilder chatBarText(String chatBarText)
    {
      this.chatBarText = chatBarText;return this;
    }
    
    public RichMenuBuilder name(String name)
    {
      this.name = name;return this;
    }
    
    public RichMenuBuilder selected(boolean selected)
    {
      this.selected = selected;return this;
    }
    
    public RichMenuBuilder size(RichMenuSize size)
    {
      this.size = size;return this;
    }
  }
  
  RichMenu(RichMenuSize size, boolean selected, String name, String chatBarText, List<RichMenuArea> areas)
  {
    this.size = size;this.selected = selected;this.name = name;this.chatBarText = chatBarText;this.areas = areas;
  }
  
  public static RichMenuBuilder builder()
  {
    return new RichMenuBuilder();
  }
  
  public RichMenuSize getSize()
  {
    return this.size;
  }
  
  public boolean isSelected()
  {
    return this.selected;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getChatBarText()
  {
    return this.chatBarText;
  }
  
  public List<RichMenuArea> getAreas()
  {
    return this.areas;
  }
  
  public RichMenuBuilder toBuilder()
  {
    return new RichMenuBuilder().size(this.size).selected(this.selected).name(this.name).chatBarText(this.chatBarText).areas(this.areas);
  }
}
