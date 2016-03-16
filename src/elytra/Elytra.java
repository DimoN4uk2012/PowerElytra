package elytra;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import static org.bukkit.Sound.valueOf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Elytra extends JavaPlugin implements Listener{
    int version = 1;
    
    Map<String, Long> delay = new HashMap<>();
    int setDelay = 4;
    float velocity = 1.5F;
    
    boolean enablePowerParticle = true;
    boolean enablePowerSound = true;
    boolean enablePowerSoundReload = true;
    boolean enableFlyParticle = true;
    
    Particle particleElytra = Particle.valueOf("CLOUD");
    Particle particlePower = Particle.valueOf("FLAME");
    Sound soundPower = Sound.valueOf("ITEM_FIRECHARGE_USE");
    Sound soundReload = Sound.valueOf("BLOCK_FIRE_EXTINGUISH");
    
    float soundVolumePower = 1.0F;
    float soundVolumePowerReload = 1.0F;
    float soundVolumeElytra = 1.0F;
    
    float soundPichPower = 1.0F;
    float soundPichPowerReload = 1.0F;
    float soundPichElytra = 1.0F;
    
    int particleCountPower = 16;
    float particlePowerDx = 0.1F;
    float particlePowerDy = 0.1F;
    float particlePowerDz = 0.1F;
    float particlePowerSpeed = 0.15F;
    
    int particleCountElytra = 3;
    float particleFlyDx = 0.1F;
    float particleFlyDy = 0.1F;
    float particleFlyDz = 0.1F;
    float particleFlySpeed = 0.1F;
    
    @Override
    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info("[PowerElitra] Plugin - Enable!");
        reloadConfigElytra();
    }
    
    @Override
    public void onDisable(){
        Bukkit.getLogger().info("[PowerElitra] Plugin - Disable.");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String com = "elytra";
        if (sender.hasPermission("powerelytra.admin.reload")){
            if (args.length == 1 && command.getName().equals(com) && args[0].equals("reload")){
                this.reloadConfig();
                reloadConfigElytra();
                sender.sendMessage("[PowerElitra] Reloaded.");
                return true;
            }
        }
        return true;
    }
        
    private void reloadConfigElytra(){
        File cfgFile = new File(this.getDataFolder(), "config.yml");
        if (!cfgFile.exists()) {
            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();
            Bukkit.getLogger().info("[PowerElitra] Load Default Config File.");
        }else{
                File cfgFileOld = new File(this.getDataFolder(), "configOld-v" + this.getConfig().getInt("version") + ".yml");
                if (this.version != this.getConfig().getInt("version")){
                    cfgFile.renameTo(cfgFileOld);
                    cfgFile.delete();
                    if (!cfgFile.exists()) {
                        this.getConfig().options().copyDefaults(true);
                        this.saveDefaultConfig();
                        Bukkit.getLogger().info("[PowerElitra] Load Default Config File.");
                    }
                    Bukkit.getLogger().info("[PowerElitra] Load Default Config File. Config version != " + this.version);
                }
            }
        this.reloadConfig();
        if (cfgFile.exists()) {
            FileConfiguration c = this.getConfig();
            setDelay = c.getInt("delay");
            velocity = (float) c.getDouble("velocity");
            enablePowerParticle = c.getBoolean("power.particle.enable");
            particlePower = Particle.valueOf(c.getString("power.particle.type"));
            particleCountPower = c.getInt("power.particle.count");
            particlePowerDx = (float) c.getDouble("power.particle.dx");
            particlePowerDy = (float) c.getDouble("power.particle.dy");
            particlePowerDz = (float) c.getDouble("power.particle.dz");
            particlePowerSpeed = (float) c.getDouble("power.particle.speed");
            enablePowerSound = c.getBoolean("power.sound.enable");
            soundPower = Sound.valueOf(c.getString("power.sound.type"));
            soundVolumePower = (float) c.getDouble("power.sound.volume");
            soundPichPower = (float) c.getDouble("power.sound.pich");
            enablePowerSoundReload = c.getBoolean("power.soundReload.enable");
            soundReload = Sound.valueOf(c.getString("power.soundReload.type"));
            soundVolumePowerReload = (float) c.getDouble("power.soundReload.volume");
            soundPichPowerReload = (float) c.getDouble("power.soundReload.pich");
            
            enableFlyParticle = c.getBoolean("fly.particle.enable");
            particleElytra = Particle.valueOf(c.getString("fly.particle.type"));
            particleCountElytra = c.getInt("fly.particle.count");
            particleFlyDx = (float) c.getDouble("fly.particle.dx");
            particleFlyDy = (float) c.getDouble("fly.particle.dy");
            particleFlyDz = (float) c.getDouble("fly.particle.dz");
            particleFlySpeed = (float) c.getDouble("fly.particle.speed");
            
            Bukkit.getLogger().info("[PowerElitra] Config Reloaded.");
        }
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent move) {
        Player player = move.getPlayer();
        if (player.hasPermission("powerelytra.player.use")){
            ItemStack chestItem = player.getInventory().getChestplate();
            if (chestItem != null && chestItem.getType().equals(Material.ELYTRA) && !player.isOnGround()) {
                if (this.delay.get(player.getName()) == null || this.delay.get(player.getName()) <= System.currentTimeMillis()) {
                    if (player.isSneaking()){
                        this.delay.put(player.getName(), System.currentTimeMillis() + (this.setDelay * 1000L));
                        Vector pv = player.getLocation().getDirection();
                        Vector v = pv.multiply(velocity);
                        player.setVelocity(v);
                        if (enablePowerSound){
                            player.getWorld().playSound(player.getLocation(), this.soundPower, this.soundVolumePower, this.soundPichPower);
                        }
                        if (enablePowerParticle){
                            player.getWorld().spawnParticle(this.particlePower, player.getLocation(), this.particleCountPower, this.particlePowerDx, this.particlePowerDy, this.particlePowerDz, this.particlePowerSpeed);
                        }
                    }
                    if (this.delay.get(player.getName()) != null && this.setDelay != 0){
                        if (this.delay.get(player.getName())/1000 == System.currentTimeMillis()/1000){
                            if (enablePowerSoundReload){
                                player.getWorld().playSound(player.getLocation(), this.soundReload, this.soundVolumeElytra, this.soundPichElytra);
                            }
                            this.delay.remove(player.getName());
                        }
                    }
                }else {
                        if (enableFlyParticle){
                            player.getWorld().spawnParticle(this.particleElytra, player.getLocation(), this.particleCountElytra, this.particleFlyDx, this.particleFlyDy, this.particleFlyDz, this.particleFlySpeed);
                        }
                    }
            }
        }
    }
    
    
}
