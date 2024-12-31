package xyz.qweru.pulse.client.systems.modules;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    WORLD("World"),
    MISC("Misc"),
    HUD("Hud"),
    SETTING("Settings");


    public final String label;
    public boolean visible;
//    public static final List<Category> categoryList = new ArrayList<>();

    Category(String label) {
        this.visible = true;
        this.label = label;
//        this.categoryList
    }

}
