package ru.kitburg.spawn;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Spawn {
    void spawn(Player player){
        Location spawnloc = player.getWorld().getSpawnLocation();
        player.teleport(spawnloc);
        player.sendMessage("Вы были телепортированы на "+ ChatColor.DARK_PURPLE +"спавн!");
    }
}
