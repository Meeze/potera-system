package de.potera.fakemobs.util;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import de.potera.fakemobs.FakeMobsPlugin;
import de.potera.fakemobs.merchant.ReflectionUtils;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class SkinQueue extends Thread {
    private final Queue<SkinEntry> queue = new LinkedBlockingQueue<SkinEntry>();

    public SkinQueue() {
        super("FakeMobs skin queue");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            blockThread();
            while (!this.queue.isEmpty()) {
                SkinEntry entry = this.queue.poll();
                if (FakeMobsPlugin.getMob(entry.getMob().getId()) != entry.getMob()) continue;


                Object nmsProfile = ReflectionUtils.searchUUID(entry.getSkinName());
                WrappedGameProfile profile = nmsProfile == null ? new WrappedGameProfile((UUID) null, entry.getSkinName()) : WrappedGameProfile.fromHandle(nmsProfile);

                if (profile.getProperties().isEmpty() && profile.isComplete())
                    profile = WrappedGameProfile.fromHandle(ReflectionUtils.fillProfileProperties(profile.getHandle()));
                if (profile == null) continue;

                entry.getMob().setPlayerSkin(profile.getProperties());
                entry.getMob().updateCustomName();
            }
        }
    }

    public synchronized void addToQueue(FakeMob mob, String skinName) {
        this.queue.add(new SkinEntry(mob, skinName));
        this.notify();
    }

    private synchronized void blockThread() {
        try {
            while (this.queue.isEmpty()) {
                this.wait();
            }
        } catch (Exception ex) {
        }
    }

    private static class SkinEntry {
        private final FakeMob mob;
        private final String skinName;

        public SkinEntry(FakeMob mob, String skinName) {
            this.mob = mob;
            this.skinName = skinName;
        }

        public FakeMob getMob() {
            return this.mob;
        }

        public String getSkinName() {
            return this.skinName;
        }
    }

}
