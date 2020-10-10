package de.potera.teamhardcore.others.crates;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class CrateOpening {

    private final Player player;
    private final BaseCrate crate;

    private final int[] slots = {9, 10, 11, 12, 13, 14, 15, 16, 17};

    private final List<ContentPiece> rewards;
    private final List<BukkitTask> tasks;

    private Inventory inventory;

    private boolean skip;

    public CrateOpening(Player player, BaseCrate crate, boolean skip) {
        this.rewards = new ArrayList<>();
        this.tasks = new ArrayList<>();

        this.player = player;
        this.crate = crate;

        calculateRewards();
        if (skip) {
            giveReward();
        } else {
            openInventory();
            startItemMoving();
        }

    }

    private void calculateRewards() {
        for (ContentPiece content : this.crate.getAddon().getCrateContent()) {
            double chance = this.crate.getAddon().getPercentChance(content);
            for (int i = 0; i < chance; i++) {
                this.rewards.add(content);
            }
        }
    }

    private void openInventory() {
        this.inventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "Crate-Opening");

        IntStream.range(0, 9).forEach((value -> {
            this.inventory.setItem(value,
                    new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayName(" ").build());
            this.inventory.setItem(value + 18,
                    new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayName(" ").build());
        }));

        this.inventory.setItem(4, new ItemBuilder(Material.HOPPER).setDisplayName(" ").build());

        this.player.openInventory(inventory);
    }

    private void giveReward() {
        ContentPiece piece = this.rewards.get(new Random().nextInt(this.rewards.size()));
        if (piece == null) return;

        Main.getInstance().getCrateManager().getPlayersInOpening().remove(player);

        piece.onWin(player);
    }

    private void startItemMoving() {
        this.tasks.add(new BukkitRunnable() {
            int counter = 25;
            int timeout = 0;

            @Override
            public void run() {
                if (timeout != 0) {
                    timeout--;
                    return;
                }

                this.counter--;

                IntStream.range(1, slots.length).forEach(value -> {
                    if (CrateOpening.this.inventory.getItem(slots[value]) != null)
                        CrateOpening.this.inventory.setItem((slots[value] - 1),
                                CrateOpening.this.inventory.getItem(slots[value]));
                });

                ContentPiece piece = CrateOpening.this.rewards.get(
                        new Random().nextInt(CrateOpening.this.rewards.size() - 1));

                CrateOpening.this.rewards.add(piece);

                CrateOpening.this.inventory.setItem(17, piece.getDisplayItem());
                CrateOpening.this.player.playSound(CrateOpening.this.player.getLocation(), Sound.CLICK, 1.0F,
                        1.0F);


                if (this.counter <= 10 && this.counter > 5) {
                    this.timeout = 1;
                } else if (this.counter <= 5 && this.counter >= 2) {
                    this.timeout = 2;
                } else if (this.counter == 1) {
                    this.timeout = 4;
                }

                if (this.counter == 0) {
                    cancel();
                    CrateOpening.this.startOpening();
                }

            }
        }.runTaskTimer(Main.getInstance(), 0L, 2L));
    }

    private void startOpening() {
        this.tasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                if (CrateOpening.this.player == null || !CrateOpening.this.player.isOnline()) return;

                if (CrateOpening.this.inventory.getItem(13) == null) {
                    CrateOpening.this.player.closeInventory();
                    return;
                }

                ContentPiece piece = CrateOpening.this.rewards.get(CrateOpening.this.rewards.size() - 5);
                piece.onWin(CrateOpening.this.player);

                Main.getInstance().getCrateManager().getPlayersInOpening().remove(player);
                CrateOpening.this.tasks.add(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (CrateOpening.this.player.getOpenInventory().getTopInventory().equals(
                                CrateOpening.this.inventory))
                            CrateOpening.this.player.closeInventory();
                    }
                }.runTaskLater(Main.getInstance(), 30L));
            }
        }.runTaskLater(Main.getInstance(), 10L));
    }

    public Player getPlayer() {
        return player;
    }

    public BaseCrate getCrate() {
        return crate;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void cancelAllTasks() {
        for (BukkitTask task : this.tasks)
            task.cancel();
    }

}
