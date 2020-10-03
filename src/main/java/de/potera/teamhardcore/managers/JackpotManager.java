package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.others.gamble.jackpot.JackpotGame;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;

public class JackpotManager {

    private JackpotGame jackpotGame;

    public JackpotManager() {
        this.jackpotGame = null;
    }

    public void startJackpotGame(long maxBet) {
        if (this.jackpotGame != null) return;
        this.jackpotGame = new JackpotGame(maxBet);
        this.jackpotGame.goToPhase(0);

        Bukkit.broadcastMessage(StringDefaults.JACKPOT_PREFIX + "§7Eine neue §a§lJackpot §7Runde ist gestartet!");
        Bukkit.broadcastMessage(
                StringDefaults.JACKPOT_PREFIX + "§7Max. Betrag§8: §e" + Util.formatNumber(maxBet) + " Münzen");
    }

    public void stopJackpotGame() {
        if (this.jackpotGame == null) return;
        this.jackpotGame.cancelTask();
        this.jackpotGame = null;
    }

    public JackpotGame getJackpotGame() {
        return jackpotGame;
    }
}
