package dev.drake.dough.skins;

import java.net.URL;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

/**
 * A wrapper for player textures that uses the native Paper Profile API.
 * This replaces the legacy NMS/AuthLib implementation.
 * 
 * @author SlimefunNewHorizons
 */
public final class CustomGameProfile {

    private static final String PLAYER_NAME = "CS-CoreLib";

    private final UUID id;
    private final URL skinUrl;
    private final String texture;

    CustomGameProfile(@Nonnull UUID uuid, @Nullable String texture, @Nonnull URL url) {
        this.id = uuid;
        this.skinUrl = url;
        this.texture = texture;
    }

    public UUID getId() {
        return id;
    }

    public URL getSkinUrl() {
        return skinUrl;
    }

    public PlayerProfile toPlayerProfile() {
        PlayerProfile playerProfile = Bukkit.createProfile(this.id, PLAYER_NAME);
        if (this.texture != null) {
            playerProfile.setProperty(new ProfileProperty("textures", this.texture));
        }
        return playerProfile;
    }

    void apply(@Nonnull SkullMeta meta) {
        meta.setPlayerProfile(toPlayerProfile());
    }

    @Nullable
    public String getBase64Texture() {
        return this.texture;
    }
}
