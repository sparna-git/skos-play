package fr.sparna.rdf.skosplay.log;

public enum Month {

    JANUARY("1", "Janvier"),
    FEBRUARY("2", "Février"),
    MARCH("3", "Mars"),
    APRIL("4", "Avril"),
    MAY("5", "Mai"),
    JUNE("6", "Juin"),
    JULY("7", "Juillet"),
    AUGUST("8", "Août"),
    SEPTEMBER("9", "Septembre"),
    OCTOBER("10", "Octobre"),
    NOVEMBER("11", "Novembre"),
    DECEMBER("12", "Décembre");
    
   
    protected String number;
    protected String label;
   
    private Month(String number, String label) {
        this.number = number;
        this.label = label;
    }

    public String getNumber() {
        return number;
    }

    public String getLabel() {
        return label;
    }
   
    public static Month fromNumber(String number) {
        Month[] values = Month.values();
        for (Month month : values) {
            if(month.getNumber().equals(number)) {
                return month;
            }
        }
        return null;
    }
}