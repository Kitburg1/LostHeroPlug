package ru.kitburg.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class BookInteractionListener implements Listener {

    private final Set<UUID> readingPlayers = new HashSet<>();
    private final Map<UUID, String> bookTitles = new HashMap<>();

    @EventHandler
    public void onBookSigned(PlayerEditBookEvent event) {
        if (!event.isSigning()) return;

        Player author = event.getPlayer();
        BookMeta signedMeta = event.getNewBookMeta();
        String title = signedMeta.getTitle();
        if (title == null) return;

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        book.setItemMeta(signedMeta);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(author)) continue;

            ItemStack copy = book.clone();
            player.getInventory().addItem(copy);
            readingPlayers.add(player.getUniqueId());
            bookTitles.put(player.getUniqueId(), title);

            player.sendMessage("📘 " + author.getName() + " поделился книгой: §e" + title);
        }

        author.sendMessage("✅ Книга отправлена другим игрокам.");
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        UUID uuid = player.getUniqueId();
        if (!readingPlayers.contains(uuid)) return;

        String expectedTitle = bookTitles.get(uuid);
        if (expectedTitle == null) return;

        Bukkit.getScheduler().runTaskLater(LostHeroPlug.getInstance(), () -> {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() != Material.WRITTEN_BOOK) continue;

                BookMeta meta = (BookMeta) item.getItemMeta();
                if (meta != null && expectedTitle.equals(meta.getTitle())) {
                    player.getInventory().remove(item);
                    player.sendMessage("📕 Ты прочитал книгу — она исчезла.");
                    break;
                }
            }

            readingPlayers.remove(uuid);
            bookTitles.remove(uuid);
        }, 5L);
    }
}

