package com.linecorp.bot.model.richmenu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class RichMenuListResponse
{
  @JsonProperty("richmenus")
  private final List<RichMenuResponse> richMenus;
  
  public String toString()
  {
    return "RichMenuListResponse(richMenus=" + getRichMenus() + ")";
  }
  
  public int hashCode()
  {
    int PRIME = 59;int result = 1;Object $richMenus = getRichMenus();result = result * 59 + ($richMenus == null ? 43 : $richMenus.hashCode());return result;
  }
  
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if (!(o instanceof RichMenuListResponse)) {
      return false;
    }
    RichMenuListResponse other = (RichMenuListResponse)o;Object this$richMenus = getRichMenus();Object other$richMenus = other.getRichMenus();return this$richMenus == null ? other$richMenus == null : this$richMenus.equals(other$richMenus);
  }
  
  public List<RichMenuResponse> getRichMenus()
  {
    return this.richMenus;
  }
  
  @JsonCreator
  public RichMenuListResponse(List<RichMenuResponse> richMenus)
  {
    this.richMenus = richMenus;
  }
}
