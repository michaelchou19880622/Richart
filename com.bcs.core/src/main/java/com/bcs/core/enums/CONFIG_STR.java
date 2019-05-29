package com.bcs.core.enums;

public enum CONFIG_STR {

	// need setting
	LINE_POINT_API_CLIENT_ID("line.point.api.client.id"),
	LINE_POINT_MESSAGE_PUSH_URL("line.point.push.url"),

	HASH_PREFIX1(""),
	
	//--------------- 新增常數--------------------------

	
	SYSTEM_START_DATE("system.start.date"),
	SYSTEM_TYPE("bcs.system.type"),
	
	
	SYSTEM_ID("system.id"),
	SYSTEM_TYPE_IS_API("system.is.api"),
	IS_MAIN_SYSTEM("is.main.system"),
	
	SYSTEM_COUPON_USE_TIME("system.coupon.record.useTime"),
	SYSTEM_REWARDCARD_USE_TIME("system.rewardcard.record.useTime"),
	
	SYSTEM_USE_PROXY("system.use.proxy"),
	SYSTEM_CHECK_SIGNATURE("system.check.signature"),
	
	BaseUrlHTTPS("bcs.base.https.url"),
	BaseUrlHTTP("bcs.base.http.url"),
	
	PageMobile("bcs.base.url.page.mobile"),
	ResourceMobile("bcs.base.url.resource.mobile"),
	
	PageBCS("bcs.base.url.page.bcs"),
	ResourceBCS("bcs.base.url.resource.bcs"),
	
	FilePath("file.path"),
	
	Default("Richart"),
	
	AutoReply("AutoReply"),
	ManualReply("ManualReply"),
	InManualReplyButNotSendMsg("InManualReplyButNotPush"),
	
	M_PAGE("bcs.m.page"),
	
	RICHART_POST_URL_USER_STATUS("richart.post.url.userStatus"),
	
	LINE_GET_PROFILE_URL("line.get.profile.url"),
	LINE_MESSAGE_REPLY_URL("line.message.reply.url"),
	LINE_MESSAGE_PUSH_URL("line.message.push.url"),
	
	LINE_POST_URL_BC("line.post.url.bc"),

	LINE_GET_URL_BC("line.get.url.bc"),
	LINE_GET_URL_BOT("line.get.url.bot"),
	
	LINE_OAUTH_URL("line.oauth.url"),
	LINE_OAUTH_URL_V2_1("line.oauth.url.v2.1"),
	LINE_OAUTH_URL_ACCESSTOKEN("line.oauth.url.accessToken"),
	LINE_OAUTH_URL_ACCESSTOKEN_V2_1("line.oauth.url.accessToken.v2.1"),
	LINE_OAUTH_VERIFY("line.oauth.verify"),
	LINE_OAUTH_PROFILE("line.oauth.url.profile"),
	LINE_OAUTH_FRIENDSHIP_STATUS("line.oauth.url.friendship.status"),
	LINE_CONVERTING_GET("line.converting.get"),
	LINE_CONVERTING_POST("line.converting.post"),
	LINE_SWITCH_API_SWITCHER_SWITCH("line.switcher.switch"),
	LINE_SWITCH_API_SWITCHER_NOTICE("line.switcher.notice"),
	
	ChannelToken("ChannelToken"),
	LINE_POINT_ChannelToken("LinePoint.ChannelToken"),
	ChannelServiceCode("ChannelServiceCode"),
	ChannelRefresh("ChannelRefresh"),
	
	ChannelID("ChannelID"),
	ChannelSecret("ChannelSecret"),
	Channel_MID("Channel_MID"),
	
	ChannelSwitchIconName("ChannelSwitchIconName"),
	ChannelSwitchIconUrl("ChannelSwitchIconUrl"),

	EVENT_SHARE("bcs.event.share"),
	EVENT_SHARE_DELAY("bcs.event.share.delay"),

	SMART_ROBOT_API("smartrobot.api.url"),
	SMART_ROBOT_BOT_API("smartrobot.bot.api.url"),
	
	TRACING_CONFIG_GET_FROM_SESSION("tracing.config.get.from.session"),
	TRACING_CONFIG_USE_SWITCH("tracing.config.use.switch"),
	TRACING_CONFIG_CHECK_MOBILE("tracing.config.check.mobile"),
	
	HASH_PREFIX("hash.prefix"),
	HASH_SUFFIX("hash.suffix"),

	RECORD_RECEIVE_AUTORESPONSE_TEXT("record.receive.autoresponse.text"),

	PASSWORD_PREFIX("password.prefix"),
	PASSWORD_SUFFIX("password.suffix"),
	SSO_LOGIN_RUL("sso.login.url"),
	
	API_ORIGINAL_TOKEN("api.original.token"),
	AES_SECRET_KEY("aes.secret.key"),
	AES_INITIALIZATION_VECTOR("aes.initialization.vector"),

	RICHART_PROXY_URL("richart.proxy.url"),
	RICHART_LOGIN_URL("richart.login.url"),
	RICHART_ANNOUNCEMENT_URL("richart.announcement.url"),
	
	GATEWAY_CHANNEL("gateway.channel"),
	GATEWAY_API_URL("gateway.api.url"),
	GATEWAY_API_KEY("gateway.api.key"),
	GATEWAY_API_SECRET("gateway.api.secret"),
	
	LIVECHAT_START_API_URL("livechat.start.api.url"),
	LIVECHAT_ADD_MESSAGE_API_URL("livechat.add.message.api.url"),
	LIVECHAT_GIVE_UP_API_URL("livechat.giveup.api.url"),
	LIVECHAT_CLOSE_API_URL("livechat.close.api.url"),
	LIVECHAT_CHECK_API_URL("livechat.check.api.url"),
	LIVECHAT_RESET_API_URL("livechat.reset.api.url"),
	LIVECHAT_LEAVE_MESSAGE_API_URL("livechat.leave.message.api.url"),
	
	CHATLOG_GET_MAX_HOUR("chatlog.get.max.hour"),
	LIVECHAT_STATUS_CHECK_CRON("livechat.status.check.cron"),
	
	AUTOREPLY_CHANNEL_NAME("autoreply.channel.name"),
	MANUALREPLY_CHANNEL_NAME("manualreply.channel.name"),
	
	RICHART_LOG_API_URL("richart.log.api.url"),
	RICHART_LOG_API_KEY("richart.log.api.key"),
	
	PNP_FTP_HOSTNAME("pnp.ftp.hostname"),
	PNP_FTP_USERNAME("pnp.ftp.username"),
	PNP_FTP_PASSWORD("pnp.ftp.password"),
	
	PNP_FTP_REMOTE_DIR_PATH("pnp.ftp.remote.dr.path"),
	PNP_FTP_LOCAL_DIR_PATH("pnp.ftp.local.dr.path"),
	
	PNP_FTP_FILE_COMPLETE_PATTERN("pnp.ftp.file.complete.pattern"),
	
	PNP_CLUSTER_SEND_URL("pnp.cluster.send.url"),
	
	SRC_USE_STATIC("src.use.static"),
	
	BCS_API_CLUSTER_SEND("rest.api.cluster.send"),
	BCS_API_CLUSTER_SEND_THIS("rest.api.cluster.send.this"),
	
	//MGM
	MGM_ACTION_IMG_CDN_URL("mgm.action.img.cdn.url"),
	MGM_SHARE_IMG_CDN_URL("mgm.share.img.cdn.url"),
	MGM_DESCRIPTION_IMG_CDN_URL("mgm.description.img.cdn.url"),
	ADD_LINE_FRIEND_LINK("add.line.friend.link"),
	
	//Rich Menu
	NUMBER_OF_ITEM_IN_LISTPAGE("number.of.item.in.listPage"),
	
	LINE_RICH_MENU_CREATE_API("line.rich.menu.create.api"),
	LINE_RICH_MENU_UPLOAD_IMAGE_API("line.rich.menu.upload.image.api"),
	LINE_RICH_MENU_LINK_ALL_API("line.rich.menu.link.all.api"),
	LINE_RICH_MENU_LINK_API("line.rich.menu.link.api"),
	LINE_RICH_MENU_UNLINK_API("line.rich.menu.unlink.api"),
	LINE_RICH_MENU_DELETE_API("line.rich.menu.delete.api"),
	LINE_RICH_MENU_GET_INFO_API("line.rich.menu.get.info.api"),
	LINE_RICH_MENU_GET_INFO_LIST_API("line.rich.menu.get.info.list.api"),
	LINE_RICH_MENU_GET_LINK_ID_OF_USER_API("line.rich.menu.get.link.id.of.user.api"),
	LINE_RICH_MENU_DOWNLOAD_IMAGE_API("line.rich.menu.download.image.api"),
	;

   private final String str;
    
    CONFIG_STR(String str) {
        this.str = str;
    }
	/**
	 * @return the str
	 */
	public String toString() {
		return str;
	}

}
