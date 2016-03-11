package elytra;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import static org.bukkit.Sound.valueOf;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Elytra extends JavaPlugin implements Listener{
    
    Map<String, Long> delay = new HashMap<>();
    int setDelay = 4;
    
    Particle particleElytra = Particle.valueOf("CLOUD");
    Particle particlePower = Particle.valueOf("FLAME");
    Sound soundPower = Sound.valueOf("ITEM_FIRECHARGE_USE");
    Sound soundReload = Sound.valueOf("BLOCK_FIRE_EXTINGUISH");
    
    int particleCountPower = 16;
    int particleCountElytra = 3;
    
    float soundVolumePower = 1;
    float soundVolumeElytra = 1;
    
    float soundPichPower = 1;
    float soundPichElytra = 1;
    
    @Override
    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info("[PowerElitra] Plugin - Enable!");
    }
    
    @Override
    public void onDisable(){
        Bukkit.getLogger().info("[PowerElitra] Plugin - Disable.");
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent move) {
        Player player = move.getPlayer();
        ItemStack chestItem = player.getInventory().getChestplate();
        if (chestItem != null && chestItem.getType().equals(Material.ELYTRA) && !player.isOnGround()) {
            if (this.delay.get(player.getName()) == null || this.delay.get(player.getName()) <= System.currentTimeMillis()) {
                if (player.isSneaking()){
                    this.delay.put(player.getName(), System.currentTimeMillis() + (this.setDelay * 1000L));
                    Vector pv = player.getLocation().getDirection();
                    Vector v = pv.multiply(1.5);
                    player.setVelocity(v);
                    player.getWorld().playSound(player.getLocation(), this.soundPower, this.soundVolumePower, this.soundPichPower);
                    player.getWorld().spawnParticle(this.particlePower, player.getLocation(), this.particleCountPower, 0.1, 0.1, 0.1, 0.15);
                }
                if (this.delay.get(player.getName()) != null){
                    if (this.delay.get(player.getName())/1000 == System.currentTimeMillis()/1000){
                        player.getWorld().playSound(player.getLocation(), this.soundReload, this.soundVolumeElytra, this.soundPichElytra);
                        this.delay.remove(player.getName());
                    }
                }
            }else {
                    player.getWorld().spawnParticle(this.particleElytra, player.getLocation(), this.particleCountElytra, 0.1, 0.1, 0.1, 0.1);
                }
        }
 
    }
    
    
}
