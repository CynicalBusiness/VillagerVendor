package net.mayateck.VillagerVendor;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Chest;
// Giant import list. :l

@SuppressWarnings("unused")
public class VendorHandler implements Listener {
	private VillagerVendor plugin;
	public VendorHandler(VillagerVendor plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	public void interactWithVendor(String vsrc, Player plyr){
		// Get vendor data.
		String name = plugin.getVendorsList().getString(vsrc+".name");
		int chestX = plugin.getVendorsList().getInt(vsrc+".chest.x");
		int chestY = plugin.getVendorsList().getInt(vsrc+".chest.y");
		int chestZ = plugin.getVendorsList().getInt(vsrc+".chest.z");
		World world = plyr.getLocation().getWorld();
		Block block = new Location(world, chestX, chestY, chestZ).getBlock();
		if (block.getState() instanceof Chest){
			// Make sure it's a chest. If not, we have a problem.
			Chest chest = (Chest) block.getState();
			ItemStack[] inv = chest.getInventory().getContents();
			Inventory vendorInv = plugin.getServer().createInventory(null, 9*3, name); // Too lazy to do math. :3
			// Null holder deletes inventory when closed, I think.
			vendorInv.setContents(inv);
			plyr.openInventory(vendorInv);
			if (plyr.hasPermission("villagervendor.use.buy")){
				String msg = "";
				List<String> messages = plugin.getConfig().getStringList("settings.vendormessages");
				List<String> vendors = plugin.getVendorsList().getStringList("vendorlist");
				int randMsg = (int) Math.floor(Math.random() * messages.size());
				msg = messages.get(randMsg);
				plyr.sendMessage(VillagerVendor.head+name+": "+msg);
				if (!(vendors.contains(name))){
					vendors.add(name);
					plugin.getVendorsList().set("vendorlist", vendors);
					plugin.saveVendorsList();
				}
			} else {
				plyr.sendMessage(VillagerVendor.head+name+": "+"You can take a look, I can't sell you anything though.");
			}
		} else {
			plyr.sendMessage(VillagerVendor.head+"This vendor's chest is missing or invalid. Interact failed.");
		}
	}
	
	@EventHandler
	public void onVendorInventoryEdit(InventoryClickEvent evt){
		Inventory inv = evt.getInventory();
		Player plyr = (Bukkit.getServer().getPlayer(evt.getWhoClicked().getName()));
		List<String> vendors = plugin.getVendorsList().getStringList("vendorlist");
		if (inv.getHolder()==null && vendors.contains(inv.getName())){
			/* Let's make sure it's a fake inventory that belongs to a vendor.
			 * The vendors list is modified by the interactWithVendor() method.
			 * First time opening the vendor's inventory stores is in the vendor data list requested here.
			 * Also, the inventory mentioned before has a null holder. So this should match a vendor.
			 */
			if (plyr.hasPermission("villagervendor.use.buy")){
				
			} else {
				plyr.sendMessage(VillagerVendor.head+"Sorry! You don't have permission to buy from this vendor!");
				evt.setCancelled(true);
			}
		}
	}
	
	
	@EventHandler
	public void onVendorInteract(EntityDamageByEntityEvent evt){
		Entity damager = evt.getDamager();
		Entity vendor = evt.getEntity();
		// For getting the EntityId.
		if (damager instanceof Player){
			Player plyr = (Player)damager;
			//plugin.getLogger().info("[DEBUG] "+plyr.getName()+" damaged "+vendor.getType()+" with "+plyr.getItemInHand().getTypeId());
			if((plyr.getItemInHand().getTypeId()==plugin.getConfig().getInt("settings.entityIDTool")) && (plyr.hasPermission("villagervendor.general.getid"))){
				plyr.sendMessage(VillagerVendor.head+"Entity ID: "+vendor.getEntityId());
			}
		}
		if (damager instanceof Player && vendor.getType()==EntityType.VILLAGER){
			// Making sure it's not some random damage event. We want when a player punches a villager.
			plugin.getLogger().info("[DEBUG] Fired EntityDamageByEntityEvent from Player->Villager");
			if (plugin.getVendorsList().contains("vendors.vendor_"+vendor.getEntityId())){
				// Check if it's actually a vendor. If so, cancel the damage and call a method. Otherwise, let it happen.
				Player plyr = (Player)damager;
				evt.setCancelled(true);
				plugin.getLogger().info("[DEBUG] Player holding "+plyr.getItemInHand().getTypeId());
				if (plyr.hasPermission("villagervendor.use.view")){
					if ((plyr.getItemInHand().getTypeId()!=plugin.getConfig().getInt("settings.debugID")) || !(plyr.hasPermission("villagervendor.general.debug"))){
						String vsrc = "vendors.vendor_"+vendor.getEntityId();
						this.interactWithVendor(vsrc, plyr);
					} else {
						plyr.sendMessage(VillagerVendor.head+"VENDOR_"+vendor.getEntityId()+".");
						plyr.sendMessage(VillagerVendor.head+"For the time being, please edit vendors manually.");
						plyr.sendMessage(VillagerVendor.head+"To remove, type "+ChatColor.RED+"/vendor delete [id]"+ChatColor.RESET+". (CANNOT BE UNDONE!)");
					}
				} else {
					plyr.sendMessage(VillagerVendor.head+"Sorry! You don't have permission to use this vendor.");
				}
			}
		}
	}

}
