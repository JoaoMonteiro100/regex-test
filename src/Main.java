import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    //private static final Pattern p = Pattern.compile("^([a-zA-Z]+)([0-9]+)(.*)");
    private static List<Phrase> phrases = new ArrayList<>();
    private static final String numbers = "[0-9]+";
    private static final String timeVarsPT = "(minuto(s)?|hora(s)?|dia(s)?|semana(s)?|mês|meses|antes|depois|após|durante|enquanto)";
    private static final String eventVarsPT = "(refeiç(ão|ões)|pequeno(s)?-almoço(s)?|almoço(s)?|almo(ce|ça|çar)|lanche(s)?|jantar(es)?|jant(e|a|ar)|ceia(s)?|deit(e|ar)|durma|dormir|acord(e|ar)|levant(e|ar)|com(a|er)|comida)";
    private static final String numbersPT = "(" + numbers + "|um(a)?|dois|duas|três|quatro|cinco|seis|sete|oito|nove|dez|onze|doze|treze|catorze|quinze|dezasseis|dezassete|dezoito|dezanove|vinte)";
    private static final String timeVarsEN = "(minute(s)?|hour(s)?|day(s)?|week(s)?|month(s)?|before|after|during|while)";
    private static final String eventVarsEN = "(meal(s)?|breakfast(s)?|lunch(es)?|snack(s)?|dinner(s)?|supper(s)?|din(e|ing)|eat(ing)|(go(ing)? to )?sleep(ing)?|wak(e|ing)( up)?|(go(ing)? to )?bed|food)";
    private static final String numbersEN = "(" + numbers + "|once|one|twice|two|thrice|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty)";

    public static void main(String[] args) {
        generatePhrases("PT"); //options are "PT" or "EN"
        matchPhrase("tome antes de cada jantar"); //string should be all lowercase
    }

    private static void generatePhrases(String lang) {
        //order of the arguments:
        //PATTERN, NUMBER, TIME UNIT, NUMBER-PER-TIME, EVENT
        switch (lang) {
            case "PT":
                phrases.add(new Phrase(Pattern.compile("^(.*)" + timeVarsPT + " d(e|a|o)(s)? (tod(o|a)(s)? ((o|a)(s)? )?|cada )?" + eventVarsPT + "(.*)"),-1,2,false,15));
                phrases.add(new Phrase(Pattern.compile("^(.*)(a |em )?cada " + numbersPT + " " + timeVarsPT + "(.*)"),3,5,false,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)(a |em )?cada " + timeVarsPT + "(.*)"),-1,3,false,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)(a |em |com )?cada " + eventVarsPT + "(.*)"),-1,-1,false,3));
                phrases.add(new Phrase(Pattern.compile("^(.*)de " + numbersPT + " em " + numbersPT + " " + timeVarsPT + "(.*)"),4,6,false,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)" + numbersPT + "( vez(es)?)? por " + timeVarsPT + "(.*)"),2,6,true,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)por " + eventVarsPT + "(.*)"),-1,-1,false,2));
                phrases.add(new Phrase(Pattern.compile("^(.*)tod(os|as)( os| as)? " + timeVarsPT + "(.*)"),-1,4,false,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)tod(os|as)( os| as)? " + eventVarsPT + "(.*)"),-1,-1,false,4));
                phrases.add(new Phrase(Pattern.compile("^(.*)com ((o|a)(s)? )?" + eventVarsPT + "(.*)"),-1,-1,false,5));
                phrases.add(new Phrase(Pattern.compile("^(.*)(quando|sempre que|juntamente com)(.*)" + eventVarsPT + "(.*)"),-1,-1,false,4));
                phrases.add(new Phrase(Pattern.compile("^(.*)jejum|até|sempre que|juntamente com|ao mesmo tempo que(.*)"),-1,-1,false,-1));
                break;

            default: //it's "EN" by default
                phrases.add(new Phrase(Pattern.compile("(.*)every " + numbersEN + " " + timeVarsEN + "(.*)"),2,3,false,-1));
                phrases.add(new Phrase(Pattern.compile("(.*)" + numbersEN + " (times )?(each|every|a(n)?|per) " + timeVarsEN + "(.*)"),2,6,true,-1));
                phrases.add(new Phrase(Pattern.compile("(.*)(every|each) " + timeVarsEN + "(.*)"),-1,2,false,-1));
                phrases.add(new Phrase(Pattern.compile("(.*)" + timeVarsEN + " (each |every |all |a(n)? )?" + eventVarsEN + "(.*)"),-1,2,false,9));
                phrases.add(new Phrase(Pattern.compile("(.*)with (each |every |all |a(n)? )?" + eventVarsEN + "(.*)"),-1,-1,false,3));
                phrases.add(new Phrase(Pattern.compile("(.*)(when|every|together with)(.*)" + eventVarsEN + "(.*)"),-1,-1,false,4));
                phrases.add(new Phrase(Pattern.compile("(.*)fasting|until|every time|whenever|anytime|up to(.*)"),-1,-1,false,-1));
        }
    }

    private static void matchPhrase(String s) {
        Matcher m;

        for(Phrase p : phrases) {
            m = p.getP().matcher(s);
            if (m.find()) {
                System.out.println("(0): " + m.group(0)); // whole matched expression
                System.out.println("(1): " + m.group(1));
                System.out.println("(2): " + m.group(2));
                System.out.println("(3): " + m.group(3));
                System.out.println("(4): " + m.group(4));
                System.out.println("(5): " + m.group(5));
                System.out.println("(6): " + m.group(6));
                System.out.println("(7): " + m.group(7));
                System.out.println("(8): " + m.group(8));
                System.out.println("(9): " + m.group(9));
                System.out.println("(10): " + m.group(10));
                System.out.println("(11): " + m.group(11));
                System.out.println("(12): " + m.group(12));
                System.out.println("(13): " + m.group(13));
                System.out.println("(14): " + m.group(14));
                System.out.println("(15): " + m.group(15));
                break;
            }
        }
    }
}
