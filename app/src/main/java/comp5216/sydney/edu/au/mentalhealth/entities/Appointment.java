package comp5216.sydney.edu.au.mentalhealth.entities;

public class Appointment {
    public String professionalName;
    public String professionalJob;
    public String date;
    public String time;
    public String avatarUrl;
    public String userName;

    public Appointment() {
    }

    public Appointment(String professionalName, String professionalJob, String date, String time
            , String avatarUrl, String userName) {
        this.professionalName = professionalName;
        this.professionalJob = professionalJob;
        this.date = date;
        this.time = time;
        this.avatarUrl = avatarUrl;
        this.userName = userName;
    }

}

