package pl.suzume.xposed.reled;

public class LedConfig {
    public static class Colors {
	public static final Integer RED = 0xFFFF0000;
	public static final Integer GREEN = 0xFF00FF00;
	public static final Integer BLUE = 0xFF0000FF;
	public static final Integer WHITE = 0xFFFFFFFF;
	public static final Integer YELLOW = 0xFFFFFF00;
	public static final Integer ORANGE = 0xFFFF8000;
	public static final Integer MAGENTA = 0xFFFF00FF;
	public static final Integer CYAN = 0xFF00FFFF;
	public static final Integer NAVY = 0xFF000080;
	public static final Integer PURPLE = 0xFF990099;
	public static final Integer NONE = 0x00000000;
    }

    private static class Defaults {
	public static final Integer ON_TIME = Integer.valueOf(1000);
	public static final Integer OFF_TIME = Integer.valueOf(2000);
	public static final Integer COLOR = Colors.CYAN;
    }

    public static LedConfig getInstance() {
	return new LedConfig();
    }

    private Integer onTime;
    private Integer offTime;
    private Integer color;

    public LedConfig() {
	this.onTime = Defaults.ON_TIME;
	this.offTime = Defaults.OFF_TIME;
	this.color = Defaults.COLOR;
    }

    public LedConfig onTime(final Integer on) {
	setOnTime(on);
	return this;
    }

    public LedConfig offTime(final Integer off) {
	setOffTime(off);
	return this;
    }

    public LedConfig color(final Integer c) {
	setColor(c);
	return this;
    }

    public Integer getOnTime() {
	return this.onTime;
    }

    public void setOnTime(final Integer onTime) {
	this.onTime = onTime;
    }

    public Integer getOffTime() {
	return this.offTime;
    }

    public void setOffTime(final Integer offTime) {
	this.offTime = offTime;
    }

    public Integer getColor() {
	return this.color;
    }

    public void setColor(final Integer color) {
	this.color = color;
    }

}
