package me.sores.arrow.kit.wrapper;

import me.sores.arrow.kit.Kit;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Created by sores on 4/20/2021.
 */
public interface IPlayerInteractEntity extends WrapperItem {

    void onPlayerInteractEntity(Kit kit, PlayerInteractEntityEvent event);

}
