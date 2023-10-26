package ltd.dreamcraft.www.autoreview.listeners;

import ltd.dreamcraft.www.autoreview.AutoReview;
import me.albert.amazingbot.bot.Bot;
import me.albert.amazingbot.events.message.GroupMessageEvent;
import me.albert.amazingbot.utils.MsgUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnGroupMessage implements Listener {
    /**
     * 监听群聊消息，如果信息内容等于"统计成员" 就返回统计图
     *
     * @param event 群聊消息事件
     */
    @EventHandler
    public void GroupMsg(GroupMessageEvent event) {
        if (AutoReview.getInstance().getConfig().getStringList("Settings.GroupList").contains(String.valueOf(event.getGroupID()))
                && event.getTextMessage().equalsIgnoreCase("统计成员")) {
            String message = MsgUtil.bufferedImgToMsg(AutoReview.generatePieChart(AutoReview.matchedCategoriesCount));
            Bot.getApi().sendGroupMsg(event.getGroupID(), message);
        }
    }
}
