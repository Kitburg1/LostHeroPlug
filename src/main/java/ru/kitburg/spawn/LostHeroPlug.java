package ru.kitburg.spawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public final class LostHeroPlug extends JavaPlugin implements Listener {
    File homes;
    HashMap<String, ArrayList<Double>> players = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public void onEnable() {
        getCommand("spawn").setExecutor(this);
        getCommand("sethome").setExecutor(this);
        getCommand("home").setExecutor(this);
        getCommand("helplh").setExecutor(this);
        getCommand("rtp").setExecutor(this);
        updateTabList();
        getServer().getPluginManager().registerEvents(this, this);

        homes = new File("homes.txt");
        if (!homes.exists()) {
            System.out.println("File not exist!");
            try {
                homes.createNewFile();
                homes = new File("homes.txt");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (homes.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(homes))) {
                players = (HashMap<String, ArrayList<Double>>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("OnEnable");
            }
        }
    }
    public static void updateTabList() {

        String header = "§6Добро пожаловать на §cLost§bHero!"; // Use color codes (§)
        String footer = "§bПроводите своё время с §6интересом!";
        Bukkit.getOnlinePlayers().forEach(playersr -> {
            playersr.setPlayerListHeader(header);
            playersr.setPlayerListFooter(footer);
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateTabList(); // Update on player join as well
    }

    @Override
    public void onDisable() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(homes))) {
            oos.writeObject(players);
            System.out.println("File is saved!");
        } catch (IOException e) {
            System.out.println("OnDisable");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        Spawn spawn = new Spawn();
        Helplh helplh = new Helplh();

        if(!(sender instanceof Player)){
            System.out.println("Возможно только от игрока!");
            return false;
        }
        if (command.getName().toLowerCase().equals("spawn")){
            spawn.spawn(player);
            return true;
        }
        if (command.getName().toLowerCase().equals("sethome")){
            sethome(player);
            return true;
        }
        if (command.getName().toLowerCase().equals("home")){
            home(player);
            return true;
        }
        if (command.getName().toLowerCase().equals("helplh")){
            helplh.helplh(player);
            return true;
        }
        if (command.getName().toLowerCase().equals("rtp")){
            rtps(player);
            return true;
        }
        return true;
    }



    void sethome(Player player){
        Location loc = player.getLocation();
        ArrayList<Double> coords = new ArrayList<>();
        coords.add(loc.getX());
        coords.add(loc.getY());
        coords.add(loc.getZ());
        players.put(player.getName(), coords);
        player.sendMessage("Вы поставили точку "+ ChatColor.GREEN +"дома!");
    }

    void rtps(Player player){
        Random random = new Random();
        int max = 4500;
        int min = 2000;
        int y = 66;
        int z =random.nextInt(max -min + 1) + min;
        int x =random.nextInt(max -min + 1) + min;
        Location olc = new Location(player.getWorld(), z, y, x);
        player.teleport(olc);
    }

    void home(Player player){
        ArrayList<Double> coords = players.get(player.getName());
        Location loc = new Location(player.getWorld(), coords.get(0), coords.get(1), coords.get(2));
        player.sendMessage("Вы были телепортированы на "+ ChatColor.GREEN +"точку дома!");

        if (loc != null){
            player.teleport(loc);
        }
        else {
            player.sendMessage("Пропишите"+ ChatColor.RED +" /sethome!");
        }
        }

    }