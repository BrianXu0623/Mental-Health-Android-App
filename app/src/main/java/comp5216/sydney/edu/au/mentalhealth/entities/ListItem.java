package comp5216.sydney.edu.au.mentalhealth.entities;

import com.google.firebase.firestore.PropertyName;

public class ListItem {
    private int iconResId;
    private String title;
    private String subtitle;

    public ListItem() {
    }

    public ListItem(int iconResId, String title, String subtitle) {
        this.iconResId = iconResId;
        this.title = title;
        this.subtitle = subtitle;
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

    // Setter methods
    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
