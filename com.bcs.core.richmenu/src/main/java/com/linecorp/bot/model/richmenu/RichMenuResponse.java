package com.linecorp.bot.model.richmenu;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;

public final class RichMenuResponse
  implements RichMenuCommonProperties
{
  private final String richMenuId;
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
    if (!(o instanceof RichMenuResponse)) {
      return false;
    }
    RichMenuResponse other = (RichMenuResponse)o;Object this$richMenuId = getRichMenuId();Object other$richMenuId = other.getRichMenuId();
    if (this$richMenuId == null ? other$richMenuId != null : !this$richMenuId.equals(other$richMenuId)) {
      return false;
    }
    Object this$size = getSize();Object other$size = other.getSize();
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
    int PRIME = 59;int result = 1;Object $richMenuId = getRichMenuId();result = result * 59 + ($richMenuId == null ? 43 : $richMenuId.hashCode());Object $size = getSize();result = result * 59 + ($size == null ? 43 : $size.hashCode());result = result * 59 + (isSelected() ? 79 : 97);Object $name = getName();result = result * 59 + ($name == null ? 43 : $name.hashCode());Object $chatBarText = getChatBarText();result = result * 59 + ($chatBarText == null ? 43 : $chatBarText.hashCode());Object $areas = getAreas();result = result * 59 + ($areas == null ? 43 : $areas.hashCode());return result;
  }
  
  public String toString()
  {
    return "RichMenuResponse(richMenuId=" + getRichMenuId() + ", size=" + getSize() + ", selected=" + isSelected() + ", name=" + getName() + ", chatBarText=" + getChatBarText() + ", areas=" + getAreas() + ")";
  }
  
  public String getRichMenuId()
  {
    return this.richMenuId;
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
  
  @JsonCreator
  public RichMenuResponse(String richMenuId, RichMenuSize size, boolean selected, String name, String chatBarText, List<RichMenuArea> areas)
  {
    this.richMenuId = richMenuId;
    this.size = size;
    this.selected = selected;
    this.name = name;
    this.chatBarText = chatBarText;
    this.areas = areas;
  }
}
