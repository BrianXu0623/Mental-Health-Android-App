package comp5216.sydney.edu.au.mentalhealth.entities;

public class Appointment {
    public String professionalName;
    public String professionalJob;
    public String date;
    public String time;

    public Appointment() {
    }

    public Appointment(String professionalName, String professionalJob, String date, String time) {
        this.professionalName = professionalName;
        this.professionalJob = professionalJob;
        this.date = date;
        this.time = time;
    }

}

