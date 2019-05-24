package ecoext.com.ecoext.general;

public class Utilities {

    /**
     * Set Purse Color
     * Take a parameter name which is the chart to be checked
     *
     * @param placeName
     * @return a String color
     */
    public static String setPurseColor(String placeName) {
        String l = placeName.toUpperCase();
        String name = String.valueOf(l.charAt(0));
        if (("A").equals(name) || ("B").equals(name) || ("C").equals(name) || ("D").equals(name) || ("E").equals(name) || ("P").equals(name)) {
            return "#1A535C";
        } else if (("F").equals(name) || ("G").equals(name) || ("H").equals(name) || ("I").equals(name) || ("K").equals(name) || ("Z").equals(name)) {
            return "#CC6352";
        } else {
            return "#9E4770";
        }
    }


    public static String setPurseColorbyPurseId(int id) {
        int theId = id % 4;
        switch (theId) {
            case 0:
                return "#9E4770";
            case 1:
                return "#1A535C";
            case 2:
                return "#CC6352";
            case 3:
                return "#3E885B";
        }
        return "#3E885";
    }


}
