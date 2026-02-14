package net.ragdot.gestaltresonance.client;

import net.minecraft.client.MinecraftClient;
import net.ragdot.gestaltresonance.network.FuturamaRecordingPayload;
import net.ragdot.gestaltresonance.network.FuturamaSyncPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FuturamaClientManager {
    private static final Map<UUID, EntityRecording> RECORDINGS = new HashMap<>();
    private static boolean recording = false;

    public static void handleRecording(FuturamaRecordingPayload payload) {
        recording = payload.recording();
    }

    public static boolean isRecording() {
        return recording;
    }

    public static void handleSync(FuturamaSyncPayload payload) {
        RECORDINGS.clear();
        for (Map.Entry<UUID, java.util.List<FuturamaSyncPayload.GhostFrame>> entry : payload.entityRecordings().entrySet()) {
            RECORDINGS.put(entry.getKey(), new EntityRecording(entry.getValue()));
        }
    }

    public static void tick() {
        RECORDINGS.entrySet().removeIf(entry -> {
            entry.getValue().tick++;
            return entry.getValue().tick >= entry.getValue().frames.size();
        });
    }

    public static GhostData getGhostData(UUID entityId) {
        EntityRecording recording = RECORDINGS.get(entityId);
        if (recording == null) return null;
        return new GhostData(recording.frames, recording.tick);
    }

    private static class EntityRecording {
        java.util.List<FuturamaSyncPayload.GhostFrame> frames;
        int tick;

        EntityRecording(java.util.List<FuturamaSyncPayload.GhostFrame> frames) {
            this.frames = frames;
            this.tick = 0;
        }
    }

    public record GhostData(java.util.List<FuturamaSyncPayload.GhostFrame> frames, int currentTick) {
        public FuturamaSyncPayload.GhostFrame getFutureFrame(int ticksAhead) {
            int target = currentTick + ticksAhead;
            if (target >= frames.size()) return null;
            return frames.get(target);
        }
    }
}
