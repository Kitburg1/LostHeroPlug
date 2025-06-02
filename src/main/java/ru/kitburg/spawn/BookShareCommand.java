package ru.kitburg.spawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.UUID;

public class BookShareCommand implements CommandExecutor {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_MILLIS = 120_000; // 2 минуты

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игрок может использовать эту команду.");
            return true;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (cooldowns.containsKey(uuid)) {
            long lastUsed = cooldowns.get(uuid);
            long timeLeft = COOLDOWN_MILLIS - (now - lastUsed);

            if (timeLeft > 0) {
                long seconds = timeLeft / 1000;
                player.sendMessage("⏳ Подожди ещё " + seconds + " сек. перед повторным использованием.");
                return true;
            }
        }

        // Выдать книгу
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta != null) {
            meta.setTitle("Черновик");
            meta.setAuthor(player.getName());
            book.setItemMeta(meta);
        }

        player.getInventory().addItem(book);
        player.sendMessage("📖 Тебе выдана книга. Напиши что-то и подпиши, чтобы отправить другим.");

        // Установить кулдаун
        cooldowns.put(uuid, now);
        return true;
    }
}


