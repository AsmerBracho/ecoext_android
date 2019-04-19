package ecoext.com.ecoext;

public class Utilities {

    /**
     * Set Purse Color
     * Take a parameter name which is the chart to be checked
     * @param name
     * @return a String color
     */
    public static String setPurseColor(String name) {
        if (("A").equals(name) || ("B").equals(name) || ("C").equals(name) || ("D").equals(name) || ("E").equals(name) || ("P").equals(name)) {
            return"#355ee4";
        } else if (("F").equals(name) || ("G").equals(name) || ("H").equals(name) || ("I").equals(name) || ("K").equals(name) || ("Z").equals(name)) {
            return "#e49c35";
        } else {
            return "#35e45e";
        }
    }


}
