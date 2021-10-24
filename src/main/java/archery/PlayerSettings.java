package archery;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

public class PlayerSettings {
    private int amount;
    private boolean fireOn;
    private int highscore;
    private boolean inFunMode;
    private boolean inPracticeMode;
    private Class<? extends Projectile> projectile;
    private boolean ridingOn;
    private int score;
    private long startTime;

    public PlayerSettings() {
        this.amount = 1;
        this.fireOn = this.ridingOn = this.inFunMode = this.inPracticeMode = false;
        this.highscore = 1000;
        this.projectile = Arrow.class;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isFireOn() {
        return fireOn;
    }

    public void setFireOn(boolean fireOn) {
        this.fireOn = fireOn;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public boolean isInFunMode() {
        return inFunMode;
    }

    public void setInFunMode(boolean inFunMode) {
        this.inFunMode = inFunMode;
    }

    public boolean isInPracticeMode() {
        return inPracticeMode;
    }

    public void setInPracticeMode(boolean inPracticeMode) {
        this.inPracticeMode = inPracticeMode;
    }

    public Class<? extends Projectile> getProjectile() {
        return projectile;
    }

    public void setProjectile(Class<? extends Projectile> projectile) {
        this.projectile = projectile;
    }

    public boolean isRidingOn() {
        return ridingOn;
    }

    public void setRidingOn(boolean riding) {
        this.ridingOn = riding;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}