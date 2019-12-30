package dev.ckitty.mc.helpme.main;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ConfigPair {
    
    private File file;
    private FileConfiguration config;
    
    public ConfigPair setFile(File parent, String name) {
        this.file = new File(parent, name);
        return this;
    }
    
    public ConfigPair setFile(File file) {
        this.file = file;
        return this;
    }
    
    public ConfigPair pack() {
        File parent = file.getParentFile();
        if (!parent.exists()) parent.mkdirs();
        
        // create a config object
        clear();
        
        if(!file.exists()) {
            // file doesn't exists! create!
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return this; // could not create a file :C
            }
        } else {
            // file exists! load!
            reload();
        }
        
        return this;
    }
    
    public void reload() {
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void clear() {
        config = new YamlConfiguration();
    }
    
    // getters and setters NO!
    
    public FileConfiguration data() {
        return config;
    }
    
    public Set<String> dataKeys(String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if(section == null) return new HashSet<>();
        return section.getKeys(false);
    }
    
}
