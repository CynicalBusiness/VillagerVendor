package net.mayateck.VillagerVendor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class VillagerVendor extends JavaPlugin implements Listener{
	Plugin plugin = this;
	public static String head = ChatColor.DARK_GRAY+"["+ChatColor.DARK_AQUA+"VillagerVendor"+ChatColor.DARK_GRAY+"] "+ChatColor.RESET;
	
	@Override
	public void onEnable(){
		this.getLogger().info("#======# VillagerVendor by Wehttam664 #======#");
		this.getLogger().info("Initializing...");
			new VendorHandler(this);
		this.getLogger().info("Requesting disk response...");
			this.saveDefaultConfig();
			this.saveDefaultVendorsList();
		this.getLogger().info("Ready!");
		this.getLogger().info("#============================================#");
	}
	
	@Override
	public void onDisable(){
		this.getLogger().info("#======# VillagerVendor by Wehttam664 #======#");
		this.getLogger().info("Wrapping up accessors...");
		// TODO: Wrap-up.
		this.getLogger().info("Shut-down complete.");
		this.getLogger().info("#============================================#");
	}
	private FileConfiguration vendors = null;
	private static File vendorsFile = null;
	
	public boolean onCommand(CommandSender s, Command cmd, String l, String[] args){
		if (cmd.getName().equalsIgnoreCase("vendor")){
			if (args.length==0){
				// TODO Plugin information.
				return true;
			} else if(args.length==1) {
				if (args[0].equalsIgnoreCase("reload")){
					if (s.hasPermission("villagervendor.admin.reloadconfig")){
						this.reloadConfig();
						s.sendMessage(VillagerVendor.head+"Reloaded confiuration.");
						return true;
					} else {
						s.sendMessage(VillagerVendor.head+"You don't have permission for that, sorry.");
						return true;
					}
				} else if(args[0].equalsIgnoreCase("reloadvendors")){
					if (s.hasPermission("villagervendor.admin.reloadconfig")){
						this.reloadVendorsList();
						s.sendMessage(VillagerVendor.head+"Reloaded vendor list.");
						return true;
					} else {
						s.sendMessage(VillagerVendor.head+"You don't have permission for that, sorry.");
						return true;
					}
				} else if(args[0].equalsIgnoreCase("help")){
					s.sendMessage(VillagerVendor.head+"Need to write the help.");
					return true;
				} else if(args[0].equalsIgnoreCase("debug")){
					boolean test = this.getVendorsList().contains("vendors.*.name");
					s.sendMessage(""+test);
					return true;
				} else {
					s.sendMessage(VillagerVendor.head+"Invalid argument. See '/vendor help'.");
					return true;
				}
			} else {
				s.sendMessage(VillagerVendor.head+"Invalid argument number. See '/vendor help'.");
				return true;
			}
		}
		return false;
	}
	
	public void reloadVendorsList() {
		if (vendorsFile == null) {
			vendorsFile = new File(plugin.getDataFolder(), "vendors.yml");
		}
	    vendors = YamlConfiguration.loadConfiguration(vendorsFile);
	    InputStream vendorConfigStream = plugin.getResource("vendors.yml");
	    if (vendorConfigStream != null) {
	        YamlConfiguration vendorConfig = YamlConfiguration.loadConfiguration(vendorConfigStream);
	        vendors.setDefaults(vendorConfig);
	    }
	}
	
	public FileConfiguration getVendorsList() {
		if (vendors == null) {
	    	this.reloadVendorsList();
		}
		return vendors;
	}
	
	public void saveVendorsList() {
	    if (vendors == null || vendorsFile == null) {
	    	return;
	    }
	    try {
	        getVendorsList().save(vendorsFile);
	    } catch (IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, "Could not save config to " + vendorsFile, ex);
	    }
	}
	
	public void saveDefaultVendorsList() {
	    if (vendorsFile == null) {
	        vendorsFile = new File(plugin.getDataFolder(), "vendors.yml");
	    }
	    if (!vendorsFile.exists()) {
	         plugin.saveResource("vendors.yml", false);
	    }
	}
}
