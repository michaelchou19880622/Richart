package com.linecorp.bot.model.richmenu;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class RichMenuIdResponse
{
  private final String richMenuId;
  
  public String toString()
  {
    return "RichMenuIdResponse(richMenuId=" + getRichMenuId() + ")";
  }
  
  public int hashCode()
  {
    int PRIME = 59;int result = 1;Object $richMenuId = getRichMenuId();result = result * 59 + ($richMenuId == null ? 43 : $richMenuId.hashCode());return result;
  }
  
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if (!(o instanceof RichMenuIdResponse)) {
      return false;
    }
    RichMenuIdResponse other = (RichMenuIdResponse)o;Object this$richMenuId = getRichMenuId();Object other$richMenuId = other.getRichMenuId();return this$richMenuId == null ? other$richMenuId == null : this$richMenuId.equals(other$richMenuId);
  }
  
  public String getRichMenuId()
  {
    return this.richMenuId;
  }
  
  @JsonCreator
  public RichMenuIdResponse(String richMenuId)
  {
    this.richMenuId = richMenuId;
  }
}
