package net.mayateck.VillagerVendor;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBookHandler {
	public static void givePlayerItemBook(Player p){
		ItemStack book = new ItemStack(Material.EMERALD, 1);
		ItemMeta data = book.getItemMeta();
		data.setDisplayName("§6VillagerVendor §7Shop Item");
		data.setLore(Arrays.asList("ItemID: 0", "Damage Value: 0", "Amount: 0", "Cost: 0"));
		// TODO Enchantments and NBT Tags
		if (p.getItemInHand()==new ItemStack(Material.AIR)){
			p.setItemInHand(book);
		} else {
			p.getInventory().addItem(book);
		}
	}
}
