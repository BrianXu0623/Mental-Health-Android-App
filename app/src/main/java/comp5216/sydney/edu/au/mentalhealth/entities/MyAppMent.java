package comp5216.sydney.edu.au.mentalhealth.entities;

public class MyAppMent {


    private boolean isProfessional = false;
    private String date;
    private String professionalJob;
    private String professionalName;
    private String time;




    public MyAppMent(String date, String professionalJob, String professionalName, String time) {
        this.date = date;
        this.professionalJob = professionalJob;
        this.professionalName = professionalName;
        this.time = time;

    }

    public boolean isProfessional() {
        return isProfessional;
    }

    public void setProfessional(boolean professional) {
        isProfessional = professional;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfessionalJob() {
        return professionalJob;
    }

    public void setProfessionalJob(String professionalJob) {
        this.professionalJob = professionalJob;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
