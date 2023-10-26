package ltd.dreamcraft.www.autoreview.listeners;

import ltd.dreamcraft.www.autoreview.AutoReview;
import me.albert.amazingbot.bot.Bot;
import me.albert.amazingbot.events.request.GroupRequestJoinEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


public class OnUserRequestJoin implements Listener {
    @EventHandler
    public void RequestCheck(GroupRequestJoinEvent event) {
        FileConfiguration config = AutoReview.getInstance().getConfig();
        List<String> GroupList = config.getStringList("Settings.GroupList");
        if (GroupList.contains(String.valueOf(event.getGroupID()))) {
            //qq用户发送的群验证消息
            String verify_message = event.getComment();
            //标记是否通过验证
            boolean isApprove = false;
            outerLoop:
            // 外层循环标签
            for (Map.Entry<String, List<String>> entry : AutoReview.categories.entrySet()) {
                String categoryName = entry.getKey();
                List<String> keywords = entry.getValue();
                for (String keyword : keywords) {
                    if (verify_message.contains(keyword)) {
                        // 设置验证消息为通过
                        event.approve(true, "");
                        // 发送私聊消息告诉 管理员
                        Bot.getApi().sendPrivateMsg(Long.valueOf(config.getString("Settings.admin")), event.getUserID() + "通过自动审核\n" + event.getComment());
                        // 使匹配的类别数量+1
                        AutoReview.matchedCategoriesCount.put(categoryName, AutoReview.matchedCategoriesCount.getOrDefault(categoryName, 0) + 1);
                        //标记通过验证
                        isApprove = true;
                        //跳出两层for内层循环至循环标签 否则会处理多条群信息如果内容是mcbbs bbs 这种雷同的东西
                        break outerLoop;
                    }
                }
            }
            //保存统计信息到文本中
            saveCategoryCounts(AutoReview.matchedCategoriesCount, AutoReview.getInstance().getDataFolder() + "\\category_counts.txt");

            if (config.getBoolean("Settings.AutoRefuseFun") && !isApprove) {
                event.approve(false, config.getString("Settings.AutoRefuseMessage"));
                Bot.getApi().sendPrivateMsg(Long.valueOf(config.getString("Settings.admin")), config.getString("Settings.AutoRefuseMessagePrivate")
                        .replace("{qq}", String.valueOf(event.getUserID()))
                        .replace("{message}", event.getComment()));
            }
        }
    }

    /**
     * 将类别计数保存到文件
     *
     * @param categoryCounts
     * @param filename
     */
    public void saveCategoryCounts(Map<String, Integer> categoryCounts, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


