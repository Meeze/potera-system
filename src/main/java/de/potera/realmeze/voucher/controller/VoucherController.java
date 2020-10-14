package de.potera.realmeze.voucher.controller;

import com.google.gson.internal.$Gson$Preconditions;
import de.potera.realmeze.voucher.model.Voucher;
import de.potera.realmeze.voucher.model.content.Content;
import de.potera.realmeze.voucher.model.content.MoneyContent;
import de.potera.realmeze.voucher.model.content.SpawnerContent;
import de.potera.realmeze.voucher.service.VoucherService;
import de.potera.teamhardcore.utils.ItemBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class VoucherController {

    private VoucherService voucherService;

    public Voucher<? extends Content> getVoucher(UUID voucherId) {
        return voucherService.load(voucherId);
    }

    public void redeem(UUID voucherId, Player player){
        Voucher<? extends Content> voucher = getVoucher(voucherId);
        voucher.getContent().reward(player);
        getVoucherService().delete(voucher);
    }

    public ItemStack create(Content content) {
        if(content instanceof MoneyContent) {
            Voucher<MoneyContent> moneyVoucher = new Voucher<>();
            moneyVoucher.setContent((MoneyContent) content);
            Voucher<? extends Content> saved = voucherService.save(moneyVoucher);
            return createVoucherItem(saved);
        } else if (content instanceof SpawnerContent) {
            Voucher<SpawnerContent> spawnerVoucher = new Voucher<>();
            spawnerVoucher.setContent((SpawnerContent) content);
            Voucher<? extends Content> saved = voucherService.save(spawnerVoucher);
            return createVoucherItem(saved);
        }
        return null;
    }

    private ItemStack createVoucherItem(Voucher<? extends Content> voucher) {
        if(voucher.getContent() instanceof MoneyContent) {
            MoneyContent moneyContent = (MoneyContent) voucher.getContent();
            ItemBuilder builder = new ItemBuilder(Material.PAPER);
            builder.setDisplayName("$ Amount: " + moneyContent.getAmount()).setLore(voucher.getVoucherId().toString()).setAmount(1).setGlow();
            return builder.build();
        } else if (voucher.getContent() instanceof SpawnerContent) {
            SpawnerContent spawnerContent = (SpawnerContent) voucher.getContent();
            ItemBuilder builder = new ItemBuilder(Material.MOB_SPAWNER);
            builder.setDisplayName("Spawner Amount: " + spawnerContent.getAmount() + " Spawner Type: " + spawnerContent.getType()).setLore(voucher.getVoucherId().toString()).setAmount(1).setGlow();
            return builder.build();
        }
        Bukkit.broadcastMessage("hibernate cant do this for me if this happens");
        return null;
    }

}
