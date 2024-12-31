package xyz.qweru.pulse.client.systems.commands;

public class ClientCommand {

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private String name;
    private String description;

    public ClientCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void run(String[] args) {};
}
