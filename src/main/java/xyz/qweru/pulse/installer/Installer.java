package xyz.qweru.pulse.installer;

import xyz.qweru.pulse.client.utils.Util;

import java.io.File;
import java.io.IOException;

public class Installer {

    static void info(String s) {
        System.out.println("[INFO] "+s);
    }

    static void warn(String s) {
        System.out.println("[INFO] "+s);
    }

    static void err(String s) {
        System.out.println("[ERROR] "+s);
    }

    static File getMinecraftDir() {
        String workingDirectory;
        String OS = (System.getProperty("os.name")).toUpperCase();
        if (OS.contains("WIN"))
        {
            workingDirectory = System.getenv("AppData") + "/.minecraft";
        }
        else
        {
            workingDirectory = System.getProperty("user.home") + "/.minecraft"; // fuck Mac users
        }

        return new File(workingDirectory);
    }

    public static void run() throws IOException {

//        Path currentRelativePath = Paths.get("");
//        String s = currentRelativePath.toAbsolutePath().toString();
//        info("Current path: " + s);
//        String fileName = new java.io.File(Installer.class.getProtectionDomain()
//                .getCodeSource()
//                .getLocation()
//                .getPath())
//                .getName();
//        info("Filename: " + fileName);
//        File thisFile = new File(currentRelativePath.toAbsolutePath() + "/" + fileName);
//        info("Full path: " + thisFile.getAbsolutePath());
//
//        File mcDir = getMinecraftDir();
//        info("Minecraft dir: " +mcDir.getAbsolutePath() + ", " +(mcDir.exists() ? "(is valid)" : "(is not valid)"));
//        if(mcDir.exists()) {
//            File modsDir = new File(mcDir.getAbsolutePath() + "/mods");
//            if(modsDir.exists()) {
//                Files.copy(thisFile.toPath(), new File(modsDir.getAbsolutePath() + "/" + PulseClient.NAME + "-" + PulseClient.VERSION).toPath(), StandardCopyOption.REPLACE_EXISTING);
//                Util.infoPopup("Successfully installed! (" + modsDir.getAbsolutePath() + ")", "Finished");
//            } else {
//                modsDir.mkdir();
//            }
//        } else {
//            Util.errorPopup("Cannot find Minecraft dir! To install this mod, please move it in to the mods folder.", "Cannot install!");
//        }

        Util.errorPopup("To install this mod, please move it in to the mods folder. ", "Error");
    }
}
