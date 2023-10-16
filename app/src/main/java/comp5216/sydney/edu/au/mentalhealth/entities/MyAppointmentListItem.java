package comp5216.sydney.edu.au.mentalhealth.entities;

public class MyAppointmentListItem {
    private int iconResId;
    private String professionalName;
    private String professionalJob;

    private String avatarUrl;
    private String date;
    private String time;
    private String userName;

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }

    public String getProfessionalJob() {
        return professionalJob;
    }

    public void setProfessionalJob(String professionalJob) {
        this.professionalJob = professionalJob;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public MyAppointmentListItem() {
    }

    public MyAppointmentListItem(int iconResId, String professionalName, String professionalJob,
                                 String avatarUrl, String date, String time) {
        this.iconResId = iconResId;
        this.professionalName = professionalName;
        this.professionalJob = professionalJob;
        this.avatarUrl = avatarUrl;
        this.date = date;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
