package frc.robot;

public class Command {
    public String name;
    public String desc;
    public double AUTO_LAUNCH_DELAY_S;
    public double AUTO_DRIVE_DELAY_S;
    public double AUTO_DRIVE_TIME_S;
    public double AUTO_DRIVE_SPEED;
    public double AUTO_LAUNCHER_SPEED;
    public Command(String name, String desc, double launch_delay, double drive_delay, double drive_time, double drive_speed, double launcher_speed) {
        this.name = name;
        this.desc = desc;
        this.AUTO_LAUNCH_DELAY_S = launch_delay;
        this.AUTO_DRIVE_DELAY_S = drive_delay;
        this.AUTO_DRIVE_TIME_S = drive_time;
        this.AUTO_DRIVE_SPEED = drive_speed;
        this.AUTO_LAUNCHER_SPEED = launcher_speed;
        Robot.commands.add(this);
    }
}
