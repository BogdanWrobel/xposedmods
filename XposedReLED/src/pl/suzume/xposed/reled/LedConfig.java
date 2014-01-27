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

    private Integer onTime;
    private Integer offTime;
    private Integer color;

    public LedConfig(final Integer onTime, final Integer offTime, final Integer color) {
	this.onTime = onTime;
	this.offTime = offTime;
	this.color = color;
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
