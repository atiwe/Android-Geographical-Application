package se.mau.ac8110.p2;

public class MemberInfo {
    private String name;
    private double longitude;
    private double latitude;

    MemberInfo(String name, double longitude, double latitude){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    double getLongitude() {
        return longitude;
    }

    double getLatitude() {
        return latitude;
    }
}
