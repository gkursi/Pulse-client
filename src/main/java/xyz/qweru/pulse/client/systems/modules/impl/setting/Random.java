package xyz.qweru.pulse.client.systems.modules.impl.setting;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.FractalType;
import personthecat.fastnoise.data.NoiseType;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;

public class Random extends ClientModule {

    public static ModeSetting mode = modeSetting()
            .name("Mode")
            .description("randomization mode")
            .defaultMode("None")
            .mode("Gaussian")
            .mode("Perlin")
            .mode("Pattern")
            .mode("None")
            .build();

    public static NumberSetting randomAmount = numberSetting()
            .name("Base amount")
            .description("randomization amount")
            .range(0, 100)
            .defaultValue(50)
            .stepFullNumbers()
            .build();

    public static NumberSetting randomDiff = numberSetting()
            .name("Random diff")
            .description("randomization difference")
            .range(0, 100)
            .defaultValue(25)
            .stepFullNumbers()
            .build();

    public static BooleanSetting randomBase = booleanSetting()
            .name("Randomize base")
            .description("Also apply randomization to the base value")
            .build();

    public static ModeSetting convertMode = modeSetting()
            .name("Convert mode")
            .description("how to convert base number")
            .defaultMode("Add")
            .mode("Multiply")
            .mode("Add")
            .build();

    public static NumberSetting hitPercentage = numberSetting()
            .name("Hit %")
            .description("how many percent of hits to hit")
            .range(0, 100)
            .defaultValue(100)
            .stepFullNumbers()
            .build();

    public Random() {
        builder()
                .name("Randomizer")
                .description("Randomization settings")
                .settings(mode, randomAmount, randomDiff, randomBase, convertMode, hitPercentage)
                .category(Category.SETTING);
    }

    double randomize(float n) {
        if(mode.is("Gaussian")) {
            return n * random.nextGaussian();
        } else if(mode.is("Perlin")) {
            FastNoise noise = FastNoise.builder()
                    .type(NoiseType.PERLIN)
                    .fractal(FractalType.FBM)
                    .frequency(0.5f)
                    .build();

            return noise.getNoise(n);
        } else if(mode.is("Pattern")) {
            double amplitude = randomDiff.getValueDouble(); // max deviation from the mean
            double frequency = 0.3d; // adjust frequency for longer or shorter patterns
            return (double) n + amplitude * Math.sin(frequency * System.currentTimeMillis());
        }
        return n;
    }

    static java.util.Random random = new java.util.Random(System.currentTimeMillis() + 69999);
    public static double getRandom(double n, RandomizerMode mode, boolean min0) {
        Random module = ((Random) Managers.MODULE.getItemByClass(Random.class));
        if(module.randomAmount.getValue() == 0 || module.randomDiff.getValue() == 0) return n;
        if(module.mode.is("None")) return n;

        double rand = module.randomize(randomBase.isEnabled() ? (float) module.randomize(randomAmount.getValue()) : randomAmount.getValue());
        double res = n;
        if(module.convertMode.is("Add")) {
            res = n + rand;
        } else if(module.convertMode.is("Multiply")) {
            res = rand;
        }

        if(min0) res = Math.max(0, res);

        return res;
    }

    public static enum RandomizerMode {
        /**
         * Return value can be both larger and smaller than given value
         */
        UP_AND_DOWN,
        /**
         * Return value can only be larger than given value
         */
        UP,
        /**
         * Return value can only be smaller than given value
         */
        DOWN
    }

}
