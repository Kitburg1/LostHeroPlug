package ru.kitburg.spawn;

import ru.kitburg.spawn.BookInteractionListener;
import ru.kitburg.spawn.BookShareCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;


public final class LostHeroPlug extends JavaPlugin implements Listener {
    File homes;
    HashMap<String, ArrayList<Double>> players = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private static LostHeroPlug instance;

    @SuppressWarnings("unchecked")
    @Override
    public void onEnable() {
        getCommand("spawn").setExecutor(this);
        getCommand("sethome").setExecutor(this);
        getCommand("home").setExecutor(this);
        getCommand("helplh").setExecutor(this);
        getCommand("rtp").setExecutor(this);
        getCommand("writebook").setExecutor(new BookShareCommand());
        getServer().getPluginManager().registerEvents(new BookInteractionListener(), this);
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
    public static LostHeroPlug getInstance() {
        return instance;
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
            System.out.println("❌Возможно только от игрока!");
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

    public void rtps(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long cooldownTime = 60 * 1000; // 60 секунд в мс

        // Проверка кулдауна
        if (cooldowns.containsKey(uuid)) {
            long lastUsed = cooldowns.get(uuid);
            long timePassed = now - lastUsed;

            if (timePassed < cooldownTime) {
                long secondsLeft = (cooldownTime - timePassed) / 1000;
                player.sendMessage("⏳ Подожди " + secondsLeft + " сек. перед телепортацией.");
                return;
            }
        }

        // Генерация координат
        Random random = new Random();
        int min = 2000;
        int max = 4500;

        int x = random.nextInt(max - min + 1) + min;
        int z = random.nextInt(max - min + 1) + min;

        World world = player.getWorld();
        int y = world.getHighestBlockYAt(x, z) + 1;

        Location location = new Location(world, x, y, z);

        // Проверка границ мира
        if (!world.getWorldBorder().isInside(location)) {
            player.sendMessage("🚫 Место за границей мира. Попробуй снова.");
            return;
        }

        // Телепортация и установка кулдауна
        player.teleport(location);
        cooldowns.put(uuid, now);
        player.sendMessage("✅ Телепортация в: X=" + x + ", Y=" + y + ", Z=" + z);
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