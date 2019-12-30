package dev.ckitty.mc.helpme.managers;

import dev.ckitty.mc.helpme.main.ConfigPair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.ckitty.mc.helpme.main.HelpMain.getInt;
import static dev.ckitty.mc.helpme.main.HelpMain.lang;

public class HelpManager {
    
    private Map<String, Integer> bookmarks = new HashMap<>();
    private List<String> pages; // no need to initialize
    private int page_height, page_total;
    
    public void onLoad(ConfigPair config) {
        // get bookmarks
        for (String s : config.dataKeys("config.bookmarks")) {
            bookmarks.put(s, config.data().getInt("config.bookmarks." + s));
        }
        
        // get pages
        pages = config.data().getStringList("pages");
        
        // page-height
        page_height = config.data().getInt("config.page-height");
        
        // page total // ceil of pages.size() / page_height
        page_total = (int) Math.ceil((double) pages.size() / (double) page_height);
    }
    
    public void help(CommandSender sender, String[] args) {
        if (args.length == 0) { // - /help
            sendPage(sender, 1); // send first page
        } else if (args.length == 1) { // - /help <n>
            // a numbered tip
            Integer n = getInt(args[0]);
            
            // check num // means bookmark!
            if (n == null) {
                sendPage(sender, args[0], 1);
                return;
            }
            
            sendPage(sender, n);
        } else if (args.length == 2) { // - /help <bookmark> <n>
            // a numbered tip
            Integer n = getInt(args[1]);
            
            // check num
            if (n == null) {
                sender.sendMessage(lang("misc.not-a-number", "{n}", args[1]));
                return;
            }
            
            sendPage(sender, args[0], n);
        } else
            sender.sendMessage(lang("cmds.help-usage"));
    }
    
    public void sendPage(CommandSender player, String bookmark, int page) {
        // get bookmark
        Integer mark = bookmarks.get(bookmark.toLowerCase());
        
        if (mark == null) {
            player.sendMessage(lang("misc.bmark-doesnt-exist", "{bmark}", bookmark));
            return; // Bookmark doesn't exist!
        }
        
        // send page
        if (page <= 0) {
            player.sendMessage(lang("misc.page-doesnt-exist", "{n}", page));
            return;
        }
        
        sendPage(player, page + mark - 1);
    }
    
    public void sendPage(CommandSender player, int page) {
        // check exists
        if (page <= 0 || page > page_total) {
            player.sendMessage(lang("misc.page-doesnt-exist", "{n}", page));
            return;
        }
        
        // fix number by subtracting 1
        page--;
        
        // send pages from page to page + page_height // max method avoids out of bounds
        int n0 = page * page_height;
        for (int i = n0; i < Math.max(n0 + page_height, page_total); i++) {
            // send message
            player.sendMessage(pages.get(i));
        }
    }
    
}
