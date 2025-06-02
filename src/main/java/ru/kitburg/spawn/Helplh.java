package ru.kitburg.spawn;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Helplh {
    void helplh(Player player){
        player.sendMessage("Все команды "+ ChatColor.AQUA+"Lost"+ChatColor.RED+"Hero");
        player.sendMessage("Чтобы телепортироваться на спавн "+ChatColor.AQUA+"/spawn");
        player.sendMessage("Чтобы создать точку дома "+ChatColor.AQUA+"/sethome");
        player.sendMessage("Чтобы телепортироваться на точку дома "+ChatColor.AQUA+"/home");
        player.sendMessage("Чтобы телепортироваться в случайную точку "+ChatColor.AQUA+"/rtp");
        player.sendMessage("Удачной игры на "+ChatColor.AQUA+"Lost"+ChatColor.RED+"Hero");

    }
}
