import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("ALL")
public class Main extends JavaPlugin {
    private Path path = Paths.get("function.f");
    private Location o;
    private String yF;
    private String zF;
    private double var1 = 30;
    private final double var2 = 0.125;
    private final double li = 5;
    private final int n = 1;
    private final Particle p1 = Particle.REDSTONE;
    private final Particle p2 = Particle.BARRIER;
    private int level = 1;


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("f")) {
            if (args.length == 0) {
                sender.sendMessage("f o");
                sender.sendMessage("f start");
                sender.sendMessage("f stop");
                sender.sendMessage("f load");
            } else {
                switch (args[0]) {
                    case "o": {
                        o = ((Player) sender).getLocation().getBlock().getLocation().clone();
                        sender.sendMessage("o");
                        break;
                    }
                    case "start": {
                        ScriptEngineManager manager = new ScriptEngineManager();
                        ScriptEngine engine = manager.getEngineByName("js");
                        sender.sendMessage("start");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                double x, y, z;
                                World world = o.getWorld();
                                for (double l = -var1; l < 30; l += li) {
                                    world.spawnParticle(p2, o.clone().add(l, 0, 0), n);
                                    world.spawnParticle(p2, o.clone().add(0, l, 0), n);
                                    world.spawnParticle(p2, o.clone().add(0, 0, l), n);
                                }
                                for (x = -var1; x < var1; x += var2 / level) {
                                    engine.put("x", x);
                                    try {
                                        y = (double) engine.eval(yF);
                                        engine.put("y", y);
                                        z = (double) engine.eval(zF);
                                    } catch (ScriptException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                    world.spawnParticle(p1, o.clone().add(x, y, z), n * 2);
                                }

                            }
                        }.runTaskTimer(this, 0, 5);
                        break;
                    }
                    case "stop": {
                        Bukkit.getScheduler().cancelTasks(this);
                        break;
                    }
                    case "load": {
                        try (BufferedReader in = Files.newBufferedReader(path)) {
                            yF = in.readLine();
                            zF = in.readLine();
                            var1 = Double.parseDouble(in.readLine());
                            level = Integer.parseInt(in.readLine());
                            sender.sendMessage("loaded");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onLoad() {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
