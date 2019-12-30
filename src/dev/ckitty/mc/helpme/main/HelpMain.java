package dev.ckitty.mc.helpme.main;

import dev.ckitty.mc.helpme.managers.HelpManager;
import dev.ckitty.mc.helpme.managers.TipManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class HelpMain extends JavaPlugin implements Listener {
    
    /*
     * Commands
     * /help (rework) into /help <page> or /help <bookmark> <page>
     * /helpbook (creates a book with help info.)
     * /tip (gives a tip)
     *
     * */
    
    public static HelpMain INSTANCE;
    private static ConfigPair CONFIG, LANG;
    private static HelpManager HELPMAN;
    private static TipManager TIPMAN;
    
    @Override
    public void onEnable() {
        // instance obj
        INSTANCE = this;
        
        // create configs
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) saveResource("config.yml", true);
        CONFIG = new ConfigPair().setFile(configFile).pack();
        
        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) saveResource("lang.yml", true);
        LANG = new ConfigPair().setFile(langFile).pack();
        
        // managers
        HELPMAN = new HelpManager();
        HELPMAN.onLoad(CONFIG);
        
        TIPMAN = new TipManager();
        TIPMAN.onLoad(CONFIG);
        TIPMAN.createTask();
        
        // commands & events
        this.getCommand("hmpt").setExecutor(this);
        this.getServer().getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public void onDisable() {
        TIPMAN.deleteTask();
        TIPMAN = null;
        HELPMAN = null;
        
        LANG = null;
        CONFIG = null;
        
        INSTANCE = null;
    }
    
    private void reloadConfigs() {
        CONFIG.reload();
        LANG.reload();
        
        HELPMAN.onLoad(CONFIG);
        TIPMAN.onLoad(CONFIG);
    }
    
    public static String lang(String msg, Object... objs) {
        String target = LANG.data().getString(msg);
        
        if (target == null) {
            return "[HelpMe] The message " + msg + " does not exist!";
        }
        
        for (int i = 0; i < objs.length; i += 2) {
            target = target.replace(objs[i].toString(), objs[i + 1].toString());
        }
        return target;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /* commands: /hmpt tip [n]
                     /hmpt help <page>
                     /hmpt help <bookmark> <page>
                     /hmpt reload
        */
        
        // label: help
        if (label.equalsIgnoreCase("help")) {
            HELPMAN.help(sender, args);
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(lang("cmds.hmpt-usage"));
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "tip":
                if (args.length == 1) {
                    // just a random tip
                    sender.sendMessage(TIPMAN.randomTip());
                } else if (args.length == 2) {
                    // a numbered tip
                    Integer n = getInt(args[1]);
                    
                    // check num
                    if (n == null) {
                        sender.sendMessage(lang("misc.not-a-number", "{n}", args[1]));
                        return true;
                    }
                    
                    // check bounds
                    if (n <= 0 || n > TIPMAN.size()) {
                        sender.sendMessage(lang("misc.tip-doesnt-exist", "{n}", args[1]));
                        return true;
                    }
                    
                    // send tip // subtract one
                    sender.sendMessage(TIPMAN.getTip(n - 1));
                } else
                    sender.sendMessage(lang("cmds.hmpt-tip"));
                break;
            case "help":
                if (args.length == 1) { // - /hmpt help
                    HELPMAN.sendPage(sender, 1); // send first page
                } else if (args.length == 2) { // - /hmpt help <n>
                    // a numbered tip
                    Integer n = getInt(args[1]);
                    
                    // check num // means bookmark!
                    if (n == null) {
                        HELPMAN.sendPage(sender, args[1], 1);
                        return true;
                    }
                    
                    HELPMAN.sendPage(sender, n);
                } else if (args.length == 3) { // - /hmpt help <bookmark> <n>
                    // a numbered tip
                    Integer n = getInt(args[2]);
                    
                    // check num
                    if (n == null) {
                        sender.sendMessage(lang("misc.not-a-number", "{n}", args[2]));
                        return true;
                    }
                    
                    HELPMAN.sendPage(sender, args[1], n);
                } else
                    sender.sendMessage(lang("cmds.hmpt-help"));
                break;
            case "reload":
                if (args.length != 1) { // just one arg
                    sender.sendMessage(lang("cmds.hmpt-reload"));
                    return true;
                }
                
                // reload configs!
                reloadConfigs();
                
                // send info!
                sender.sendMessage(lang("cmds.reloaded"));
                break;
            default:
                // default branch should catch any misusage!
                sender.sendMessage(lang("cmds.hmpt-usage"));
                break;
        }
        return true;
    }
    
    public static Integer getInt(String num) {
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            return null;
        }
    }
    
}
