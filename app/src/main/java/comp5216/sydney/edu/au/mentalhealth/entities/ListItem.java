package comp5216.sydney.edu.au.mentalhealth.entities;

public class ListItem {
    private int iconResId;
    private String title;
    private String subtitle;

    private String avatarUrl;

    public ListItem() {
    }

    public ListItem(String title, String subtitle, String avatarUrl) {
        this.title = title;
        this.subtitle = subtitle;
        this.avatarUrl = avatarUrl;
    }

    // Getter methods
    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
