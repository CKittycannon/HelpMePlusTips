package dev.ckitty.mc.helpme.managers;

import dev.ckitty.mc.helpme.main.ConfigPair;
import dev.ckitty.mc.helpme.main.HelpMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class TipManager implements Runnable {
    
    private List<String> tips;
    private List<String> blacklist;
    private int timer;
    private BukkitTask task;
    
    public void onLoad(ConfigPair config) {
        // get tips
        tips = config.data().getStringList("tips");
        
        // get world blacklist
        blacklist = config.data().getStringList("config.world-blacklist");
        
        // get timer
        timer = config.data().getInt("config.tip-timer");
    }
    
    public void createTask() {
        // creates an async task
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(HelpMain.INSTANCE, this, 0, timer * 20);
    }
    
    public void deleteTask() {
        // cancel task.
        task.cancel();
        task = null;
    }
    
    @Override
    public void run() {
        // get a new tip
        String tip = randomTip();
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            // check the permission! // help.tips.send //
            if (!p.hasPermission("help.tips.send"))
                continue;
            
            // check for the blacklisted world
            String world = p.getWorld().getName();
            if (blacklist.contains(world)) continue;
            
            // send the tip
            p.sendMessage(tip);
        }
    }
    
    public String randomTip() {
        return tips.get(random());
    }
    
    public int size() {
        return tips.size();
    }
    
    public String getTip(int n) {
        return tips.get(n);
    }
    
    private int random() {
        return new Random(System.nanoTime()).nextInt(tips.size());
    }
    
}
